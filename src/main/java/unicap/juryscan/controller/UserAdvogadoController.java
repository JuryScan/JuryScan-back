package unicap.juryscan.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicap.juryscan.dto.pagination.PageResponse;
import unicap.juryscan.dto.userAdvogado.UserAdvogadoCreateDTO;
import unicap.juryscan.dto.userAdvogado.UserAdvogadoResponseDTO;
import unicap.juryscan.service.userAdvogado.UserAdvogadoService;
import unicap.juryscan.utils.ApiResponse;

import java.util.UUID;

@RestController
@RequestMapping("${api.uri}/users/advogado")
public class UserAdvogadoController {

    private final UserAdvogadoService userAdvogadoService;

    public UserAdvogadoController(UserAdvogadoService userAdvogadoService) {
        this.userAdvogadoService = userAdvogadoService;
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse> getAllUserAdvogados(
            @RequestParam("page") int page,
            @RequestParam("page_size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<UserAdvogadoResponseDTO> users = userAdvogadoService.getAllUserAdvogados(pageable);
        if (users.getItems().isEmpty()){
            ApiResponse response = new ApiResponse(true, "Nenhum usuário advogado encontrado", 204);
            return ResponseEntity.status(204).body(response);
        }
        ApiResponse response = new ApiResponse(true, "Usuários encontrados com sucesso", users, 200);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUserAdvogadoById(@PathVariable UUID id){
        UserAdvogadoResponseDTO user = userAdvogadoService.getUserAdvogadoById(id);
        ApiResponse response = new ApiResponse(true, "Usuário encontrado com sucesso", user, 200);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/")
    public ResponseEntity<ApiResponse> addUser(@RequestBody UserAdvogadoCreateDTO userRequest){
        UserAdvogadoResponseDTO createdUser = userAdvogadoService.createUserAdvogado(userRequest);
        ApiResponse response = new ApiResponse(true, "Usuário criado com sucesso", createdUser, 201);
        return ResponseEntity.status(201).body(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> hardDeleteUser(@PathVariable UUID userId) {
        userAdvogadoService.hardDeleteUserAdvogado(userId);
        ApiResponse response = new ApiResponse(true, "Usuário deletado com sucesso", 200);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{userId}/soft-delete")
    public ResponseEntity<ApiResponse> softDeleteUser(@PathVariable UUID userId) {
        userAdvogadoService.softDeleteUserAdvogado(userId);
        ApiResponse response = new ApiResponse(true, "Usuário desativado com sucesso", 200);
        return ResponseEntity.status(200).body(response);
    }
}
