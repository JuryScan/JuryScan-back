package unicap.juryscan.service.failure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import unicap.juryscan.dto.failure.FailureResponseDTO;
import unicap.juryscan.dto.pagination.PageResponse;
import unicap.juryscan.dto.userComum.UserComumResponseDTO;
import unicap.juryscan.mapper.FailureMapper;
import unicap.juryscan.repository.FailureRepository;

import java.util.UUID;

@Service
public class FailureService implements IFailureService {

    private final FailureRepository failureRepository;
    private final FailureMapper failureMapper;

    public FailureService(FailureRepository failureRepository, FailureMapper failureMapper) {
        this.failureRepository = failureRepository;
        this.failureMapper = failureMapper;
    }


    @Override
    public PageResponse<FailureResponseDTO> getAllFailuresByAnalysisId(UUID analysisId, Pageable pageable) {
        Page<FailureResponseDTO> page = failureRepository
                .findAllByAnaliseId(analysisId, pageable)
                .map(failureMapper::toResponseDTO);

        PageResponse<FailureResponseDTO> pageResponse = new PageResponse<>();
        pageResponse.setTotalElements(page.getTotalElements());
        pageResponse.setTotalPages(page.getTotalPages());
        pageResponse.setPage(page.getNumber());
        pageResponse.setItems(page.getContent());
        pageResponse.setPageSize(page.getSize());

        return pageResponse;
    }
}
