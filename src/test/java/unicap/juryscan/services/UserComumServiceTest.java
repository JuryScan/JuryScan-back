package unicap.juryscan.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import unicap.juryscan.dto.pagination.PageResponse;
import unicap.juryscan.dto.userComum.UserComumCreateDTO;
import unicap.juryscan.dto.userComum.UserComumResponseDTO;
import unicap.juryscan.enums.TipoUserEnum;
import unicap.juryscan.enums.UserStatusEnum;
import unicap.juryscan.exception.custom.ResourceNotFoundException;
import unicap.juryscan.exception.custom.UserAlreadyExistsException;
import unicap.juryscan.mapper.UserComumMapper;
import unicap.juryscan.model.User;
import unicap.juryscan.repository.UserRepository;
import unicap.juryscan.service.userComum.UserComumService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserComumServiceTest {

    @Mock
    private UserRepository repo;

    @Mock
    private UserComumMapper mapper;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserComumService service;

    private User user;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("teste@email.com");
        user.setSenha("123");
        user.setTipoUsuario(TipoUserEnum.COMUM);
        user.setStatus(UserStatusEnum.ATIVO);
    }

    @Test
    void criar_ok() {
        UserComumCreateDTO dto = new UserComumCreateDTO();
        dto.setEmail("novo@teste.com");
        dto.setSenha("123");

        when(repo.findByEmailIgnoreCase(dto.getEmail())).thenReturn(null);
        when(mapper.toEntity(dto)).thenReturn(user);
        when(encoder.encode("123")).thenReturn("abc");
        when(repo.save(user)).thenReturn(user);
        when(mapper.toResponseDTO(user)).thenReturn(new UserComumResponseDTO());

        var res = service.createUserComum(dto);
        assertNotNull(res);
    }

    @Test
    void criar_emailExistente() {
        UserComumCreateDTO dto = new UserComumCreateDTO();
        dto.setEmail("teste@email.com");

        when(repo.findByEmailIgnoreCase(dto.getEmail())).thenReturn(user);

        assertThrows(UserAlreadyExistsException.class, () -> service.createUserComum(dto));
    }

    @Test
    void buscarPorId_ok() {
        when(repo.findByTipoUsuarioAndId(any(), any())).thenReturn(Optional.of(user));
        when(mapper.toResponseDTO(user)).thenReturn(new UserComumResponseDTO());

        var res = service.getUserComumById(user.getId());
        assertNotNull(res);
    }

    @Test
    void buscarPorId_notFound() {
        when(repo.findByTipoUsuarioAndId(any(), any())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getUserComumById(UUID.randomUUID()));
    }

    @Test
    void softDelete_ok() {
        when(repo.findByTipoUsuarioAndId(any(), any())).thenReturn(Optional.of(user));
        service.softDeleteUserComum(user.getId());
        assertEquals(UserStatusEnum.INATIVO, user.getStatus());
    }

    @Test
    void listar_ok() {
        var pageable = PageRequest.of(0, 5);
        when(repo.findAllByTipoUsuario(any(), any()))
                .thenReturn(new PageImpl<>(List.of(user)));
        when(mapper.toResponseDTO(any())).thenReturn(new UserComumResponseDTO());

        PageResponse<UserComumResponseDTO> res = service.getAllUserComums(pageable);

        assertEquals(1, res.getTotalElements());
    }
}
