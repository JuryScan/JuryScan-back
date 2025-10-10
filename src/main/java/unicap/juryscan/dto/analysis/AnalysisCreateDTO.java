package unicap.juryscan.dto.analysis;

import lombok.*;
import unicap.juryscan.dto.failure.FailureCreateDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class AnalysisCreateDTO {
    private String titulo;
    private String descricaoGeral;
    private List<FailureCreateDTO> falhas;
}
