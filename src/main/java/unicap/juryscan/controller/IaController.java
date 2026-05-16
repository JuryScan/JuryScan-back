@RestController
@RequestMapping("/ia")
public class IaController {

    private final IaService iaService;

    public IaController(IaService iaService) {
        this.iaService = iaService;
    }

    @PostMapping("/process")
    public ResponseEntity<IAResponseDTO> process(@RequestBody IARequestDTO request) {
        IAResponseDTO response = iaService.processInput(request);
        return ResponseEntity.ok(response);
    }
}