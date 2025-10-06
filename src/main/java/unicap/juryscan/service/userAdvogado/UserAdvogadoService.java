package unicap.juryscan.service.userAdvogado;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import unicap.juryscan.dto.pagination.PageResponse;
import unicap.juryscan.dto.userAdvogado.UserAdvogadoCreateDTO;
import unicap.juryscan.dto.userAdvogado.UserAdvogadoResponseDTO;
import unicap.juryscan.enums.TipoUserEnum;
import unicap.juryscan.enums.UserStatusEnum;
import unicap.juryscan.exception.custom.ResourceNotFoundException;
import unicap.juryscan.mapper.UserAdvogadoMapper;
import unicap.juryscan.model.User;
import unicap.juryscan.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
public class UserAdvogadoService implements IUserAdvogadoService {

    private final UserRepository userRepository;
    private final UserAdvogadoMapper userAdvogadoMapper;

    public UserAdvogadoService(UserRepository userRepository, UserAdvogadoMapper userAdvogadoMapper) {
        this.userRepository = userRepository;
        this.userAdvogadoMapper = userAdvogadoMapper;
    }

    @Override
    public UserAdvogadoResponseDTO createUserAdvogado(UserAdvogadoCreateDTO userCreateDTO) {
        User userMapped = userAdvogadoMapper.toEntity(userCreateDTO);
        userMapped.setSenha(userCreateDTO.getSenha());
        userMapped.setTipoUsuario(TipoUserEnum.ADVOGADO);
        userMapped.setStatus(UserStatusEnum.ATIVO);
        userMapped.setEmailVerificado(false);
        userRepository.save(userMapped);

        return userAdvogadoMapper.toResponseDTO(userMapped);
    }

    @Override
    public PageResponse<UserAdvogadoResponseDTO> getAllUserAdvogados(Pageable pageable) {
        Page<UserAdvogadoResponseDTO> page = userRepository
                .findAllByTipoUsuario(TipoUserEnum.ADVOGADO, pageable)
                .map(userAdvogadoMapper::toResponseDTO);
        //TODO Implementação de mapper de Page para PageResponse
        PageResponse<UserAdvogadoResponseDTO> pageResponse = new PageResponse<>();
        pageResponse.setTotalElements(page.getTotalElements());
        pageResponse.setTotalPages(page.getTotalPages());
        pageResponse.setPage(page.getNumber());
        pageResponse.setItems(page.getContent());
        pageResponse.setPageSize(page.getSize());

        return pageResponse;
    }

    @Override
    public UserAdvogadoResponseDTO getUserAdvogadoById(UUID id) {
        User user = userRepository
                .findByTipoUsuarioAndId(TipoUserEnum.ADVOGADO, id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return userAdvogadoMapper.toResponseDTO(user);
    }

    @Override
    public void hardDeleteUserAdvogado(UUID id) {
        User user = userRepository.findByTipoUsuarioAndId(TipoUserEnum.ADVOGADO, id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        userRepository.delete(user);
    }

    @Override
    public void softDeleteUserAdvogado(UUID id) {
        User user = userRepository.findByTipoUsuarioAndId(TipoUserEnum.ADVOGADO, id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        if (user.getStatus() == UserStatusEnum.INATIVO) throw new IllegalStateException("Usuário já está inativo");
        user.setStatus(UserStatusEnum.INATIVO);
        userRepository.save(user);
    }
}
