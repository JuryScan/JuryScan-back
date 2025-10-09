package unicap.juryscan.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import unicap.juryscan.exception.custom.ResourceNotFoundException;
import unicap.juryscan.model.User;
import unicap.juryscan.repository.UserRepository;

import java.io.IOException;
import java.util.Collections;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    public SecurityFilter(TokenService tokenService, UserRepository userRepository) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        var token = recoverToken(request);
        var login = tokenService.validateToken(token);

        if (login != null){
            User user = userRepository.findByEmailIgnoreCase(login).orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
            String role = switch (user.getTipoUsuario()){
                case COMUM -> "ROLE_COMUM";
                case ADVOGADO -> "ROLE_ADVOGADO";
                case ADMIN -> "ROLE_ADMIN";
            };
            var authorities = Collections.singleton(new SimpleGrantedAuthority(role));
            var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return authHeader.replace("Bearer ", "");
    }
}
