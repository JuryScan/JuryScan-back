@Service
public class IaServiceImpl implements IaService {

    private final WebClient webClient;

    public IaServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public IAResponseDTO processInput(IARequestDTO request) {
        return webClient.post()
                .uri("/analyze")
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                    clientResponse.bodyToMono(String.class)
                        .map(body -> new RuntimeException("Erro na chamada IA: " + body))
                )
                .bodyToMono(IAResponseDTO.class)
                .block();
    }
}