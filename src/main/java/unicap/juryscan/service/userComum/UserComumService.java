package unicap.juryscan.service.userComum;

import org.springframework.stereotype.Service;
import unicap.juryscan.dto.userComum.UserComumCreateDTO;
import unicap.juryscan.dto.userComum.UserComumResponseDTO;
import unicap.juryscan.enums.TipoUserEnum;
import unicap.juryscan.enums.UserStatusEnum;
import unicap.juryscan.exception.custom.ResourceNotFoundException;
import unicap.juryscan.mapper.UserComumMapper;
import unicap.juryscan.model.User;
import unicap.juryscan.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
public class UserComumService implements IUserComumService {

    private final UserRepository userRepository;
    private final UserComumMapper userComumMapper;

    public UserComumService(UserRepository userRepository, UserComumMapper userComumMapper) {
        this.userRepository = userRepository;
        this.userComumMapper = userComumMapper;
    }

    @Override
    public List<UserComumResponseDTO> getAllUserComums(){
        return userRepository
                .findAllByTipoUsuario(TipoUserEnum.COMUM)
                .stream()
                .map(userComumMapper::toResponseDTO)
                .toList();
    }

    @Override
    public UserComumResponseDTO createUserComum(UserComumCreateDTO userCreateDTO) {
        User userMapped = userComumMapper.toEntity(userCreateDTO);
        userMapped.setSenha(userCreateDTO.getSenha());
        userMapped.setTipoUsuario(TipoUserEnum.COMUM);
        userMapped.setStatus(UserStatusEnum.ATIVO);
        userMapped.setEmailVerificado(false);
        userRepository.save(userMapped);

        return userComumMapper.toResponseDTO(userMapped);
    }

    @Override
    public UserComumResponseDTO getUserComumById(UUID id){
        return userRepository
                .findById(id)
                .map(userComumMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }
}