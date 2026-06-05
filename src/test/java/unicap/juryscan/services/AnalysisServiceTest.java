package unicap.juryscan.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import unicap.juryscan.dto.analysis.AnalysisResponseDTO;
import unicap.juryscan.exception.ResourceNotFoundException;
import unicap.juryscan.mapper.AnalysisMapper;
import unicap.juryscan.model.Analysis;
import unicap.juryscan.model.User;
import unicap.juryscan.repository.AnalysisRepository;
import unicap.juryscan.repository.UserRepository;
import unicap.juryscan.service.analysis.AnalysisService;
import unicap.juryscan.service.serviceAI.IGenericAIService;
import unicap.juryscan.service.wallet.IWalletService;
import unicap.juryscan.dto.integrationAi.AIResponseDTO;

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

    @Mock
    private IWalletService walletService;

    @InjectMocks
    private AnalysisService analysisService;

    private static final int ANALYSIS_COST = 1;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        // @Value nao e resolvido em teste unitario; injeta o custo da analise manualmente
        ReflectionTestUtils.setField(analysisService, "analysisCost", ANALYSIS_COST);
    }

    @Test
    void testCreateAnalysis() {
        UUID userId = UUID.randomUUID();
        User userMock = new User();
        userMock.setId(userId);

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
        when(walletService.getBalance(userId)).thenReturn(ANALYSIS_COST + 5);
        when(genericAIService.analyzeDocument(dummyDoc)).thenReturn(aiResponse);
        when(analysisMapper.toEntity(aiResponse)).thenReturn(analysisEntity);
        when(analysisRepository.saveAndFlush(analysisEntity)).thenReturn(savedEntity);
        when(analysisMapper.toResponseDTO(savedEntity)).thenReturn(responseDTO);

        AnalysisResponseDTO result = analysisService.createAnalysis(userId, dummyDoc);

        assertEquals("Nova Análise", result.getTitulo());
        assertEquals("Descrição test", result.getDescricaoGeral());

        verify(walletService, times(1)).getBalance(userId);
        verify(analysisRepository, times(1)).saveAndFlush(analysisEntity);
        verify(walletService, times(1)).deductCredits(userId, ANALYSIS_COST);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testCreateAnalysis_SaldoInsuficiente() {
        UUID userId = UUID.randomUUID();
        User userMock = new User();
        userMock.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userMock));
        when(walletService.getBalance(userId)).thenReturn(0);

        assertThrows(IllegalStateException.class,
                () -> analysisService.createAnalysis(userId, new byte[]{1, 2, 3}));

        verify(walletService, never()).deductCredits(any(), anyInt());
        verify(analysisRepository, never()).saveAndFlush(any());
    }

    @Test
    void testGetAnalysisById_NotFound() {
        UUID analysisId = UUID.randomUUID();
        when(analysisRepository.findById(analysisId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> analysisService.getAnalysisById(analysisId));
    }
}
