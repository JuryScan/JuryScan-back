package unicap.juryscan;

import unicap.juryscan.dto.analysis.AnalysisCreateDTO;
import unicap.juryscan.dto.analysis.AnalysisResponseDTO;
import unicap.juryscan.dto.pagination.PageResponse;
import unicap.juryscan.exception.custom.ResourceNotFoundException;
import unicap.juryscan.mapper.AnalysisMapper;
import unicap.juryscan.model.Analysis;
import unicap.juryscan.model.Failure;
import unicap.juryscan.model.User;
import unicap.juryscan.repository.AnalysisRepository;
import unicap.juryscan.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AnalysisServiceTest {

    @Mock
    private AnalysisRepository analysisRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AnalysisMapper analysisMapper;

    @InjectMocks
    private AnalysisService analysisService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

  
    @Test
    void testGetAllAnalysis() {
        Pageable pageable = PageRequest.of(0, 10);

        Analysis analysis = new Analysis();
        analysis.setTitulo("Teste 1");

        AnalysisResponseDTO dto = new AnalysisResponseDTO();
        dto.setTitulo("Teste 1");

        Page<Analysis> pageMock =
                new PageImpl<>(List.of(analysis), pageable, 1);

        when(analysisRepository.findAll(pageable))
                .thenReturn(pageMock);

        when(analysisMapper.toResponseDTO(analysis))
                .thenReturn(dto);

        PageResponse<AnalysisResponseDTO> response =
                analysisService.getAllAnalysis(pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getItems().size());
        assertEquals("Teste 1", response.getItems().get(0).getTitulo());

        verify(analysisRepository, times(1)).findAll(pageable);
    }

   
    @Test
    void testGetAllAnalysisByUserId() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        Analysis analysis = new Analysis();
        analysis.setTitulo("Teste User");

        AnalysisResponseDTO dto = new AnalysisResponseDTO();
        dto.setTitulo("Teste User");

        Page<Analysis> pageMock =
                new PageImpl<>(List.of(analysis), pageable, 1);

        when(analysisRepository.findAllByUsuarioId(userId, pageable))
                .thenReturn(pageMock);

        when(analysisMapper.toResponseDTO(analysis))
                .thenReturn(dto);

        PageResponse<AnalysisResponseDTO> response =
                analysisService.getAllAnalysisByUserId(userId, pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals("Teste User", response.getItems().get(0).getTitulo());

        verify(analysisRepository, times(1))
                .findAllByUsuarioId(userId, pageable);
    }

 
    @Test
    void testCreateAnalysis() {

        UUID userId = UUID.randomUUID();

        User userMock = new User();
        userMock.setId(userId);

        AnalysisCreateDTO createDTO = new AnalysisCreateDTO();
        createDTO.setTitulo("Nova Análise");
        createDTO.setDescricaoGeral("Descrição test");
        createDTO.setFalhas(List.of()); // vazio mesmo, funciona

        Analysis analysisEntity = new Analysis();
        analysisEntity.setTitulo("Nova Análise");

        Analysis savedEntity = new Analysis();
        savedEntity.setTitulo("Nova Análise");

        AnalysisResponseDTO responseDTO = new AnalysisResponseDTO();
        responseDTO.setTitulo("Nova Análise");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(userMock));

        when(analysisMapper.toEntity(createDTO))
                .thenReturn(analysisEntity);

        when(analysisRepository.save(analysisEntity))
                .thenReturn(savedEntity);

        when(analysisMapper.toResponseDTO(savedEntity))
                .thenReturn(responseDTO);

        AnalysisResponseDTO result =
                analysisService.createAnalysis(userId, createDTO);

        assertEquals("Nova Análise", result.getTitulo());

        verify(analysisRepository, times(1))
                .save(analysisEntity);

        verify(userRepository, times(1))
                .findById(userId);
    }


    @Test
    void testGetAnalysisById() {

        UUID analysisId = UUID.randomUUID();

        Analysis analysis = new Analysis();
        analysis.setTitulo("Consulta única");

        AnalysisResponseDTO dto = new AnalysisResponseDTO();
        dto.setTitulo("Consulta única");

        when(analysisRepository.findById(analysisId))
                .thenReturn(Optional.of(analysis));

        when(analysisMapper.toResponseDTO(analysis))
                .thenReturn(dto);

        AnalysisResponseDTO result =
                analysisService.getAnalysisById(analysisId);

        assertEquals("Consulta única", result.getTitulo());
    }

 
    @Test
    void testGetAnalysisById_NotFound() {
        UUID analysisId = UUID.randomUUID();

        when(analysisRepository.findById(analysisId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> analysisService.getAnalysisById(analysisId)
        );
    }
}
