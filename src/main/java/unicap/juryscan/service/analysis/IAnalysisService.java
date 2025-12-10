package unicap.juryscan.service.analysis;

import org.springframework.data.domain.Pageable;
import unicap.juryscan.dto.analysis.AnalysisCreateDTO;
import unicap.juryscan.dto.analysis.AnalysisResponseDTO;
import unicap.juryscan.dto.pagination.PageResponse;

import java.util.UUID;

public interface IAnalysisService {
    PageResponse<AnalysisResponseDTO> getAllAnalysis(Pageable pageable);

    PageResponse<AnalysisResponseDTO> getAllAnalysisByUserId(UUID userId, Pageable pageable);

    AnalysisResponseDTO createAnalysis(UUID userId, byte[] documentBytes);

    AnalysisResponseDTO getAnalysisById(UUID analysisId);
}
