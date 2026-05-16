@Service
public class IaServiceImpl implements IaService {

    private final WebClient webClient;

    public IaServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public IAResponseDTO processInput(IARequestDTO request) {
        return webClient.post()
                .uri("/predict")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(IAResponseDTO.class)
                .block();
    }
}