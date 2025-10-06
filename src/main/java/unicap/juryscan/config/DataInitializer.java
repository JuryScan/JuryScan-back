package unicap.juryscan.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import unicap.juryscan.dto.userComum.UserComumCreateDTO;
import unicap.juryscan.service.userComum.UserComumService;

import java.sql.Date;
import java.time.LocalDate;

//TODO Fazer initializer para criar tres tipos de usuario em ambiente de desenvolvimento, comum, advogado e admin
@Component
public class DataInitializer implements CommandLineRunner {

    @Value("${api.data-load.create-default-users}")
    private boolean createDefaultUsers;

    private final UserComumService userComumService;

    public DataInitializer(UserComumService userComumService) {
        this.userComumService = userComumService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (createDefaultUsers){
            createDefaultUsers();
            System.out.println("🔧 COMMAND LINE RUNNER : Default users created.");
        }
    }

    private void createDefaultUsers(){
        UserComumCreateDTO userComumCreateDTO = new UserComumCreateDTO();
        userComumCreateDTO.setNomeCompleto("user1");
        userComumCreateDTO.setSenha("123456");
        userComumCreateDTO.setCpf("123456789");
        userComumCreateDTO.setEmail("pg077685@gmail.com");
        userComumCreateDTO.setTelefone("81999999999");
        userComumCreateDTO.setDataNascimento(Date.valueOf(LocalDate.of(2000,1,1)));

        userComumService.createUserComum(userComumCreateDTO);
    }
}
