package unicap.juryscan.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicap.juryscan.dto.failure.FailureResponseDTO;
import unicap.juryscan.dto.pagination.PageResponse;
import unicap.juryscan.service.failure.IFailureService;
import unicap.juryscan.utils.ApiResponse;

import java.util.UUID;

@RestController
@RequestMapping("${api.uri}/failures")
public class FailureController {

    private final IFailureService failureService;

    public FailureController(IFailureService failureService) {
        this.failureService = failureService;
    }

    @GetMapping("/analysis/{analysisId}")
    public ResponseEntity<ApiResponse> getAllFailuresByAnalysisId(
            @PathVariable("analysisId") UUID analysisId,
            @RequestParam("page") int page,
            @RequestParam("page_size") int page_size) {
        Pageable pageable = PageRequest.of(page, page_size);
        PageResponse<FailureResponseDTO> failures = failureService.getAllFailuresByAnalysisId(analysisId, pageable);
        if (failures.getItems().isEmpty()){
            ApiResponse response = new ApiResponse(true, "Nenhuma falha encontrada", 204);
            return ResponseEntity.status(204).body(response);
        }
        ApiResponse response = new ApiResponse(true, "Falhas encontradas com sucesso", failures, 200);
        return ResponseEntity.status(200).body(response);
    }
}
