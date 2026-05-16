package unicap.juryscan.dto.ai;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

public class AIRequestDTO {
    @NotBlank
    private String titulo;
    @NotBlank
    private String descricaoGeral;
}