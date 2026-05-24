package unicap.juryscan.dto.integrationAi;

import lombok.*;
import unicap.juryscan.dto.failure.FailureCreateDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AIResponseResultDTO {
    private String titulo;
    private String descricaoGeral;
    private String relatorio_sumario_juridico;
    private String sumario;
    private List<FailureCreateDTO> failures;
}

