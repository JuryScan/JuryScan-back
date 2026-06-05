package unicap.juryscan.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import unicap.juryscan.dto.dashboard.DashboardMetricsDTO;
import unicap.juryscan.dto.dashboard.MonthlyCountDTO;
import unicap.juryscan.enums.StatusLeadEnum;
import unicap.juryscan.enums.TipoUserEnum;
import unicap.juryscan.exception.ResourceNotFoundException;
import unicap.juryscan.model.User;
import unicap.juryscan.model.Wallet;
import unicap.juryscan.repository.AnalysisRepository;
import unicap.juryscan.repository.FailureRepository;
import unicap.juryscan.repository.LeadRepository;
import unicap.juryscan.repository.UserRepository;
import unicap.juryscan.repository.WalletRepository;
import unicap.juryscan.service.dashboard.DashboardService;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class DashboardServiceTest {

    @Mock private LeadRepository leadRepository;
    @Mock private AnalysisRepository analysisRepository;
    @Mock private FailureRepository failureRepository;
    @Mock private WalletRepository walletRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private DashboardService dashboardService;

    private UUID advogadoId;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        advogadoId = UUID.randomUUID();
        User advogado = new User();
        advogado.setId(advogadoId);
        advogado.setTipoUsuario(TipoUserEnum.ADVOGADO);
        when(userRepository.findByTipoUsuarioAndId(TipoUserEnum.ADVOGADO, advogadoId))
                .thenReturn(Optional.of(advogado));
    }

    @Test
    void getAdvogadoMetrics_montaKpisESeriesComDadosReais() {
        Timestamp agora = Timestamp.valueOf(LocalDateTime.now());
        Timestamp foraDaJanela = Timestamp.valueOf(LocalDateTime.now().minusYears(2));

        when(leadRepository.countByAdvogadoIdAndStatus(advogadoId, StatusLeadEnum.ADQUIRIDO)).thenReturn(8L);
        when(leadRepository.countByAdvogadoAndStatusAndDataAquisicaoApos(eq(advogadoId), eq(StatusLeadEnum.ADQUIRIDO), any()))
                .thenReturn(3L);
        when(leadRepository.countByStatus(StatusLeadEnum.DISPONIVEL)).thenReturn(12L);
        when(leadRepository.countDistinctClientesByAdvogadoAndStatus(advogadoId, StatusLeadEnum.ADQUIRIDO)).thenReturn(4L);
        when(leadRepository.sumCustoCreditosByAdvogadoAndStatus(advogadoId, StatusLeadEnum.ADQUIRIDO)).thenReturn(80L);
        when(leadRepository.findDatasAquisicaoDesde(eq(advogadoId), eq(StatusLeadEnum.ADQUIRIDO), any()))
                .thenReturn(List.of(agora, agora, foraDaJanela));

        when(analysisRepository.countByUsuarioIdAndDataCriacaoAfter(eq(advogadoId), any())).thenReturn(5L);
        when(analysisRepository.countByUsuarioId(advogadoId)).thenReturn(20L);
        when(analysisRepository.findDatasCriacaoDesde(eq(advogadoId), any())).thenReturn(List.of(agora));

        when(failureRepository.countByAnaliseUsuarioId(advogadoId)).thenReturn(15L);
        when(failureRepository.findDatasAnaliseDesde(eq(advogadoId), any())).thenReturn(List.of(agora, agora, agora));

        Wallet carteira = new Wallet();
        carteira.setSaldo(150);
        when(walletRepository.findByUsuarioId(advogadoId)).thenReturn(Optional.of(carteira));

        DashboardMetricsDTO dto = dashboardService.getAdvogadoMetrics(advogadoId);

        assertEquals(8L, dto.getLeadsAdquiridos());
        assertEquals(3L, dto.getLeadsAdquiridosNoMes());
        assertEquals(12L, dto.getLeadsDisponiveis());
        assertEquals(4L, dto.getClientesAtivos());
        assertEquals(80L, dto.getTotalGastoEmCreditos());
        assertEquals(5L, dto.getAnalisesNoMes());
        assertEquals(20L, dto.getAnalisesTotais());
        assertEquals(15L, dto.getTotalErros());
        assertEquals(150, dto.getSaldoCreditos());

        assertNotNull(dto.getTaxaConversao());
        assertEquals(4.0 / 8.0, dto.getTaxaConversao(), 1e-9);

        // Series sempre com 6 meses; o item fora da janela e ignorado; mes corrente fica no ultimo bucket
        assertEquals(6, dto.getLeadsPorMes().size());
        assertEquals(6, dto.getAnalisesPorMes().size());
        assertEquals(6, dto.getErrosPorMes().size());

        YearMonth mesCorrente = YearMonth.now();
        MonthlyCountDTO ultimoLead = dto.getLeadsPorMes().get(5);
        assertEquals(mesCorrente.getMonthValue(), ultimoLead.getMes());
        assertEquals(mesCorrente.getYear(), ultimoLead.getAno());
        assertEquals(2L, ultimoLead.getCount()); // os 2 "agora"; o foraDaJanela e descartado
        assertEquals(2L, dto.getLeadsPorMes().stream().mapToLong(MonthlyCountDTO::getCount).sum());
        assertEquals(3L, dto.getErrosPorMes().get(5).getCount());
    }

    @Test
    void getAdvogadoMetrics_semLeads_taxaConversaoNulaESaldoZero() {
        when(leadRepository.countByAdvogadoIdAndStatus(advogadoId, StatusLeadEnum.ADQUIRIDO)).thenReturn(0L);
        when(leadRepository.countByAdvogadoAndStatusAndDataAquisicaoApos(eq(advogadoId), eq(StatusLeadEnum.ADQUIRIDO), any()))
                .thenReturn(0L);
        when(leadRepository.countByStatus(StatusLeadEnum.DISPONIVEL)).thenReturn(0L);
        when(leadRepository.countDistinctClientesByAdvogadoAndStatus(advogadoId, StatusLeadEnum.ADQUIRIDO)).thenReturn(0L);
        when(leadRepository.sumCustoCreditosByAdvogadoAndStatus(advogadoId, StatusLeadEnum.ADQUIRIDO)).thenReturn(0L);
        when(leadRepository.findDatasAquisicaoDesde(eq(advogadoId), eq(StatusLeadEnum.ADQUIRIDO), any())).thenReturn(List.of());
        when(analysisRepository.countByUsuarioIdAndDataCriacaoAfter(eq(advogadoId), any())).thenReturn(0L);
        when(analysisRepository.countByUsuarioId(advogadoId)).thenReturn(0L);
        when(analysisRepository.findDatasCriacaoDesde(eq(advogadoId), any())).thenReturn(List.of());
        when(failureRepository.countByAnaliseUsuarioId(advogadoId)).thenReturn(0L);
        when(failureRepository.findDatasAnaliseDesde(eq(advogadoId), any())).thenReturn(List.of());
        when(walletRepository.findByUsuarioId(advogadoId)).thenReturn(Optional.empty());

        DashboardMetricsDTO dto = dashboardService.getAdvogadoMetrics(advogadoId);

        assertNull(dto.getTaxaConversao());
        assertEquals(0, dto.getSaldoCreditos());
        assertEquals(6, dto.getLeadsPorMes().size());
        assertEquals(0L, dto.getLeadsPorMes().stream().mapToLong(MonthlyCountDTO::getCount).sum());
    }

    @Test
    void getAdvogadoMetrics_idInexistente_lancaResourceNotFound() {
        UUID inexistente = UUID.randomUUID();
        when(userRepository.findByTipoUsuarioAndId(TipoUserEnum.ADVOGADO, inexistente)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> dashboardService.getAdvogadoMetrics(inexistente));
    }
}
