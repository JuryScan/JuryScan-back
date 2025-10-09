package unicap.juryscan.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import unicap.juryscan.dto.auth.AuthenticationDTO;
import unicap.juryscan.dto.auth.LoginResponseDTO;
import unicap.juryscan.infra.security.TokenService;
import unicap.juryscan.model.User;

@RestController
@RequestMapping("${api.uri}/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthenticationController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO authRequest){
        var usernamePassword = new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword());
        var auth = authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((User) auth.getPrincipal());
        LoginResponseDTO response = new LoginResponseDTO(token, "Autenticação bem-sucedida", true, 200);

        return ResponseEntity.status(200).body(response);
    }
}
