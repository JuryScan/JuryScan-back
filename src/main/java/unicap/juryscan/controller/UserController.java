package unicap.juryscan.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import unicap.juryscan.dto.user.ChangePasswordDTO;
import unicap.juryscan.infra.ApiResponse;
import unicap.juryscan.model.User;
import unicap.juryscan.service.user.UserService;

import java.util.UUID;

@RestController
@RequestMapping("${api.uri}/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<ApiResponse> changePassword(@PathVariable UUID id, @RequestBody ChangePasswordDTO request) {
        userService.changePassword(id, request);
        ApiResponse response = new ApiResponse(true, "Senha alterada com sucesso", 200);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> updateAvatar(@PathVariable UUID id, @RequestParam("foto") MultipartFile foto) {
        userService.updateAvatar(id, foto);
        ApiResponse response = new ApiResponse(true, "Foto de perfil atualizada com sucesso", 200);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/avatar/{id}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        byte[] foto = user.getFotoPerfil();
        if (foto == null || foto.length == 0) {
            return ResponseEntity.notFound().build();
        }
        String contentType = user.getFotoContentType() != null ? user.getFotoContentType() : MediaType.IMAGE_JPEG_VALUE;
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(foto);
    }
}
