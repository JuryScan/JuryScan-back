package unicap.juryscan.repositories;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import unicap.juryscan.enums.StatusLeadEnum;
import unicap.juryscan.enums.TipoTransacaoEnum;
import unicap.juryscan.enums.TipoUserEnum;
import unicap.juryscan.enums.UserStatusEnum;
import unicap.juryscan.model.Analysis;
import unicap.juryscan.model.Failure;
import unicap.juryscan.model.Lead;
import unicap.juryscan.model.Transaction;
import unicap.juryscan.model.User;
import unicap.juryscan.repository.AnalysisRepository;
import unicap.juryscan.repository.FailureRepository;
import unicap.juryscan.repository.LeadRepository;
import unicap.juryscan.repository.TransactionRepository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class DashboardRepositoryTest {

    @Autowired
    private LeadRepository leadRepository;
    @Autowired
    private AnalysisRepository analysisRepository;
    @Autowired
    private FailureRepository failureRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private EntityManager em;

    private User advogado;
    private User cliente1;
    private User cliente2;

    @BeforeEach
    void seed() {
        advogado = persistUser(TipoUserEnum.ADVOGADO);
        cliente1 = persistUser(TipoUserEnum.COMUM);
        cliente2 = persistUser(TipoUserEnum.COMUM);

        Analysis a1 = persistAnalysis(cliente1);
        Analysis a2 = persistAnalysis(cliente2);
        Analysis a3 = persistAnalysis(cliente1);
        persistAnalysis(advogado); // análise do próprio advogado

        // 2 falhas em a1, 1 em a2
        persistFailure(a1);
        persistFailure(a1);
        persistFailure(a2);

        // 2 leads adquiridos pelo advogado (clientes distintos), 1 disponível
        persistLead(cliente1, a1, advogado, StatusLeadEnum.ADQUIRIDO);
        persistLead(cliente2, a2, advogado, StatusLeadEnum.ADQUIRIDO);
        persistLead(cliente1, a3, null, StatusLeadEnum.DISPONIVEL);

        // gasto em leads = 20; uma compra que não deve entrar no somatório
        persistTransaction(advogado, TipoTransacaoEnum.AQUISICAO_LEAD, 10);
        persistTransaction(advogado, TipoTransacaoEnum.AQUISICAO_LEAD, 10);
        persistTransaction(advogado, TipoTransacaoEnum.COMPRA, 50);

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("Conta clientes distintos a partir dos leads adquiridos pelo advogado")
    void countDistinctClientes() {
        assertThat(leadRepository.countDistinctClientesByAdvogado(advogado.getId(), StatusLeadEnum.ADQUIRIDO))
                .isEqualTo(2);
    }

    @Test
    @DisplayName("Conta leads por status/advogado e disponíveis no marketplace")
    void countLeads() {
        assertThat(leadRepository.countByAdvogadoIdAndStatus(advogado.getId(), StatusLeadEnum.ADQUIRIDO))
                .isEqualTo(2);
        assertThat(leadRepository.countByStatus(StatusLeadEnum.DISPONIVEL)).isEqualTo(1);
    }

    @Test
    @DisplayName("Conta falhas das análises por trás dos leads adquiridos")
    void countFailuresOfAcquiredLeads() {
        assertThat(failureRepository.countByAdvogadoAcquiredLeads(advogado.getId(), StatusLeadEnum.ADQUIRIDO))
                .isEqualTo(3);
    }

    @Test
    @DisplayName("Soma apenas as transações de aquisição de lead")
    void sumGastoEmLeads() {
        assertThat(transactionRepository.sumQuantidadeByUsuarioAndTipo(advogado.getId(), TipoTransacaoEnum.AQUISICAO_LEAD))
                .isEqualTo(20);
    }

    @Test
    @DisplayName("Conta apenas as análises do próprio advogado")
    void countAnalisesDoAdvogado() {
        assertThat(analysisRepository.countByUsuarioId(advogado.getId())).isEqualTo(1);
    }

    @Test
    @DisplayName("Retorna as datas de aquisição dos leads adquiridos dentro da janela")
    void findAcquisitionDatesSince() {
        Timestamp since = Timestamp.valueOf(YearMonth.now().minusMonths(5).atDay(1).atStartOfDay());
        assertThat(leadRepository.findAcquisitionDatesSince(advogado.getId(), StatusLeadEnum.ADQUIRIDO, since))
                .hasSize(2);
    }

    // ===== helpers =====

    private User persistUser(TipoUserEnum tipo) {
        User u = new User();
        u.setTipoUsuario(tipo);
        u.setNomeCompleto("User " + tipo);
        u.setEmail(UUID.randomUUID() + "@test.com");
        u.setSenha("x");
        u.setStatus(UserStatusEnum.ATIVO);
        em.persist(u);
        return u;
    }

    private Analysis persistAnalysis(User owner) {
        Analysis a = new Analysis();
        a.setTitulo("Análise");
        a.setUsuario(owner);
        em.persist(a);
        return a;
    }

    private void persistFailure(Analysis analise) {
        Failure f = new Failure();
        f.setTitulo("Falha");
        f.setAnalise(analise);
        em.persist(f);
    }

    private void persistLead(User cliente, Analysis analise, User adv, StatusLeadEnum status) {
        Lead l = new Lead();
        l.setUsuarioCliente(cliente);
        l.setAnalise(analise);
        l.setAdvogado(adv);
        l.setStatus(status);
        l.setCustoCreditos(10);
        if (status == StatusLeadEnum.ADQUIRIDO) {
            l.setDataAquisicao(Timestamp.valueOf(LocalDate.now().withDayOfMonth(1).atStartOfDay()));
        }
        em.persist(l);
    }

    private void persistTransaction(User usuario, TipoTransacaoEnum tipo, int quantidade) {
        Transaction t = new Transaction();
        t.setUsuario(usuario);
        t.setTipoTransacao(tipo);
        t.setQuantidade(quantidade);
        em.persist(t);
    }
}
