package unicap.juryscan.dto.userComum;

import lombok.*;
import unicap.juryscan.enums.UserStatusEnum;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserComumResponseDTO {
    private UUID id;
    private String nomeCompleto;
    private String email;
    private String telefone;
    private String emailRecuperacao;
    private Date dataNascimento;
    private String cpf;
    private UserStatusEnum status;
    // Não é estritamente o padrão HATEOAS
    private String enderecoUrl;
    private Boolean emailVerificado;
    private Timestamp dataCriacao;
    private Timestamp dataUltimaAtualizacao;
}
