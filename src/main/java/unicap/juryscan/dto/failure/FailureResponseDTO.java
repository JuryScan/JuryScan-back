package unicap.juryscan.dto.failure;

import lombok.*;
import unicap.juryscan.enums.SeveridadeFalhaEnum;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class FailureResponseDTO {
    private UUID id;
    private String titulo;
    private SeveridadeFalhaEnum severidade;
    private Float confianca;
    private String sugestaoCorrecao;
}
