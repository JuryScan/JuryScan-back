package unicap.juryscan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import unicap.juryscan.dto.dashboard.DashboardMetricsDTO;
import unicap.juryscan.infra.ApiResponse;
import unicap.juryscan.model.User;
import unicap.juryscan.service.dashboard.IDashboardService;

import java.util.UUID;

@RestController
@RequestMapping("${api.uri}/dashboard")
public class DashboardController {

    private final IDashboardService dashboardService;

    public DashboardController(IDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/metrics/me")
    public ResponseEntity<ApiResponse> getMyMetrics(@AuthenticationPrincipal User user) {
        DashboardMetricsDTO metrics = dashboardService.getAdvogadoMetrics(user.getId());
        ApiResponse response = new ApiResponse(true, "Métricas obtidas com sucesso", metrics, 200);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/metrics/advogado/{advogadoId}")
    public ResponseEntity<ApiResponse> getAdvogadoMetrics(@PathVariable UUID advogadoId) {
        DashboardMetricsDTO metrics = dashboardService.getAdvogadoMetrics(advogadoId);
        ApiResponse response = new ApiResponse(true, "Métricas obtidas com sucesso", metrics, 200);
        return ResponseEntity.ok(response);
    }
}

