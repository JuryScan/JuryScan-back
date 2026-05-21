package unicap.juryscan.unitTests.mapper.services.Authorization;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import unicap.juryscan.model.User;
import unicap.juryscan.repository.UserRepository;
import unicap.juryscan.service.auth.AuthorizationService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private UserRepository repo;

    @InjectMocks
    private AuthorizationService service;

    @Nested
    @DisplayName("Tests for loadUserByUsername")
    class LoadUserByUsernameTests {

        @Test
        @DisplayName("Should return UserDetails when user is found by email")
        void loadUserByUsername_found() {
            User user = new User();
            user.setEmail("test@email.com");

            when(repo.findByEmailIgnoreCase("test@email.com")).thenReturn(user);

            UserDetails result = service.loadUserByUsername("test@email.com");

            assertNotNull(result);
            assertEquals("test@email.com", result.getUsername());
            verify(repo, times(1)).findByEmailIgnoreCase("test@email.com");
        }

        @Test
        @DisplayName("Should throw UsernameNotFoundException when user is not found")
        void loadUserByUsername_notFound() {
            when(repo.findByEmailIgnoreCase("notfound@email.com")).thenReturn(null);

            assertThrows(UsernameNotFoundException.class, () ->
                    service.loadUserByUsername("notfound@email.com")
            );

            verify(repo, times(1)).findByEmailIgnoreCase("notfound@email.com");
        }
    }
}