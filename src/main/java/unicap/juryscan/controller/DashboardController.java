package unicap.juryscan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import unicap.juryscan.dto.dashboard.DashboardAdvogadoResponseDTO;
import unicap.juryscan.infra.ApiResponse;
import unicap.juryscan.model.User;
import unicap.juryscan.service.dashboard.IDashboardService;

@RestController
@RequestMapping("${api.uri}/dashboard")
public class DashboardController {

    private final IDashboardService dashboardService;

    public DashboardController(IDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/advogado/me")
    public ResponseEntity<ApiResponse> getAdvogadoDashboard(@AuthenticationPrincipal User user) {
        DashboardAdvogadoResponseDTO dashboard = dashboardService.getAdvogadoDashboard(user.getId());
        ApiResponse response = new ApiResponse(true, "Dashboard do advogado obtido com sucesso", dashboard, 200);
        return ResponseEntity.status(200).body(response);
    }
}
