package unicap.juryscan.unitTests.mapper.services.Authentication;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import unicap.juryscan.dto.auth.AuthenticationDTO;
import unicap.juryscan.dto.auth.LoginResponseDTO;
import unicap.juryscan.dto.auth.UserAuthenticatedDTO;
import unicap.juryscan.infra.security.TokenService;
import unicap.juryscan.mapper.UserMapper;
import unicap.juryscan.model.User;
import unicap.juryscan.service.auth.AuthenticationService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserMapper userMapper;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Nested
    @DisplayName("Tests for login method")
    class LoginTests {

        @Test
        @DisplayName("Should return LoginResponseDTO when authentication is successful")
        void login_ok() {
            AuthenticationDTO authRequest = AuthenticationDTO.builder().email("test@email.com").password("password").build();
            User userMock = mock(User.class);
            Authentication authenticationMock = mock(Authentication.class);
            UserAuthenticatedDTO userAuthenticatedDTO = UserAuthenticatedDTO.builder().build();

            when(authenticationManager.authenticate(any())).thenReturn(authenticationMock);
            when(authenticationMock.getPrincipal()).thenReturn(userMock);
            when(tokenService.generateToken(userMock)).thenReturn("mocked-jwt-token");
            when(userMapper.toUserAuthenticatedDTO(userMock)).thenReturn(userAuthenticatedDTO);

            LoginResponseDTO response = authenticationService.login(authRequest);

            assertNotNull(response);
            assertTrue(response.isSuccess());
            assertEquals(200, response.getStatus());
            assertEquals("mocked-jwt-token", response.getToken());
            assertEquals(userAuthenticatedDTO, response.getUser());

            verify(authenticationManager, times(1)).authenticate(any());
            verify(tokenService, times(1)).generateToken(userMock);
            verify(userMapper, times(1)).toUserAuthenticatedDTO(userMock);
        }
    }

    @Nested
    @DisplayName("Tests for getAuthenticatedUser method")
    class GetAuthenticatedUserTests {

        @Test
        @DisplayName("Should return UserAuthenticatedDTO from security context")
        void getAuthenticatedUser_ok() {
            User userMock = mock(User.class);
            Authentication authenticationMock = mock(Authentication.class);
            SecurityContext securityContextMock = mock(SecurityContext.class);
            UserAuthenticatedDTO expectedDTO = UserAuthenticatedDTO.builder().build();

            when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
            when(authenticationMock.getPrincipal()).thenReturn(userMock);
            when(userMapper.toUserAuthenticatedDTO(userMock)).thenReturn(expectedDTO);

            try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
                mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);

                UserAuthenticatedDTO result = authenticationService.getAuthenticatedUser();

                assertNotNull(result);
                assertEquals(expectedDTO, result);
                verify(userMapper, times(1)).toUserAuthenticatedDTO(userMock);
            }
        }
    }
}