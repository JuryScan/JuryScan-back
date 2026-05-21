package unicap.juryscan.integrationTests.services.Transaction;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;

import unicap.juryscan.dto.pagination.PageResponse;
import unicap.juryscan.enums.TipoUserEnum;
import unicap.juryscan.enums.TipoTransacaoEnum;
import unicap.juryscan.enums.UserStatusEnum;
import unicap.juryscan.model.User;
import unicap.juryscan.model.Transaction;
import unicap.juryscan.repository.UserRepository;
import unicap.juryscan.repository.TransactionRepository;
import unicap.juryscan.service.transaction.TransactionService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = { TransactionService.class })
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = "unicap.juryscan.repository")
@EntityScan(basePackages = "unicap.juryscan.model")
@ActiveProfiles("test")
@Transactional
public class TransactionServiceIntegrationTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private User createTestUser(String email, String cpf) {
        User user = new User();
        user.setNomeCompleto("Transaction Test User");
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
    void shouldGetTransactionsByUserIdWithPagination() {
        User user = createTestUser("tx@test.com", "11122233344");

        Transaction tx1 = new Transaction();
        tx1.setUsuario(user);
        tx1.setTipoTransacao(TipoTransacaoEnum.COMPRA);
        tx1.setQuantidade(50);
        tx1.setStripeCheckoutId("cs_1");
        transactionRepository.save(tx1);

        Transaction tx2 = new Transaction();
        tx2.setUsuario(user);
        tx2.setTipoTransacao(TipoTransacaoEnum.COMPRA);
        tx2.setQuantidade(20);
        tx2.setStripeCheckoutId("cs_2");
        transactionRepository.save(tx2);

        Pageable pageable = PageRequest.of(0, 10);

        PageResponse<Transaction> response = transactionService.getTransactionsByUserId(pageable, user.getId());

        assertNotNull(response);
        assertEquals(2, response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertEquals(0, response.getPage());
        assertEquals(2, response.getItems().size());
        assertEquals(10, response.getPageSize());
    }

    @Test
    void shouldReturnEmptyPageWhenUserHasNoTransactions() {
        User user = createTestUser("notx@test.com", "55566677788");
        Pageable pageable = PageRequest.of(0, 10);

        PageResponse<Transaction> response = transactionService.getTransactionsByUserId(pageable, user.getId());

        assertNotNull(response);
        assertEquals(0, response.getTotalElements());
        assertTrue(response.getItems().isEmpty());
    }
}