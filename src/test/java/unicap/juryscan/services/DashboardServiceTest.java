package unicap.juryscan.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import unicap.juryscan.dto.dashboard.DashboardAdvogadoResponseDTO;
import unicap.juryscan.dto.dashboard.MonthlyCountDTO;
import unicap.juryscan.enums.StatusLeadEnum;
import unicap.juryscan.enums.TipoTransacaoEnum;
import unicap.juryscan.repository.AnalysisRepository;
import unicap.juryscan.repository.FailureRepository;
import unicap.juryscan.repository.LeadRepository;
import unicap.juryscan.repository.TransactionRepository;
import unicap.juryscan.service.dashboard.DashboardService;
import unicap.juryscan.service.wallet.IWalletService;

import java.sql.Timestamp;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class DashboardServiceTest {

    @Mock
    private LeadRepository leadRepository;
    @Mock
    private AnalysisRepository analysisRepository;
    @Mock
    private FailureRepository failureRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private IWalletService walletService;

    @InjectMocks
    private DashboardService dashboardService;

    private final UUID advId = UUID.randomUUID();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private static Timestamp ts(YearMonth ym) {
        return Timestamp.valueOf(ym.atDay(15).atStartOfDay());
    }

    @Test
    void deveMapearKpisEMontarSeriesDeSeisMeses() {
        YearMonth now = YearMonth.now();
        YearMonth doisMesesAtras = now.minusMonths(2);
        YearMonth foraDaJanela = now.minusMonths(7); // deve ser ignorado

        // KPIs
        when(leadRepository.countDistinctClientesByAdvogado(advId, StatusLeadEnum.ADQUIRIDO)).thenReturn(3L);
        when(leadRepository.countByAdvogadoIdAndStatus(advId, StatusLeadEnum.ADQUIRIDO)).thenReturn(4L);
        when(leadRepository.countByAdvogadoIdAndStatusAndDataAquisicaoGreaterThanEqual(
                eq(advId), eq(StatusLeadEnum.ADQUIRIDO), any(Timestamp.class))).thenReturn(2L);
        when(leadRepository.countByStatus(StatusLeadEnum.DISPONIVEL)).thenReturn(10L);
        when(analysisRepository.countByUsuarioId(advId)).thenReturn(7L);
        when(analysisRepository.countByUsuarioIdAndDataCriacaoGreaterThanEqual(eq(advId), any(Timestamp.class)))
                .thenReturn(5L);
        when(transactionRepository.sumQuantidadeByUsuarioAndTipo(advId, TipoTransacaoEnum.AQUISICAO_LEAD))
                .thenReturn(40L);
        when(failureRepository.countByAdvogadoAcquiredLeads(advId, StatusLeadEnum.ADQUIRIDO)).thenReturn(12L);
        when(walletService.getBalance(advId)).thenReturn(50);

        // Séries
        when(leadRepository.findAcquisitionDatesSince(eq(advId), eq(StatusLeadEnum.ADQUIRIDO), any(Timestamp.class)))
                .thenReturn(List.of(ts(now), ts(now), ts(doisMesesAtras), ts(foraDaJanela)));
        when(analysisRepository.findAnalysisDatesSince(eq(advId), any(Timestamp.class)))
                .thenReturn(List.of());
        when(failureRepository.findFailureAnalysisDatesSince(eq(advId), eq(StatusLeadEnum.ADQUIRIDO), any(Timestamp.class)))
                .thenReturn(List.of(ts(now)));

        DashboardAdvogadoResponseDTO dto = dashboardService.getAdvogadoDashboard(advId);

        // KPIs
        assertEquals(advId, dto.getAdvogadoId());
        assertEquals(3L, dto.getClientesAtivos());
        assertEquals(4L, dto.getLeadsAdquiridosTotais());
        assertEquals(2L, dto.getLeadsAdquiridosNoMes());
        assertEquals(10L, dto.getLeadsDisponiveis());
        assertEquals(7L, dto.getAnalisesTotais());
        assertEquals(5L, dto.getAnalisesNoMes());
        assertEquals(40L, dto.getTotalGastoEmLeads());
        assertEquals(12L, dto.getTotalErros());
        assertEquals(50, dto.getSaldoTokens());
        assertEquals(0.75, dto.getTaxaConversao(), 0.0001); // 3 / 4

        // Série de leads: 6 buckets, mais antigo -> mais recente
        List<MonthlyCountDTO> leads = dto.getLeadsPorMes();
        assertEquals(6, leads.size());
        assertEquals(now.minusMonths(5).getMonthValue(), leads.get(0).getMes());
        assertEquals(now.getYear(), leads.get(5).getAno());
        assertEquals(now.getMonthValue(), leads.get(5).getMes());
        assertEquals(2L, leads.get(5).getCount());                 // mês atual: 2
        assertEquals(1L, leads.get(3).getCount());                 // 2 meses atrás: 1
        assertEquals("Jan".length(), leads.get(0).getLabel().length()); // label pt-BR de 3 letras
        // Total contado exclui o timestamp fora da janela de 6 meses
        long totalLeads = leads.stream().mapToLong(MonthlyCountDTO::getCount).sum();
        assertEquals(3L, totalLeads);

        // Série de análises: tudo zero (lista vazia), mas ainda 6 buckets
        assertEquals(6, dto.getAnalisesPorMes().size());
        assertEquals(0L, dto.getAnalisesPorMes().stream().mapToLong(MonthlyCountDTO::getCount).sum());

        // Série de erros: 1 no mês atual
        assertEquals(1L, dto.getErrosPorMes().get(5).getCount());
    }

    @Test
    void taxaConversaoDeveSerNulaSemLeadsAdquiridos() {
        // Sem stubs de contagem: todos os long retornam 0 por padrão (incl. leadsAdquiridosTotais=0)
        DashboardAdvogadoResponseDTO dto = dashboardService.getAdvogadoDashboard(advId);

        assertNull(dto.getTaxaConversao());
        assertEquals(0L, dto.getLeadsAdquiridosTotais());
        assertEquals(0, dto.getSaldoTokens()); // carteira mockada retorna null -> 0
        assertEquals(6, dto.getLeadsPorMes().size());
        assertEquals(0L, dto.getLeadsPorMes().stream().mapToLong(MonthlyCountDTO::getCount).sum());
    }
}
