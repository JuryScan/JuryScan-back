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
import unicap.juryscan.utils.ApiResponse;

@RestController
@RequestMapping("${api.uri}/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    public AuthenticationController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody @Valid AuthenticationDTO authRequest){
        var usernamePassword = new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword());
        var auth = authenticationManager.authenticate(usernamePassword);

        return ResponseEntity.ok(new ApiResponse(true, "Autenticação bem-sucedida", auth, 200));
    }
}
