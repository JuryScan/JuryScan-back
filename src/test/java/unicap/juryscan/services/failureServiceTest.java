package unicap.juryscan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import unicap.juryscan.dto.failure.FailureResponseDTO;
import unicap.juryscan.dto.pagination.PageResponse;
import unicap.juryscan.mapper.FailureMapper;
import unicap.juryscan.model.Failure;
import unicap.juryscan.repository.FailureRepository;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FailureServiceTest {

    @Mock
    private FailureRepository failureRepository;

    @Mock
    private FailureMapper failureMapper;

    @InjectMocks
    private FailureService failureService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllFailuresByAnalysisId() {

        UUID analysisId = UUID.randomUUID();
        PageRequest pageable = PageRequest.of(0, 10);

    
        Failure failure = new Failure();

     
        FailureResponseDTO responseDTO = new FailureResponseDTO();

     
        Page<Failure> page = new PageImpl<>(Collections.singletonList(failure), pageable, 1);

        
        when(failureRepository.findAllByAnaliseId(analysisId, pageable)).thenReturn(page);

       
        when(failureMapper.toResponseDTO(failure)).thenReturn(responseDTO);

        
        PageResponse<FailureResponseDTO> result =
                failureService.getAllFailuresByAnalysisId(analysisId, pageable);

    
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(0, result.getPage());
        assertEquals(10, result.getPageSize());
        assertEquals(1, result.getItems().size());
        assertEquals(responseDTO, result.getItems().get(0));

        
        verify(failureRepository, times(1)).findAllByAnaliseId(analysisId, pageable);
        verify(failureMapper, times(1)).toResponseDTO(failure);
    }
}
