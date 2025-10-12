package unicap.juryscan.service.failure;

import org.springframework.data.domain.Pageable;
import unicap.juryscan.dto.failure.FailureResponseDTO;
import unicap.juryscan.dto.pagination.PageResponse;

import java.util.UUID;

public interface IFailureService {

    PageResponse<FailureResponseDTO> getAllFailuresByAnalysisId(UUID analysisId, Pageable pageable);
}
