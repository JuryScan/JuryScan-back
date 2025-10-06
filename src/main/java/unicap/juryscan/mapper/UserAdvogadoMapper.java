package unicap.juryscan.mapper;

import org.springframework.stereotype.Component;
import unicap.juryscan.dto.userAdvogado.UserAdvogadoCreateDTO;
import unicap.juryscan.dto.userAdvogado.UserAdvogadoResponseDTO;

import unicap.juryscan.model.User;

@Component
public class UserAdvogadoMapper {

    public UserAdvogadoResponseDTO toResponseDTO(User entity) {
        UserAdvogadoResponseDTO dto = new UserAdvogadoResponseDTO();
        dto.setId(entity.getId());
        dto.setNomeCompleto(entity.getNomeCompleto());
        dto.setEmail(entity.getEmail());
        dto.setTelefone(entity.getTelefone());
        dto.setEmailRecuperacao(entity.getEmailRecuperacao());
        dto.setDataNascimento(entity.getDataNascimento());
        dto.setCpf(entity.getCpf());
        dto.setStatus(entity.getStatus());
        dto.setEmailVerificado(entity.getEmailVerificado());
        dto.setDataCriacao(entity.getDataCriacao());
        dto.setDataUltimaAtualizacao(entity.getDataUltimaAtualizacao());

        dto.setDescricao(entity.getDescricao());
        dto.setExperiencia(entity.getExperiencia());
        dto.setNumeroOab(entity.getNumeroOab());

        return dto;
    }

    public User toEntity(UserAdvogadoCreateDTO dto) {
        User user = new User();
        user.setNomeCompleto(dto.getNomeCompleto());
        user.setEmail(dto.getEmail());
        user.setTelefone(dto.getTelefone());
        user.setDataNascimento(dto.getDataNascimento());
        user.setCpf(dto.getCpf());

        user.setDescricao(dto.getDescricao());
        user.setExperiencia(dto.getExperiencia());
        user.setNumeroOab(dto.getNumeroOab());

        return user;
    }
}
