package unicap.juryscan.mapper;

import org.springframework.stereotype.Component;
import unicap.juryscan.dto.auth.UserAuthenticatedDTO;
import unicap.juryscan.model.User;

@Component
public class UserMapper {

    public UserAuthenticatedDTO toUserAuthenticatedDTO(User user) {
        return UserAuthenticatedDTO.builder()
            .id(user.getId())
            .nomeCompleto(user.getNomeCompleto())
            .email(user.getEmail())
            .telefone(user.getTelefone())
            .emailRecuperacao(user.getEmailRecuperacao())
            .dataNascimento(user.getDataNascimento())
            .cpf(user.getCpf())
            .status(user.getStatus())
            .emailVerificado(user.getEmailVerificado())
            .enderecoUrl(user.getEndereco() != null ? "/addresses/" + user.getEndereco().getId() : null)
            .tipoUsuario(user.getTipoUsuario())
            .descricao(user.getDescricao())
            .numeroOab(user.getNumeroOab())
            .experiencia(user.getExperiencia())
            .dataCriacao(user.getDataCriacao())
            .dataUltimaAtualizacao(user.getDataUltimaAtualizacao())
            .build();
    }
}
