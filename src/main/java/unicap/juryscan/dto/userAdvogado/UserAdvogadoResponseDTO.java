package unicap.juryscan.dto.userAdvogado;

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
public class UserAdvogadoResponseDTO {
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

    private String descricao;
    private String numeroOab;
    private String experiencia;

    private Timestamp dataCriacao;
    private Timestamp dataUltimaAtualizacao;
}
