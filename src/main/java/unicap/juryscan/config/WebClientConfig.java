package juryscan.unicap.config;

@Configuration

public class WebClientConfig {
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
            .baseUrl("https://juryscan-agents-service.onrender.com/")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .filter(ExchangeFilterFunctions.logRequest()) //add filtro para requisição
            .filter(ExchangeFilterFunctions.logResponse()) // add filtro para respostas
            .build();
    }
}

    