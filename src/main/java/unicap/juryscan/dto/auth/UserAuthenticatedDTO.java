package unicap.juryscan.dto.auth;

import lombok.*;
import unicap.juryscan.enums.TipoUserEnum;
import unicap.juryscan.enums.UserStatusEnum;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserAuthenticatedDTO {
    private UUID id;
    private String nomeCompleto;
    private String email;
    private String telefone;
    private String emailRecuperacao;
    private Date dataNascimento;
    private String cpf;
    private UserStatusEnum status;
    private Boolean emailVerificado;
    private String enderecoUrl;
    private TipoUserEnum tipoUsuario;

    private String descricao;
    private String numeroOab;
    private String experiencia;

    private Timestamp dataCriacao;
    private Timestamp dataUltimaAtualizacao;
}