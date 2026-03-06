package unicap.juryscan.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import unicap.juryscan.controller.AuthenticationController;
import unicap.juryscan.dto.auth.AuthenticationDTO;
import unicap.juryscan.dto.auth.LoginResponseDTO;
import unicap.juryscan.dto.auth.UserAuthenticatedDTO;
import unicap.juryscan.mapper.UserMapper;
import unicap.juryscan.service.auth.AuthenticationService;
import unicap.juryscan.utils.ApiResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {

    @InjectMocks
    private AuthenticationController authenticationController;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should login successfully")
    void shouldLoginSuccessfully() {
        AuthenticationDTO request = AuthenticationDTO.builder().email("t@t.com").password("123").build();
        LoginResponseDTO mockResponse = LoginResponseDTO.builder().token("token").success(true).build();

        when(authenticationService.login(any())).thenReturn(mockResponse);

        ResponseEntity<LoginResponseDTO> response = authenticationController.login(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("token", response.getBody().getToken());
    }

    @Test
    @DisplayName("Should return user data")
    void shouldReturnAuthenticatedUser() {
        UserAuthenticatedDTO userDTO = UserAuthenticatedDTO.builder().email("t@t.com").build();
        when(authenticationService.getAuthenticatedUser()).thenReturn(userDTO);

        ResponseEntity<ApiResponse> response = authenticationController.me();

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    @DisplayName("Should throw exception on login failure")
    void shouldThrowExceptionWhenLoginFails() {
        when(authenticationService.login(any())).thenThrow(new RuntimeException("Error"));
        assertThrows(RuntimeException.class, () -> authenticationController.login(new AuthenticationDTO()));
    }
}