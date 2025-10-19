package unicap.juryscan;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import unicap.juryscan.model.User;
import unicap.juryscan.repository.UserRepository;
import unicap.juryscan.service.auth.AuthorizationService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorizationServiceTest {

    @Test
    void testLoadUserByUsernameFound() {
      
        UserRepository repo = mock(UserRepository.class);
        User user = new User();
        user.setEmail("teste@email.com");
        when(repo.findByEmailIgnoreCase("teste@email.com")).thenReturn(user);

       
        AuthorizationService service = new AuthorizationService(repo);
        UserDetails result = service.loadUserByUsername("teste@email.com");

        assertEquals("teste@email.com", result.getUsername());
        verify(repo, times(1)).findByEmailIgnoreCase("teste@email.com");
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        UserRepository repo = mock(UserRepository.class);
        when(repo.findByEmailIgnoreCase("naoexiste@email.com")).thenReturn(null);

        AuthorizationService service = new AuthorizationService(repo);

        assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername("naoexiste@email.com");
        });
    }
}
