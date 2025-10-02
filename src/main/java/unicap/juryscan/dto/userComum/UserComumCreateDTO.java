package unicap.juryscan.dto.userComum;

import lombok.*;

import java.sql.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserComumCreateDTO {
    private String nomeCompleto;
    private String email;
    private String telefone;
    private String senha;
    private Date dataNascimento;
    private String cpf;
}
