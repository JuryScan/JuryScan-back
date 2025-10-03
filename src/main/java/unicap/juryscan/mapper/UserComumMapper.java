package unicap.juryscan.mapper;

import org.springframework.stereotype.Component;
import unicap.juryscan.dto.userComum.UserComumCreateDTO;
import unicap.juryscan.dto.userComum.UserComumResponseDTO;
import unicap.juryscan.model.User;

@Component
public class UserComumMapper {

    public UserComumResponseDTO toResponseDTO(User entity){
        UserComumResponseDTO dto = new UserComumResponseDTO();
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

        return dto;
    }

    public User toEntity(UserComumCreateDTO dto){
        User user = new User();
        user.setNomeCompleto(dto.getNomeCompleto());
        user.setEmail(dto.getEmail());
        user.setTelefone(dto.getTelefone());
        user.setDataNascimento(dto.getDataNascimento());
        user.setCpf(dto.getCpf());

        return user;
    }
}
