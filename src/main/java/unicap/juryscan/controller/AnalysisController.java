package unicap.juryscan.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import unicap.juryscan.dto.analysis.AnalysisResponseDTO;
import unicap.juryscan.dto.pagination.PageResponse;
import unicap.juryscan.model.User;
import unicap.juryscan.service.analysis.IAnalysisService;
import unicap.juryscan.service.serviceAI.IGenericAIService;
import unicap.juryscan.infra.ApiResponse;

import java.util.UUID;

@RestController
@RequestMapping("${api.uri}/analysis")
public class AnalysisController {

    private final IAnalysisService analysisService;

    public AnalysisController(IAnalysisService analysisService, IGenericAIService genericAIService) {
        this.analysisService = analysisService;
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse> getAllAnalysis(
            @RequestParam("page") int page,
            @RequestParam("page_size") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        PageResponse<AnalysisResponseDTO> analysis = analysisService.getAllAnalysis(pageable);
        if (analysis.getItems().isEmpty()) {
            ApiResponse response = new ApiResponse(true, "Nenhuma análise encontrada", 204);
            return ResponseEntity.status(204).body(response);
        }
        ApiResponse response = new ApiResponse(true, "Análises encontradas com sucesso", analysis, 200);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getAnalysisById(@PathVariable UUID id) {
        AnalysisResponseDTO analysis = analysisService.getAnalysisById(id);
        ApiResponse response  = new ApiResponse(true, "Análise encontrada com sucesso", analysis, 200);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getAllAnalysisByUserId(
            @PathVariable("userId") UUID userId,
            @RequestParam("page") int page,
            @RequestParam("page_size") int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        PageResponse<AnalysisResponseDTO> analysis = analysisService.getAllAnalysisByUserId(userId, pageable);
        if (analysis.getItems().isEmpty()) {
            ApiResponse response = new ApiResponse(true, "Nenhuma análise encontrada", 204);
            return ResponseEntity.status(204).body(response);
        }
        ApiResponse response = new ApiResponse(true, "Análises encontradas com sucesso", analysis, 200);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ApiResponse> createAnalysis(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) throws Exception {

        if (!file.getContentType().equals("application/pdf")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Tipo de arquivo inválido. Apenas PDFs são aceitos.", 400));
        }

        byte[] documentBytes = file.getBytes();
        AnalysisResponseDTO analysis = analysisService.createAnalysis(user.getId(), documentBytes);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Análise criada com sucesso", analysis, 201));
    }
}
