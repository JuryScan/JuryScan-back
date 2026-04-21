package unicap.juryscan.service.wallet;

import unicap.juryscan.model.User;
import unicap.juryscan.model.Wallet;

import java.util.UUID;

public interface IWalletService {
    Wallet createWallet(User user);
    Wallet getWalletByUserId(UUID userId);
    Wallet addCredits(UUID userId, Integer credits);
    Wallet deductCredits(UUID userId, Integer credits);
    Integer getBalance(UUID userId);
}
