package unicap.juryscan.service.dashboard;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import unicap.juryscan.dto.dashboard.DashboardMetricsDTO;
import unicap.juryscan.dto.dashboard.MonthlyCountDTO;
import unicap.juryscan.enums.StatusLeadEnum;
import unicap.juryscan.enums.TipoUserEnum;
import unicap.juryscan.exception.ResourceNotFoundException;
import unicap.juryscan.model.Wallet;
import unicap.juryscan.repository.AnalysisRepository;
import unicap.juryscan.repository.FailureRepository;
import unicap.juryscan.repository.LeadRepository;
import unicap.juryscan.repository.UserRepository;
import unicap.juryscan.repository.WalletRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public DashboardService(LeadRepository leadRepository,
                            AnalysisRepository analysisRepository,
                            FailureRepository failureRepository,
                            WalletRepository walletRepository,
                            UserRepository userRepository) {
        this.leadRepository = leadRepository;
        this.analysisRepository = analysisRepository;
        this.failureRepository = failureRepository;
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardMetricsDTO getAdvogadoMetrics(UUID advogadoId) {
        // Garante que o usuario existe e e advogado (evita devolver metricas para ids invalidos)
        userRepository.findByTipoUsuarioAndId(TipoUserEnum.ADVOGADO, advogadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Advogado não encontrado com ID: " + advogadoId));

        Timestamp inicioDoMes = getStartOfCurrentMonth();
        YearMonth inicioSerieYm = YearMonth.now().minusMonths(MESES_SERIE - 1L);
        Timestamp inicioSerie = Timestamp.valueOf(inicioSerieYm.atDay(1).atStartOfDay());

        // --- KPIs de leads ---
        long leadsAdquiridos = leadRepository.countByAdvogadoIdAndStatus(advogadoId, StatusLeadEnum.ADQUIRIDO);
        long leadsAdquiridosNoMes = leadRepository.countByAdvogadoAndStatusAndDataAquisicaoApos(
                advogadoId, StatusLeadEnum.ADQUIRIDO, inicioDoMes);
        long leadsDisponiveis = leadRepository.countByStatus(StatusLeadEnum.DISPONIVEL);
        long clientesAtivos = leadRepository.countDistinctClientesByAdvogadoAndStatus(
                advogadoId, StatusLeadEnum.ADQUIRIDO);
        long totalGastoEmCreditos = leadRepository.sumCustoCreditosByAdvogadoAndStatus(
                advogadoId, StatusLeadEnum.ADQUIRIDO);

        // --- KPIs de analises / falhas ---
        long analisesNoMes = analysisRepository.countByUsuarioIdAndDataCriacaoAfter(advogadoId, inicioDoMes);
        long analisesTotais = analysisRepository.countByUsuarioId(advogadoId);
        long totalErros = failureRepository.countByAnaliseUsuarioId(advogadoId);

        // --- Carteira ---
        Integer saldoCreditos = walletRepository.findByUsuarioId(advogadoId)
                .map(Wallet::getSaldo)
                .orElse(0);

        // Taxa de conversao = clientes distintos atendidos / leads adquiridos (nula quando nao ha leads)
        Double taxaConversao = leadsAdquiridos > 0
                ? (double) clientesAtivos / leadsAdquiridos
                : null;

        // --- Series mensais (agregacao feita em Java -> identica em H2 (teste) e PostgreSQL) ---
        List<MonthlyCountDTO> leadsPorMes = bucketByMonth(
                leadRepository.findDatasAquisicaoDesde(advogadoId, StatusLeadEnum.ADQUIRIDO, inicioSerie), inicioSerieYm);
        List<MonthlyCountDTO> analisesPorMes = bucketByMonth(
                analysisRepository.findDatasCriacaoDesde(advogadoId, inicioSerie), inicioSerieYm);
        List<MonthlyCountDTO> errosPorMes = bucketByMonth(
                failureRepository.findDatasAnaliseDesde(advogadoId, inicioSerie), inicioSerieYm);

        return DashboardMetricsDTO.builder()
                .leadsAdquiridos(leadsAdquiridos)
                .leadsAdquiridosNoMes(leadsAdquiridosNoMes)
                .leadsDisponiveis(leadsDisponiveis)
                .clientesAtivos(clientesAtivos)
                .analisesNoMes(analisesNoMes)
                .analisesTotais(analisesTotais)
                .totalErros(totalErros)
                .totalGastoEmCreditos(totalGastoEmCreditos)
                .saldoCreditos(saldoCreditos)
                .taxaConversao(taxaConversao)
                .leadsPorMes(leadsPorMes)
                .analisesPorMes(analisesPorMes)
                .errosPorMes(errosPorMes)
                .build();
    }

    private Timestamp getStartOfCurrentMonth() {
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        return Timestamp.valueOf(startOfMonth);
    }

    /**
     * Agrupa timestamps em {@value #MESES_SERIE} buckets mensais consecutivos a partir de {@code start},
     * preenchendo meses sem dados com zero. Feito em Java para ser identico em H2 (teste) e PostgreSQL.
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
