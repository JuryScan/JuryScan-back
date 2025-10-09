package unicap.juryscan.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicap.juryscan.dto.pagination.PageResponse;
import unicap.juryscan.dto.userComum.UserComumCreateDTO;
import unicap.juryscan.dto.userComum.UserComumResponseDTO;

import unicap.juryscan.service.userComum.UserComumService;
import unicap.juryscan.utils.ApiResponse;

import java.util.UUID;

@RestController
@RequestMapping("${api.uri}/users/comum")
public class UserComumController {

    private final UserComumService userComumService;

    public UserComumController(UserComumService userComumService) {
        this.userComumService = userComumService;
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse> getAllUserComums(
            @RequestParam("page") int page,
            @RequestParam("page_size") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<UserComumResponseDTO> users = userComumService.getAllUserComums(pageable);
        if (users.getItems().isEmpty()){
            ApiResponse response = new ApiResponse(true, "Nenhum usuário comum encontrado", 204);
            return ResponseEntity.status(204).body(response);
        }
        ApiResponse response = new ApiResponse(true, "Usuários encontrados com sucesso", users, 200);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUserComumById(@PathVariable UUID id){
        UserComumResponseDTO user = userComumService.getUserComumById(id);
        ApiResponse response = new ApiResponse(true, "Usuário encontrado com sucesso", user, 200);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUserComum(@RequestBody UserComumCreateDTO userRequest){
        UserComumResponseDTO createdUser = userComumService.createUserComum(userRequest);
        ApiResponse response = new ApiResponse(true, "Usuário criado com sucesso", createdUser, 201);
        return ResponseEntity.status(201).body(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse> hardDeleteUser(@PathVariable UUID userId){
        userComumService.hardDeleteUserComum(userId);
        ApiResponse response = new ApiResponse(true, "Usuário deletado com sucesso", 200);
        return ResponseEntity.status(200).body(response);
    }

    @PutMapping("/{userId}/soft-delete")
    public ResponseEntity<ApiResponse> softDeleteUser(@PathVariable UUID userId){
        userComumService.softDeleteUserComum(userId);
        ApiResponse response = new ApiResponse(true, "Usuário desativado com sucesso", 200);
        return ResponseEntity.status(200).body(response);
    }
}