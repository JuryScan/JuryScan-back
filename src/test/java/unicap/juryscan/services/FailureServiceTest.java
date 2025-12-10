package unicap.juryscan.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import unicap.juryscan.dto.failure.FailureResponseDTO;
import unicap.juryscan.dto.pagination.PageResponse;
import unicap.juryscan.mapper.FailureMapper;
import unicap.juryscan.model.Failure;
import unicap.juryscan.repository.FailureRepository;
import unicap.juryscan.service.failure.FailureService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FailureServiceTest {

    @Mock
    private FailureRepository failureRepository;

    @Mock
    private FailureMapper failureMapper;

    @InjectMocks
    private FailureService failureService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllFailuresByAnalysisId() {
        UUID analysisId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        Failure failure = new Failure();
        failure.setId(UUID.randomUUID());

        FailureResponseDTO dto = new FailureResponseDTO();
        dto.setId(failure.getId());

        Page<Failure> pageMock = new PageImpl<>(List.of(failure), pageable, 1);

        when(failureRepository.findAllByAnaliseId(analysisId, pageable)).thenReturn(pageMock);
        when(failureMapper.toResponseDTO(failure)).thenReturn(dto);

        PageResponse<FailureResponseDTO> response = failureService.getAllFailuresByAnalysisId(analysisId, pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getItems().size());
        assertEquals(failure.getId(), response.getItems().get(0).getId());

        verify(failureRepository, times(1)).findAllByAnaliseId(analysisId, pageable);
    }
}
