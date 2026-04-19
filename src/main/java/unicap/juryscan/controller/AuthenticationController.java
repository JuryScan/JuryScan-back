package unicap.juryscan.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicap.juryscan.dto.auth.AuthenticationDTO;
import unicap.juryscan.dto.auth.LoginResponseDTO;
import unicap.juryscan.dto.auth.UserAuthenticatedDTO;
import unicap.juryscan.mapper.UserMapper;
import unicap.juryscan.service.auth.AuthenticationService;
import unicap.juryscan.infra.ApiResponse;
import unicap.juryscan.service.reCaptcha.RecaptchaService;

@RestController
@RequestMapping("${api.uri}/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final RecaptchaService recaptchaService;

    public AuthenticationController(AuthenticationService authenticationService, UserMapper userMapper,  RecaptchaService recaptchaService) {
        this.authenticationService = authenticationService;
        this.recaptchaService = recaptchaService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO authRequest){
        // valida primeiro no captcha
        recaptchaService.isValid(authRequest.getRecaptchaToken());

        LoginResponseDTO response = authenticationService.login(authRequest);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> me(){
        UserAuthenticatedDTO user = authenticationService.getAuthenticatedUser();
        ApiResponse response = new ApiResponse(true, "Usuário autenticado", user, 200);
        return ResponseEntity.status(200).body(response);
    }

}
