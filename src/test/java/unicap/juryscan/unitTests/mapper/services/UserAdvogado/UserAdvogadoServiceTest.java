package unicap.juryscan.unitTests.mapper.services.UserAdvogado;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import unicap.juryscan.dto.auth.AuthenticationDTO;
import unicap.juryscan.dto.auth.LoginResponseDTO;
import unicap.juryscan.dto.userAdvogado.UserAdvogadoCreateDTO;
import unicap.juryscan.dto.userAdvogado.UserAdvogadoRegisteredDTO;
import unicap.juryscan.dto.userAdvogado.UserAdvogadoResponseDTO;
import unicap.juryscan.enums.TipoUserEnum;
import unicap.juryscan.enums.UserStatusEnum;
import unicap.juryscan.exception.ResourceNotFoundException;
import unicap.juryscan.exception.UserAlreadyExistsException;
import unicap.juryscan.mapper.UserAdvogadoMapper;
import unicap.juryscan.model.User;
import unicap.juryscan.repository.UserRepository;
import unicap.juryscan.service.auth.AuthenticationService;
import unicap.juryscan.service.userAdvogado.UserAdvogadoService;
import unicap.juryscan.service.wallet.WalletService;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAdvogadoServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserAdvogadoMapper userAdvogadoMapper;

    @Mock
    private WalletService walletService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private UserAdvogadoService userAdvogadoService;

    private UserAdvogadoCreateDTO createDTO;
    private User userEntity;
    private UserAdvogadoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        createDTO = new UserAdvogadoCreateDTO();
        createDTO.setEmail("advogado@teste.com");
        createDTO.setSenha("senha123");

        userEntity = new User();
        userEntity.setEmail("advogado@teste.com");

        responseDTO = new UserAdvogadoResponseDTO();
    }

    @Nested
    @DisplayName("Tests for Advogado User Creation")
    class CreateUserAdvogadoTests {

        @Test
        @DisplayName("Should create an advogado successfully when email does not exist")
        void shouldCreateAdvogado_WhenEmailDoesNotExist() {
            // Arrange
            when(userRepository.findByEmailIgnoreCase(createDTO.getEmail())).thenReturn(null);
            when(userAdvogadoMapper.toEntity(createDTO)).thenReturn(userEntity);
            when(passwordEncoder.encode(createDTO.getSenha())).thenReturn("encryptedPassword");
            when(userRepository.save(any(User.class))).thenReturn(userEntity);

            LoginResponseDTO loginResponse = LoginResponseDTO.builder().token("mock-jwt-token").build();
            when(authenticationService.login(any(AuthenticationDTO.class))).thenReturn(loginResponse);
            when(userAdvogadoMapper.toResponseDTO(userEntity)).thenReturn(responseDTO);


            UserAdvogadoRegisteredDTO result = userAdvogadoService.createUserAdvogado(createDTO);

            assertNotNull(result);
            assertEquals("mock-jwt-token", result.getToken());

            assertEquals(TipoUserEnum.ADVOGADO, userEntity.getTipoUsuario());
            assertEquals(UserStatusEnum.ATIVO, userEntity.getStatus());
            assertFalse(userEntity.getEmailVerificado());
            assertEquals("encryptedPassword", userEntity.getSenha());

            verify(walletService, times(1)).createWallet(userEntity);
            verify(userRepository, times(1)).save(userEntity);
        }

        @Test
        @DisplayName("Should throw UserAlreadyExistsException when email is already registered")
        void shouldThrowException_WhenEmailAlreadyExists() {

            when(userRepository.findByEmailIgnoreCase(createDTO.getEmail())).thenReturn(new User());


            assertThrows(UserAlreadyExistsException.class, () -> {
                userAdvogadoService.createUserAdvogado(createDTO);
            });

            verify(userRepository, never()).save(any(User.class));
            verify(walletService, never()).createWallet(any(User.class));
        }
    }

    @Nested
    @DisplayName("Tests for Soft Delete")
    class SoftDeleteTests {

        @Test
        @DisplayName("Should change status to INACTIVE successfully")
        void shouldInactivateUser_WhenUserIsActive() {

            UUID id = UUID.randomUUID();
            userEntity.setStatus(UserStatusEnum.ATIVO);
            when(userRepository.findByTipoUsuarioAndId(TipoUserEnum.ADVOGADO, id)).thenReturn(Optional.of(userEntity));

            userAdvogadoService.softDeleteUserAdvogado(id);


            assertEquals(UserStatusEnum.INATIVO, userEntity.getStatus());
            verify(userRepository, times(1)).save(userEntity);
        }

        @Test
        @DisplayName("Should throw IllegalStateException when user is already INACTIVE")
        void shouldThrowException_WhenUserIsAlreadyInactive() {

            UUID id = UUID.randomUUID();
            userEntity.setStatus(UserStatusEnum.INATIVO);
            when(userRepository.findByTipoUsuarioAndId(TipoUserEnum.ADVOGADO, id)).thenReturn(Optional.of(userEntity));


            assertThrows(IllegalStateException.class, () -> {
                userAdvogadoService.softDeleteUserAdvogado(id);
            });
            verify(userRepository, never()).save(userEntity);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user is not found")
        void shouldThrowException_WhenUserNotFound() {

            UUID id = UUID.randomUUID();
            when(userRepository.findByTipoUsuarioAndId(TipoUserEnum.ADVOGADO, id)).thenReturn(Optional.empty());


            assertThrows(ResourceNotFoundException.class, () -> {
                userAdvogadoService.softDeleteUserAdvogado(id);
            });
        }
    }
}