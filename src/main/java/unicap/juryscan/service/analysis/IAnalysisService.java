package unicap.juryscan.service.analysis;

import org.springframework.data.domain.Pageable;
import unicap.juryscan.dto.analysis.AnalysisResponseDTO;
import unicap.juryscan.dto.pagination.PageResponse;

public interface IAnalysisService {
    PageResponse<AnalysisResponseDTO> getAllAnalysis(Pageable pageable);
}
