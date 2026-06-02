package unicap.juryscan.dto.userAdvogado;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAdvogadoUpdateDTO {
    private String nomeCompleto;
    private String telefone;
    private String descricao;
    private String experiencia;
    private String numeroOab;
}
