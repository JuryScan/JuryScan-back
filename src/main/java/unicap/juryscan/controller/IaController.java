package unicap.juryscan.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import unicap.juryscan.service.analysis.IAnalysisService;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import unicap.juryscan.dto.analysis.AnalysisResponseDTO;
import unicap.juryscan.infra.ApiResponse;

import java.util.UUID;

@RestController
@RequestMapping("${api.uri}/ia-service")
@Deprecated
public class IaController {

    private final IAnalysisService analysisService;

    public IaController(IAnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> process(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") UUID userId) {

        try {
            byte[] documentBytes = file.getBytes();
            AnalysisResponseDTO response = analysisService.createAnalysis(userId, documentBytes);
            ApiResponse apiResponse = new ApiResponse(true, "Análise realizada com sucesso", response, 200);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse(false, "Erro ao processar: " + e.getMessage(), 500);
            return ResponseEntity.internalServerError().body(apiResponse);
        }
    }
}

