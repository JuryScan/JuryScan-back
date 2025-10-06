package unicap.juryscan.service.userComum;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import unicap.juryscan.dto.pagination.PageResponse;
import unicap.juryscan.dto.userComum.UserComumCreateDTO;
import unicap.juryscan.dto.userComum.UserComumResponseDTO;
import unicap.juryscan.enums.TipoUserEnum;
import unicap.juryscan.enums.UserStatusEnum;
import unicap.juryscan.exception.custom.ResourceNotFoundException;
import unicap.juryscan.mapper.UserComumMapper;
import unicap.juryscan.model.User;
import unicap.juryscan.repository.UserRepository;

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
    public PageResponse<UserComumResponseDTO> getAllUserComums(Pageable pageable) {
        Page<UserComumResponseDTO> page = userRepository
                .findAllByTipoUsuario(TipoUserEnum.COMUM, pageable)
                .map(userComumMapper::toResponseDTO);

        //TODO Implementação de mapper de Page para PageResponse
        PageResponse<UserComumResponseDTO> pageResponse = new PageResponse<>();
        pageResponse.setTotalElements(page.getTotalElements());
        pageResponse.setTotalPages(page.getTotalPages());
        pageResponse.setPage(page.getNumber());
        pageResponse.setItems(page.getContent());
        pageResponse.setPageSize(page.getSize());

        return pageResponse;
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
        User user = userRepository
                .findByTipoUsuarioAndId(TipoUserEnum.COMUM, id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return userComumMapper.toResponseDTO(user);
    }

    @Override
    public void hardDeleteUserComum(UUID id) {
        User user = userRepository.findByTipoUsuarioAndId(TipoUserEnum.COMUM, id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        userRepository.delete(user);
    }

    @Override
    public void softDeleteUserComum(UUID id) {
        User user = userRepository.findByTipoUsuarioAndId(TipoUserEnum.COMUM, id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        if (user.getStatus() == UserStatusEnum.INATIVO) throw new IllegalStateException("Usuário já está inativo");
        user.setStatus(UserStatusEnum.INATIVO);
        userRepository.save(user);
    }
}