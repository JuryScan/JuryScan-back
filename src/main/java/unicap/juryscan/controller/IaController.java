package unicap.juryscan.controller;

import org.springframework.web.bind.annotation.*;
import unicap.juryscan.dto.ai.AIRequestDTO;
import unicap.juryscan.dto.ai.AIResponseDTO;
import org.springframework.http.ResponseEntity;
import unicap.juryscan.serviceAI.IaService;
import unicap.juryscan.serviceAI.IaServiceImpl;

@RestController
@RequestMapping("${api.uri}/ai-service")
public class IaController {

    private final IaService iaService;

    public IaController(IaService iaService) {
        this.iaService = iaService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<IAResponseDTO> process(@RequestBody IARequestDTO request) {
        IAResponseDTO response = iaService.processInput(request);
        return ResponseEntity.ok(response);
    }
}