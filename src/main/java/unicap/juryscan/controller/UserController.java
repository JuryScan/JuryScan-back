package unicap.juryscan.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unicap.juryscan.dto.userComum.UserComumCreateDTO;
import unicap.juryscan.model.User;
import unicap.juryscan.service.userComum.UserComumService;

@RestController
@RequestMapping("${api.uri}/users")
public class UserController {

    private final UserComumService userComumService;

    public UserController(UserComumService userComumService) {
        this.userComumService = userComumService;
    }

    @GetMapping
    public String users(){
        return "users";
    }

    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody UserComumCreateDTO user){
        User createdUser = userComumService.createUser(user);
        return ResponseEntity.ok().body(createdUser);
    }
}
