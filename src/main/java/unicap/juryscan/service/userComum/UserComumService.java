package unicap.juryscan.service.userComum;

import org.springframework.stereotype.Service;
import unicap.juryscan.dto.userComum.UserComumCreateDTO;
import unicap.juryscan.enums.TipoUserEnum;
import unicap.juryscan.enums.UserAtivoEnum;
import unicap.juryscan.mapper.UserMapper;
import unicap.juryscan.model.User;
import unicap.juryscan.repository.UserRepository;

@Service
public class UserComumService implements IUserComumService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserComumService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public User createUser(UserComumCreateDTO user) {
        User userMapped = userMapper.toEntity(user);
        userMapped.setAtivo(UserAtivoEnum.ATIVO);
        userMapped.setTipoUsuario(TipoUserEnum.COMUM);

        return userRepository.save(userMapped);
    }
}
