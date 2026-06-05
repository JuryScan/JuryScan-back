package unicap.juryscan.service.dashboard;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unicap.juryscan.dto.dashboard.DashboardAdvogadoResponseDTO;
import unicap.juryscan.dto.dashboard.MonthlyCountDTO;
import unicap.juryscan.enums.StatusLeadEnum;
import unicap.juryscan.enums.TipoTransacaoEnum;
import unicap.juryscan.repository.AnalysisRepository;
import unicap.juryscan.repository.FailureRepository;
import unicap.juryscan.repository.LeadRepository;
import unicap.juryscan.repository.TransactionRepository;
import unicap.juryscan.service.wallet.IWalletService;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DashboardService implements IDashboardService {

    private static final String[] MES_LABELS =
            {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};
    private static final int MESES_SERIE = 6;

    private final LeadRepository leadRepository;
    private final AnalysisRepository analysisRepository;
    private final FailureRepository failureRepository;
    private final TransactionRepository transactionRepository;
    private final IWalletService walletService;

    public DashboardService(LeadRepository leadRepository,
                            AnalysisRepository analysisRepository,
                            FailureRepository failureRepository,
                            TransactionRepository transactionRepository,
                            IWalletService walletService) {
        this.leadRepository = leadRepository;
        this.analysisRepository = analysisRepository;
        this.failureRepository = failureRepository;
        this.transactionRepository = transactionRepository;
        this.walletService = walletService;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardAdvogadoResponseDTO getAdvogadoDashboard(UUID advogadoId) {
        Timestamp inicioMes = Timestamp.valueOf(LocalDate.now().withDayOfMonth(1).atStartOfDay());
        YearMonth inicioSerieYm = YearMonth.now().minusMonths(MESES_SERIE - 1L);
        Timestamp inicioSerie = Timestamp.valueOf(inicioSerieYm.atDay(1).atStartOfDay());

        long clientesAtivos = leadRepository.countDistinctClientesByAdvogado(advogadoId, StatusLeadEnum.ADQUIRIDO);
        long leadsAdquiridosTotais = leadRepository.countByAdvogadoIdAndStatus(advogadoId, StatusLeadEnum.ADQUIRIDO);
        long leadsAdquiridosNoMes = leadRepository.countByAdvogadoIdAndStatusAndDataAquisicaoGreaterThanEqual(
                advogadoId, StatusLeadEnum.ADQUIRIDO, inicioMes);
        long leadsDisponiveis = leadRepository.countByStatus(StatusLeadEnum.DISPONIVEL);
        long analisesTotais = analysisRepository.countByUsuarioId(advogadoId);
        long analisesNoMes = analysisRepository.countByUsuarioIdAndDataCriacaoGreaterThanEqual(advogadoId, inicioMes);
        long totalGastoEmLeads = transactionRepository.sumQuantidadeByUsuarioAndTipo(
                advogadoId, TipoTransacaoEnum.AQUISICAO_LEAD);
        long totalErros = failureRepository.countByAdvogadoAcquiredLeads(advogadoId, StatusLeadEnum.ADQUIRIDO);

        int saldoTokens = resolveSaldo(advogadoId);

        Double taxaConversao = leadsAdquiridosTotais > 0
                ? (double) clientesAtivos / leadsAdquiridosTotais
                : null;

        List<MonthlyCountDTO> leadsPorMes = bucketByMonth(
                leadRepository.findAcquisitionDatesSince(advogadoId, StatusLeadEnum.ADQUIRIDO, inicioSerie), inicioSerieYm);
        List<MonthlyCountDTO> analisesPorMes = bucketByMonth(
                analysisRepository.findAnalysisDatesSince(advogadoId, inicioSerie), inicioSerieYm);
        List<MonthlyCountDTO> errosPorMes = bucketByMonth(
                failureRepository.findFailureAnalysisDatesSince(advogadoId, StatusLeadEnum.ADQUIRIDO, inicioSerie), inicioSerieYm);

        return DashboardAdvogadoResponseDTO.builder()
                .advogadoId(advogadoId)
                .clientesAtivos(clientesAtivos)
                .leadsAdquiridosTotais(leadsAdquiridosTotais)
                .leadsAdquiridosNoMes(leadsAdquiridosNoMes)
                .leadsDisponiveis(leadsDisponiveis)
                .analisesTotais(analisesTotais)
                .analisesNoMes(analisesNoMes)
                .saldoTokens(saldoTokens)
                .totalGastoEmLeads(totalGastoEmLeads)
                .totalErros(totalErros)
                .taxaConversao(taxaConversao)
                .leadsPorMes(leadsPorMes)
                .analisesPorMes(analisesPorMes)
                .errosPorMes(errosPorMes)
                .build();
    }

    /** Saldo da carteira; 0 caso o advogado (ainda) não possua carteira. */
    private int resolveSaldo(UUID advogadoId) {
        try {
            Integer saldo = walletService.getBalance(advogadoId);
            return saldo != null ? saldo : 0;
        } catch (RuntimeException e) {
            return 0;
        }
    }

    /**
     * Agrupa uma lista de timestamps em {@value #MESES_SERIE} buckets mensais consecutivos
     * a partir de {@code start} (mais antigo -> mais recente), preenchendo meses sem dados com zero.
     * Agregação feita em Java para ser portável entre H2 (teste) e PostgreSQL (dev/prod).
     */
    private List<MonthlyCountDTO> bucketByMonth(List<Timestamp> dates, YearMonth start) {
        Map<YearMonth, Long> counts = new LinkedHashMap<>();
        for (int i = 0; i < MESES_SERIE; i++) {
            counts.put(start.plusMonths(i), 0L);
        }
        if (dates != null) {
            for (Timestamp ts : dates) {
                if (ts == null) continue;
                YearMonth ym = YearMonth.from(ts.toLocalDateTime());
                if (counts.containsKey(ym)) {
                    counts.merge(ym, 1L, Long::sum);
                }
            }
        }
        List<MonthlyCountDTO> result = new ArrayList<>(MESES_SERIE);
        for (Map.Entry<YearMonth, Long> entry : counts.entrySet()) {
            YearMonth ym = entry.getKey();
            result.add(MonthlyCountDTO.builder()
                    .ano(ym.getYear())
                    .mes(ym.getMonthValue())
                    .label(MES_LABELS[ym.getMonthValue() - 1])
                    .count(entry.getValue())
                    .build());
        }
        return result;
    }
}
