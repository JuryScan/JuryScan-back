package unicap.juryscan.services;

import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.Optional;
import java.util.UUID;

import unicap.juryscan.enums.TipoUserEnum;
import unicap.juryscan.enums.UserStatusEnum;
import unicap.juryscan.model.User;
import unicap.juryscan.model.Wallet;
import unicap.juryscan.repository.UserRepository;
import unicap.juryscan.repository.TransactionRepository;
import unicap.juryscan.service.wallet.WalletService;



import unicap.juryscan.service.webhook.StripeWebhookService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class StripeWebhookServiceIntegrationTest {

    @Autowired
    private StripeWebhookService stripeWebhookService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private TransactionRepository transactionRepository;

    private User createTestUser(String email, String cpf) {
        User user = new User();
        user.setNomeCompleto("Webhook Test User");
        user.setEmail(email);
        user.setSenha("$2a$10$eCezIb33jQLbEk77AVRcyOq.7Kg2AECB6T7A1O.8VmsFw.C0zVee2");
        user.setCpf(cpf);
        user.setTipoUsuario(TipoUserEnum.COMUM);
        user.setStatus(UserStatusEnum.ATIVO);
        user.setEmailVerificado(true);
        user.setDataNascimento(Date.valueOf("2000-01-01"));
        return userRepository.save(user);
    }

    private Event createMockEvent(String sessionId, UUID userId, Long amount) {
        Session mockSession = Mockito.mock(Session.class);
        Mockito.when(mockSession.getId()).thenReturn(sessionId);
        Mockito.when(mockSession.getClientReferenceId()).thenReturn(userId.toString());
        Mockito.when(mockSession.getAmountTotal()).thenReturn(amount);

        EventDataObjectDeserializer mockDeserializer = Mockito.mock(EventDataObjectDeserializer.class);
        Mockito.when(mockDeserializer.getObject()).thenReturn(Optional.of(mockSession));

        Event mockEvent = Mockito.mock(Event.class);
        Mockito.when(mockEvent.getDataObjectDeserializer()).thenReturn(mockDeserializer);

        return mockEvent;
    }

    @Test
    void shouldHandleCheckoutSessionCompleted() {
        User user = createTestUser("webhook@test.com", "12345678901");
        walletService.createWallet(user);
        String stripeSessionId = "cs_test_" + UUID.randomUUID();
        Event mockEvent = createMockEvent(stripeSessionId, user.getId(), 1000L);

        stripeWebhookService.handleCheckoutSessionCompleted(mockEvent);

        Wallet wallet = walletService.getWalletByUserId(user.getId());
        assertTrue(wallet.getSaldo() > 0);
        assertTrue(transactionRepository.findByStripeCheckoutId(stripeSessionId).isPresent());
    }

    @Test
    void shouldNotProcessDuplicateWebhook() {
        User user = createTestUser("idempotency@test.com", "98765432100");
        walletService.createWallet(user);
        String stripeSessionId = "cs_duplicate_123";
        Event mockEvent = createMockEvent(stripeSessionId, user.getId(), 1000L);

        stripeWebhookService.handleCheckoutSessionCompleted(mockEvent);
        int firstCallBalance = walletService.getWalletByUserId(user.getId()).getSaldo();

        stripeWebhookService.handleCheckoutSessionCompleted(mockEvent);
        int secondCallBalance = walletService.getWalletByUserId(user.getId()).getSaldo();

        assertEquals(firstCallBalance, secondCallBalance);
    }
}