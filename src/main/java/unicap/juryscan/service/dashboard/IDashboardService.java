package unicap.juryscan.service.dashboard;

import unicap.juryscan.dto.dashboard.DashboardAdvogadoResponseDTO;

import java.util.UUID;

public interface IDashboardService {
    DashboardAdvogadoResponseDTO getAdvogadoDashboard(UUID advogadoId);
}
