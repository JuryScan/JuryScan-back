package unicap.juryscan.integrationTests.services.Wallet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import unicap.juryscan.enums.TipoUserEnum;
import unicap.juryscan.enums.UserStatusEnum;
import unicap.juryscan.model.User;
import unicap.juryscan.model.Wallet;
import unicap.juryscan.repository.UserRepository;
import unicap.juryscan.service.wallet.WalletService;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = { WalletService.class })
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = "unicap.juryscan.repository")
@EntityScan(basePackages = "unicap.juryscan.model")
@ActiveProfiles("test")
@Transactional

public class WalletServiceIntegrationTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserRepository userRepository;

    private User createTestUser(String email, String cpf) {
        User user = new User();
        user.setNomeCompleto("Test User");
        user.setEmail(email);
        user.setSenha("$2a$10$eCezIb33jQLbEk77AVRcyOq.7Kg2AECB6T7A1O.8VmsFw.C0zVee2");
        user.setCpf(cpf);
        user.setTipoUsuario(TipoUserEnum.COMUM);
        user.setStatus(UserStatusEnum.ATIVO);
        user.setEmailVerificado(true);
        user.setDataNascimento(Date.valueOf("2000-01-01"));
        return userRepository.save(user);
    }

    @Test
    void shouldCreateWallet() {
        User user = createTestUser("create@test.com", "11122233344");

        Wallet wallet = walletService.createWallet(user);

        assertNotNull(wallet);
        assertNotNull(wallet.getId());
        assertEquals(0, wallet.getSaldo());
        assertEquals(user.getId(), wallet.getUsuario().getId());
    }

    @Test
    void shouldAddCredits() {
        User user = createTestUser("deposit@test.com", "55566677788");
        walletService.createWallet(user);

        walletService.addCredits(user.getId(), 150);
        Wallet wallet = walletService.getWalletByUserId(user.getId());

        assertNotNull(wallet);
        assertEquals(150, wallet.getSaldo());
    }

    @Test
    void shouldDebitWallet() {
        User user = createTestUser("debit@test.com", "99900011122");
        walletService.createWallet(user);
        walletService.addCredits(user.getId(), 200);

        walletService.deductCredits(user.getId(), 50);
        Wallet wallet = walletService.getWalletByUserId(user.getId());

        assertEquals(150, wallet.getSaldo());
    }

    @Test
    void shouldThrowWhenInsufficientBalance() {
        User user = createTestUser("low@test.com", "44455566611");
        walletService.createWallet(user);
        walletService.addCredits(user.getId(), 30);

        assertThrows(IllegalStateException.class, () -> {
            walletService.deductCredits(user.getId(), 100);
        });
    }

    @Test
    void shouldFindWalletByUserId() {
        User user = createTestUser("find@test.com", "22233344499");
        Wallet created = walletService.createWallet(user);

        Wallet found = walletService.getWalletByUserId(user.getId());

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
    }
}