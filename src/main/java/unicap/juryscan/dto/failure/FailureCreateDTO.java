package unicap.juryscan.dto.failure;

import lombok.*;
import unicap.juryscan.enums.SeveridadeFalhaEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class FailureCreateDTO {
    private String titulo;
    private SeveridadeFalhaEnum severidade;
    private String descricao;
    private String sugestaoCorrecao;
    private Float confianca;
}
