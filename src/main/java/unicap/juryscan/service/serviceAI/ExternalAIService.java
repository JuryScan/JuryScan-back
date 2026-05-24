package unicap.juryscan.service.serviceAI;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import unicap.juryscan.dto.integrationAi.AIResponseDTO;

@Service
public class ExternalAIService implements IGenericAIService {

    private final WebClient webClient;

    public ExternalAIService(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public AIResponseDTO analyzeDocument(byte[] documentBytes) {
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(documentBytes) {
                @Override
                public String getFilename() {
                    return "document.pdf";
                }
            });

            AIResponseDTO response = webClient.post()
                    .uri("/api/v1/analyze")
                    .body(BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .bodyToMono(AIResponseDTO.class)
                    .doOnError(error -> {
                        throw new RuntimeException("Erro ao chamar serviço externo de IA: " + error.getMessage(), error);
                    })
                    .block();
            validateAIResponse(response);

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao analisar documento com serviço externo: " + e.getMessage(), e);
        }
    }

    private void validateAIResponse(AIResponseDTO response) {
        if (response == null) {
            throw new IllegalStateException("Resposta inválida da IA: nenhuma resposta recebida");
        }

        if (!"success".equalsIgnoreCase(response.getStatus())) {
            throw new IllegalStateException("Erro na análise da IA: " + response.getMessage());
        }

        if (response.getResult() == null) {
            throw new IllegalStateException("Resposta inválida da IA: dados de resultado ausentes");
        }

        if (response.getResult().getTitulo() == null || response.getResult().getTitulo().isEmpty()) {
            throw new IllegalStateException("Resposta inválida da IA: título não fornecido");
        }

        if (response.getResult().getDescricaoGeral() == null || response.getResult().getDescricaoGeral().isEmpty()) {
            throw new IllegalStateException("Resposta inválida da IA: descrição geral não fornecida");
        }
    }
}


