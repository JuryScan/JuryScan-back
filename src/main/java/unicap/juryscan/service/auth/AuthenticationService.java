package unicap.juryscan.service.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import unicap.juryscan.dto.auth.AuthenticationDTO;
import unicap.juryscan.dto.auth.LoginResponseDTO;
import unicap.juryscan.dto.auth.UserAuthenticatedDTO;
import unicap.juryscan.infra.security.TokenService;
import unicap.juryscan.mapper.UserMapper;
import unicap.juryscan.model.User;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final TokenService tokenService;

    public AuthenticationService(UserMapper userMapper, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
        this.userMapper = userMapper;
    }

    public LoginResponseDTO login(AuthenticationDTO authRequest) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword());
        var auth = authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());
        var user = userMapper.toUserAuthenticatedDTO((User) auth.getPrincipal());
        LoginResponseDTO response = new LoginResponseDTO(token, "Autenticação bem-sucedida", true, 200, user);
        return response;
    }

    public UserAuthenticatedDTO getAuthenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userMapper.toUserAuthenticatedDTO((User) authentication.getPrincipal());
    }
}
