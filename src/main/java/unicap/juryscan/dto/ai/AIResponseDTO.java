package unicap.juryscan.dto.ai;

import lombok.*;
import unicap.juryscan.dto.failure.FailureCreateDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AIResponseDTO {
    private String titulo;
    private String descricaoGeral;
    private List<FailureCreateDTO> failures;
}
