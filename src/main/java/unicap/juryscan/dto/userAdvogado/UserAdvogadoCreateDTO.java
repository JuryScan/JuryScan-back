package unicap.juryscan.dto.userAdvogado;

import lombok.*;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserAdvogadoCreateDTO {
    private String nomeCompleto;
    private String email;
    private String telefone;
    private String senha;
    private Date dataNascimento;
    private String cpf;

    private String descricao;
    private String numeroOab;
    private String experiencia;
}
