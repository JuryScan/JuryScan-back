package unicap.juryscan.service.dashboard;

import unicap.juryscan.dto.dashboard.DashboardMetricsDTO;

import java.util.UUID;

public interface IDashboardService {

    /**
     * Retorna as métricas do dashboard para um advogado específico
     *
     * @param advogadoId ID do advogado
     * @return DashboardMetricsDTO com as métricas calculadas
     */
    DashboardMetricsDTO getAdvogadoMetrics(UUID advogadoId);
}

