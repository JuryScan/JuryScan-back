package unicap.juryscan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicap.juryscan.dto.userComum.UserComumCreateDTO;
import unicap.juryscan.dto.userComum.UserComumResponseDTO;

import unicap.juryscan.service.userComum.UserComumService;
import unicap.juryscan.utils.ApiResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.uri}/users/comum")
public class UserComumController {

    private final UserComumService userComumService;

    public UserComumController(UserComumService userComumService) {
        this.userComumService = userComumService;
    }

    @GetMapping("/")
    public ResponseEntity<ApiResponse> getAllUserComums(){
        List<UserComumResponseDTO> users = userComumService.getAllUserComums();
        if (users.isEmpty()){
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

    @PostMapping("/")
    public ResponseEntity<ApiResponse> addUser(@RequestBody UserComumCreateDTO userRequest){
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

    @PostMapping("/{userId}/soft-delete")
    public ResponseEntity<ApiResponse> softDeleteUser(@PathVariable UUID userId){
        userComumService.softDeleteUserComum(userId);
        ApiResponse response = new ApiResponse(true, "Usuário desativado com sucesso", 200);
        return ResponseEntity.status(200).body(response);
    }
}