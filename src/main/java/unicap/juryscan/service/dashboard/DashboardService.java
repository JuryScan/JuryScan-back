package unicap.juryscan.service.dashboard;

import org.springframework.stereotype.Service;
import unicap.juryscan.dto.dashboard.DashboardMetricsDTO;
import unicap.juryscan.enums.StatusLeadEnum;
import unicap.juryscan.enums.TipoUserEnum;
import unicap.juryscan.exception.ResourceNotFoundException;
import unicap.juryscan.model.User;
import unicap.juryscan.model.Wallet;
import unicap.juryscan.repository.AnalysisRepository;
import unicap.juryscan.repository.LeadRepository;
import unicap.juryscan.repository.UserRepository;
import unicap.juryscan.repository.WalletRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;

@Service
public class DashboardService implements IDashboardService {

    private final LeadRepository leadRepository;
    private final AnalysisRepository analysisRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public DashboardService(LeadRepository leadRepository,
                           AnalysisRepository analysisRepository,
                           WalletRepository walletRepository,
                           UserRepository userRepository) {
        this.leadRepository = leadRepository;
        this.analysisRepository = analysisRepository;
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    @Override
    public DashboardMetricsDTO getAdvogadoMetrics(UUID advogadoId) {
        // Verificar se o usuário existe e é advogado
        User advogado = userRepository.findByTipoUsuarioAndId(TipoUserEnum.ADVOGADO, advogadoId)
                .orElseThrow(() -> new ResourceNotFoundException("Advogado não encontrado com ID: " + advogadoId));

        long leadsAdquiridos = leadRepository.countByAdvogadoIdAndStatus(advogadoId, StatusLeadEnum.ADQUIRIDO);

        long leadsDisponiveis = leadRepository.countByStatus(StatusLeadEnum.DISPONIVEL);

        Timestamp inicioDoMes = getStartOfCurrentMonth();
        long analisesNoMes = analysisRepository.countByUsuarioIdAndDataCriacaoAfter(advogadoId, inicioDoMes);

        Integer saldoCreditos = walletRepository.findByUsuarioId(advogadoId)
                .map(Wallet::getSaldo)
                .orElse(0);

        return DashboardMetricsDTO.builder()
                .leadsAdquiridos(leadsAdquiridos)
                .leadsDisponiveis(leadsDisponiveis)
                .analisesNoMes(analisesNoMes)
                .saldoCreditos(saldoCreditos)
                .build();
    }

    private Timestamp getStartOfCurrentMonth() {
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        return Timestamp.valueOf(startOfMonth);
    }
}

