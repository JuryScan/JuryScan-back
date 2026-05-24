package unicap.juryscan.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${api.integration.services.ai-analyzer.url}")
    private String aiAnalyzerUrl;

    @Value("${api.integration.services.ai-analyzer.api-key}")
    private String apiKey;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
            .baseUrl(aiAnalyzerUrl)
            .defaultHeader("X-API-Key", apiKey)
            .build();
    }
}


