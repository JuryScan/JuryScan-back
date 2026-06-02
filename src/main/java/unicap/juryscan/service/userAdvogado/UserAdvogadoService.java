package unicap.juryscan.service.userAdvogado;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import unicap.juryscan.dto.auth.AuthenticationDTO;
import unicap.juryscan.dto.auth.LoginResponseDTO;
import unicap.juryscan.dto.pagination.PageResponse;
import unicap.juryscan.dto.userAdvogado.UserAdvogadoCreateDTO;
import unicap.juryscan.dto.userAdvogado.UserAdvogadoRegisteredDTO;
import unicap.juryscan.dto.userAdvogado.UserAdvogadoResponseDTO;
import unicap.juryscan.dto.userAdvogado.UserAdvogadoUpdateDTO;
import unicap.juryscan.enums.TipoUserEnum;
import unicap.juryscan.enums.UserStatusEnum;
import unicap.juryscan.exception.ResourceNotFoundException;
import unicap.juryscan.exception.UserAlreadyExistsException;
import unicap.juryscan.mapper.UserAdvogadoMapper;
import unicap.juryscan.model.User;
import unicap.juryscan.model.Wallet;
import unicap.juryscan.repository.UserRepository;
import unicap.juryscan.repository.WalletRepository;
import unicap.juryscan.service.auth.AuthenticationService;
import unicap.juryscan.service.wallet.WalletService;

import java.util.UUID;

@Service
public class UserAdvogadoService implements IUserAdvogadoService {

    private final UserRepository userRepository;
    private final UserAdvogadoMapper userAdvogadoMapper;
    private final WalletService walletService;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;

    public UserAdvogadoService(UserRepository userRepository, UserAdvogadoMapper userAdvogadoMapper, PasswordEncoder passwordEncoder, AuthenticationService authenticationService, WalletService walletService) {
        this.userRepository = userRepository;
        this.userAdvogadoMapper = userAdvogadoMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationService = authenticationService;
        this.walletService = walletService;
    }

    @Override
    public UserAdvogadoRegisteredDTO createUserAdvogado(UserAdvogadoCreateDTO userCreateDTO) {
        if (userRepository.findByEmailIgnoreCase(userCreateDTO.getEmail()) != null) {
            throw new UserAlreadyExistsException("Usuário com esse email já existe");
        }

        User userMapped = userAdvogadoMapper.toEntity(userCreateDTO);
        String encryptedPassword = passwordEncoder.encode(userCreateDTO.getSenha());

        userMapped.setSenha(encryptedPassword);
        userMapped.setTipoUsuario(TipoUserEnum.ADVOGADO);
        userMapped.setStatus(UserStatusEnum.ATIVO);
        userMapped.setEmailVerificado(false);
        User savedUser = userRepository.save(userMapped);
        // Criar carteira automaticamente para o novo usuário
        Wallet wallet = walletService.createWallet(savedUser);

        AuthenticationDTO authDTO = AuthenticationDTO.builder()
                .email(userMapped.getEmail())
                .password(userCreateDTO.getSenha())
                .build();
        LoginResponseDTO loginResponse = authenticationService.login(authDTO);

        return new UserAdvogadoRegisteredDTO(loginResponse.getToken(), userAdvogadoMapper.toResponseDTO(userMapped));
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

    @Override
    public UserAdvogadoResponseDTO updateUserAdvogado(UUID id, UserAdvogadoUpdateDTO dto) {
        User user = userRepository.findByTipoUsuarioAndId(TipoUserEnum.ADVOGADO, id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        if (dto.getNomeCompleto() != null) user.setNomeCompleto(dto.getNomeCompleto());
        if (dto.getTelefone() != null) user.setTelefone(dto.getTelefone());
        if (dto.getDescricao() != null) user.setDescricao(dto.getDescricao());
        if (dto.getExperiencia() != null) user.setExperiencia(dto.getExperiencia());
        if (dto.getNumeroOab() != null) user.setNumeroOab(dto.getNumeroOab());
        User saved = userRepository.save(user);
        return userAdvogadoMapper.toResponseDTO(saved);
    }

    @Override
    public PageResponse<UserAdvogadoResponseDTO> searchAdvogados(String busca, String cidade, String estado, Pageable pageable) {
        String b = (busca != null && !busca.isBlank()) ? busca : null;
        String c = (cidade != null && !cidade.isBlank()) ? cidade : null;
        String e = (estado != null && !estado.isBlank()) ? estado : null;

        Page<UserAdvogadoResponseDTO> page = userRepository
                .searchAdvogados(TipoUserEnum.ADVOGADO, b, c, e, pageable)
                .map(userAdvogadoMapper::toResponseDTO);

        PageResponse<UserAdvogadoResponseDTO> pageResponse = new PageResponse<>();
        pageResponse.setTotalElements(page.getTotalElements());
        pageResponse.setTotalPages(page.getTotalPages());
        pageResponse.setPage(page.getNumber());
        pageResponse.setItems(page.getContent());
        pageResponse.setPageSize(page.getSize());

        return pageResponse;
    }
}
