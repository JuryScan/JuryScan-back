package unicap.juryscan.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import unicap.juryscan.dto.analysis.AnalysisCreateDTO;
import unicap.juryscan.dto.analysis.AnalysisResponseDTO;
import unicap.juryscan.dto.pagination.PageResponse;
import unicap.juryscan.exception.custom.ResourceNotFoundException;
import unicap.juryscan.mapper.AnalysisMapper;
import unicap.juryscan.model.Analysis;
import unicap.juryscan.model.User;
import unicap.juryscan.repository.AnalysisRepository;
import unicap.juryscan.repository.UserRepository;
import unicap.juryscan.service.analysis.AnalysisService;
import unicap.juryscan.service.serviceAI.IGenericAIService;
import unicap.juryscan.dto.ai.AIResponseDTO;

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

    @Mock
    private IGenericAIService genericAIService;

    @InjectMocks
    private AnalysisService analysisService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAnalysis() {
        UUID userId = UUID.randomUUID();
        User userMock = new User();
        userMock.setId(userId);

        AnalysisCreateDTO createDTO = new AnalysisCreateDTO();
        createDTO.setTitulo("Nova Análise");
        createDTO.setDescricaoGeral("Descrição test");

        Analysis analysisEntity = new Analysis();
        analysisEntity.setTitulo("Nova Análise");

        Analysis savedEntity = new Analysis();
        savedEntity.setTitulo("Nova Análise");

        AnalysisResponseDTO responseDTO = new AnalysisResponseDTO();
        responseDTO.setTitulo("Nova Análise");
        responseDTO.setDescricaoGeral("Descrição test");

        byte[] dummyDoc = new byte[]{1, 2, 3};
        AIResponseDTO aiResponse = new AIResponseDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.of(userMock));
        when(genericAIService.analyzeDocument(dummyDoc)).thenReturn(aiResponse);
        when(analysisMapper.toEntity(aiResponse)).thenReturn(analysisEntity);
        when(analysisRepository.save(analysisEntity)).thenReturn(savedEntity);
        when(analysisMapper.toResponseDTO(savedEntity)).thenReturn(responseDTO);

        AnalysisResponseDTO result = analysisService.createAnalysis(userId, dummyDoc);

        assertEquals("Nova Análise", result.getTitulo());
        assertEquals("Descrição test", result.getDescricaoGeral());

        verify(analysisRepository, times(1)).save(analysisEntity);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetAnalysisById_NotFound() {
        UUID analysisId = UUID.randomUUID();
        when(analysisRepository.findById(analysisId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> analysisService.getAnalysisById(analysisId));
    }
}
