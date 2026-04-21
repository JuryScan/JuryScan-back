package unicap.juryscan.service.webhook;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unicap.juryscan.config.TokenPricingConfig;
import unicap.juryscan.enums.TipoTransacaoEnum;
import unicap.juryscan.exception.ResourceNotFoundException;
import unicap.juryscan.model.Transaction;
import unicap.juryscan.model.User;
import unicap.juryscan.repository.TransactionRepository;
import unicap.juryscan.repository.UserRepository;
import unicap.juryscan.service.wallet.WalletService;

import java.util.UUID;

@Service
public class StripeWebhookService implements IStripeWebhookService{

    @Value("${stripe.webhook.secret:}")
    private String webhookSecret;

    private final WalletService walletService;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public StripeWebhookService(WalletService walletService, TransactionRepository transactionRepository, UserRepository userRepository) {
        this.walletService = walletService;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public Event constructEvent(String payload, String sigHeader) throws SignatureVerificationException {

        if (webhookSecret != null && !webhookSecret.isEmpty()) {
            // Valida assinatura - exceção tratada no GlobalExceptionHandler
            return Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } else {
            // Se não houver secret configurado (dev/test), apenas parseia o evento
            return Event.GSON.fromJson(payload, Event.class);
        }
    }

    @Transactional
    public void handleCheckoutSessionCompleted(Event event) {
        Session session = (Session) event
                .getDataObjectDeserializer()
                .getObject()
                .orElseThrow(() -> new RuntimeException("Sessão do checkout não encontrada no evento"));

        // Verificar se a transação já foi processada (idempotência)
        if (transactionRepository.findByStripeCheckoutId(session.getId()).isPresent()) return;
        // Obter o ID do usuário do clientReferenceId
        String userIdString = session.getClientReferenceId();
        if (userIdString == null) throw new RuntimeException("clientReferenceId não encontrado na sessão");

        UUID userId = UUID.fromString(userIdString);
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário com id não encontrado com id: " + userId));

        // Obter o valor pago em centavos
        Long amountPaidInCents = session.getAmountTotal();
        // Calcular quantidade de tokens baseado no valor pago
        // Exemplo: R$ 10,00 (1000 centavos) ÷ R$ 0,20 (20 centavos/token) = 50 tokens
        Integer tokens = TokenPricingConfig.calculateTokens(amountPaidInCents);

        // Incrementar tokens na carteira
        walletService.addCredits(userId, tokens);

        // Registrar a transação
        Transaction transaction = new Transaction();
        transaction.setUsuario(user);
        transaction.setTipoTransacao(TipoTransacaoEnum.COMPRA);
        // Quantidade de TOKENS, não valor em centavos
        transaction.setQuantidade(tokens);
        transaction.setStripeCheckoutId(session.getId());
        transactionRepository.save(transaction);
    }
}
