package unicap.juryscan.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public String helloJuryScan(){
        return "Bem-vindo a API do JuryScan! rotas acessadas em /api/v1 e documentação /swagger-ui/index.html";
    }
}
