package unicap.juryscan.service.serviceAI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import unicap.juryscan.dto.ai.AIResponseDTO;
import unicap.juryscan.dto.analysis.AnalysisResponseDTO;
import unicap.juryscan.utils.GeminiRequestContent;

import java.util.List;

@Service
public class GeminiAIService implements IGenericAIService {

    @Value("${gemini.api.key}")
    private String apiKey;
    @Value("${gemini.model}")
    private String model;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public AIResponseDTO analyzeDocument(byte[] documentBytes) {
        try {
            Client client = Client.builder().apiKey(apiKey).build();
            Part documentPart = Part.fromBytes( documentBytes, "application/pdf");
            Part textPart = Part.fromText(GeminiRequestContent.PROMPT);

            Content content =  Content.builder()
                    .role("user")
                    .parts(List.of(textPart, documentPart))
                    .build();
            GenerateContentConfig config = GenerateContentConfig.builder()
                    .responseMimeType("application/json")
                    .candidateCount(1)
                    .responseSchema(GeminiRequestContent.buildSchema())
                    .build();
            GenerateContentResponse response = client.models.generateContent(model, content, config);
            String jsonResponse = response.text();

            return mapper.readValue(jsonResponse, AIResponseDTO.class);
        } catch (Exception e){
            throw new RuntimeException("Erro ao analisar o documento com Gemini AI");
        }
    }
}
