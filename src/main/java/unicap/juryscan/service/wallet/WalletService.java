package unicap.juryscan.service.wallet;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unicap.juryscan.exception.ResourceNotFoundException;
import unicap.juryscan.model.User;
import unicap.juryscan.model.Wallet;
import unicap.juryscan.repository.UserRepository;
import unicap.juryscan.repository.WalletRepository;

import java.util.UUID;

@Service
public class WalletService implements IWalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public WalletService(WalletRepository walletRepository, UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    public Wallet createWallet(User user) {
        Wallet wallet = new Wallet();
        wallet.setUsuario(user);
        wallet.setSaldo(0);
        return walletRepository.save(wallet);
    }

    public Wallet getWalletByUserId(UUID userId) {
        return walletRepository.findByUsuarioId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Carteira não encontrada para o usuário: " + userId));
    }

    @Transactional
    public Wallet addCredits(UUID userId, Integer credits) {
        Wallet wallet = getWalletByUserId(userId);
        wallet.setSaldo(wallet.getSaldo() + credits);
        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet deductCredits(UUID userId, Integer credits) {
        Wallet wallet = getWalletByUserId(userId);

        if (wallet.getSaldo() < credits) {
            throw new IllegalStateException("Saldo insuficiente. Saldo atual: " + wallet.getSaldo() + ", créditos necessários: " + credits);
        }

        wallet.setSaldo(wallet.getSaldo() - credits);
        return walletRepository.save(wallet);
    }

    public Integer getBalance(UUID userId) {
        return getWalletByUserId(userId).getSaldo();
    }
}
