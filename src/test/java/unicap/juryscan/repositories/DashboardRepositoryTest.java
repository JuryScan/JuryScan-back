package unicap.juryscan.repositories;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import unicap.juryscan.enums.StatusLeadEnum;
import unicap.juryscan.enums.TipoUserEnum;
import unicap.juryscan.model.Analysis;
import unicap.juryscan.model.Failure;
import unicap.juryscan.model.Lead;
import unicap.juryscan.model.User;
import unicap.juryscan.repository.AnalysisRepository;
import unicap.juryscan.repository.FailureRepository;
import unicap.juryscan.repository.LeadRepository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Garante que as @Query agregadas usadas pelo DashboardService sao validas e executam em H2,
 * cobrindo as travessias aninhadas (Lead->cliente, Falha->analise->usuario) e o COALESCE da soma.
 */
@DataJpaTest
@ActiveProfiles("test")
public class DashboardRepositoryTest {

    @Autowired private LeadRepository leadRepository;
    @Autowired private AnalysisRepository analysisRepository;
    @Autowired private FailureRepository failureRepository;
    @Autowired private EntityManager entityManager;

    private User advogado;
    private final Timestamp inicioJanela = Timestamp.valueOf(LocalDate.now().minusMonths(6).atStartOfDay());
    private final Timestamp inicioDoMes = Timestamp.valueOf(LocalDate.now().withDayOfMonth(1).atStartOfDay());

    @BeforeEach
    void seed() {
        advogado = novoUsuario(TipoUserEnum.ADVOGADO);
        User cliente = novoUsuario(TipoUserEnum.COMUM);

        Analysis analise = new Analysis();
        analise.setTitulo("Analise CNIS");
        analise.setUsuario(advogado);
        entityManager.persist(analise);

        Failure falha = new Failure();
        falha.setTitulo("Vinculo faltante");
        falha.setAnalise(analise);
        entityManager.persist(falha);

        Lead lead = new Lead();
        lead.setUsuarioCliente(cliente);
        lead.setAdvogado(advogado);
        lead.setAnalise(analise);
        lead.setStatus(StatusLeadEnum.ADQUIRIDO);
        lead.setCustoCreditos(30);
        lead.setDataAquisicao(Timestamp.valueOf(LocalDateTime.now()));
        entityManager.persist(lead);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void leadQueries() {
        assertThat(leadRepository.countByAdvogadoAndStatusAndDataAquisicaoApos(
                advogado.getId(), StatusLeadEnum.ADQUIRIDO, inicioDoMes)).isEqualTo(1L);
        assertThat(leadRepository.countDistinctClientesByAdvogadoAndStatus(
                advogado.getId(), StatusLeadEnum.ADQUIRIDO)).isEqualTo(1L);
        assertThat(leadRepository.sumCustoCreditosByAdvogadoAndStatus(
                advogado.getId(), StatusLeadEnum.ADQUIRIDO)).isEqualTo(30L);
        List<Timestamp> datas = leadRepository.findDatasAquisicaoDesde(
                advogado.getId(), StatusLeadEnum.ADQUIRIDO, inicioJanela);
        assertThat(datas).hasSize(1);
    }

    @Test
    void analysisQueries() {
        assertThat(analysisRepository.countByUsuarioId(advogado.getId())).isEqualTo(1L);
        assertThat(analysisRepository.findDatasCriacaoDesde(advogado.getId(), inicioJanela)).hasSize(1);
    }

    @Test
    void failureQueries() {
        assertThat(failureRepository.countByAnaliseUsuarioId(advogado.getId())).isEqualTo(1L);
        assertThat(failureRepository.findDatasAnaliseDesde(advogado.getId(), inicioJanela)).hasSize(1);
    }

    @Test
    void somaDeCreditosSemLeads_retornaZeroEvitandoNull() {
        User advogadoSemLeads = novoUsuario(TipoUserEnum.ADVOGADO);
        entityManager.flush();
        assertThat(leadRepository.sumCustoCreditosByAdvogadoAndStatus(
                advogadoSemLeads.getId(), StatusLeadEnum.ADQUIRIDO)).isEqualTo(0L);
    }

    private User novoUsuario(TipoUserEnum tipo) {
        User u = new User();
        u.setTipoUsuario(tipo);
        u.setNomeCompleto(tipo == TipoUserEnum.ADVOGADO ? "Dra. Teste" : "Cliente Teste");
        u.setEmail(tipo.name().toLowerCase() + "-" + System.nanoTime() + "@test.com");
        entityManager.persist(u);
        return u;
    }
}
