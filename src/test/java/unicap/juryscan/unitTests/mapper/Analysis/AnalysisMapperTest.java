package unicap.juryscan.unitTests.mapper.Analysis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import unicap.juryscan.dto.ai.AIResponseDTO;
import unicap.juryscan.mapper.AnalysisMapper;
import unicap.juryscan.mapper.FailureMapper;
import unicap.juryscan.model.Analysis;
import unicap.juryscan.model.Failure;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalysisMapperTest {

    @Mock
    private FailureMapper failureMapper;

    @InjectMocks
    private AnalysisMapper analysisMapper;

    @Nested
    @DisplayName("Tests for toEntity (AIResponseDTO)")
    class ToEntityAITests {

        @Test
        @DisplayName("Should map AIResponseDTO to Analysis and set bidirectional relationship correctly")
        void shouldMapToEntityAndSetRelationship_WhenAIResponseDTOPass() {
            AIResponseDTO dto = new AIResponseDTO();
            dto.setTitulo("Analysis Title");
            dto.setDescricaoGeral("General Description");

            var mockedFailureDTO = mock(unicap.juryscan.dto.failure.FailureCreateDTO.class);
            dto.setFailures(List.of(mockedFailureDTO));

            Failure mockedFailureEntity = new Failure();

            when(failureMapper.toEntity(any())).thenReturn(mockedFailureEntity);

            Analysis result = analysisMapper.toEntity(dto);

            assertNotNull(result);
            assertEquals("Analysis Title", result.getTitulo());
            assertEquals("General Description", result.getDescricaoGeral());
            assertEquals(1, result.getFalhas().size());
            assertEquals(result, result.getFalhas().get(0).getAnalise());

            verify(failureMapper, times(1)).toEntity(any());
        }
    }
}