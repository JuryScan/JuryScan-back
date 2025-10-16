package unicap.juryscan.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import unicap.juryscan.dto.userAdvogado.UserAdvogadoCreateDTO;
import unicap.juryscan.dto.userComum.UserComumCreateDTO;

import unicap.juryscan.service.userAdvogado.UserAdvogadoService;
import unicap.juryscan.service.userComum.UserComumService;

import java.sql.Date;
import java.time.LocalDate;

//TODO Fazer initializer para criar tres tipos de usuario em ambiente de desenvolvimento, comum, advogado e admin
//TODO adicionar password encoder
@Component
public class DataInitializer implements CommandLineRunner {

    @Value("${api.data-load.create-default-users}")
    private boolean createDefaultUsers;

    @Value("${api.data-load.create-dump-data}")
    private boolean createDumpData;

    private final UserComumService userComumService;
    private final UserAdvogadoService userAdvogadoService;

    public DataInitializer(UserComumService userComumService, UserAdvogadoService userAdminService) {
        this.userAdvogadoService = userAdminService;
        this.userComumService = userComumService;
    }

    @Override
    public void run(String... args) throws Exception {

        if (createDefaultUsers){
            System.out.println("🔧 COMMAND LINE RUNNER : Creating default users...");
            createDefaultUsers();
            System.out.println("🔧 COMMAND LINE RUNNER : Default users created.");
        }

        if (createDumpData){
            System.out.println("🔧 COMMAND LINE RUNNER : Creating dump data...");
            createDumpData();
            System.out.println("🔧 COMMAND LINE RUNNER : Dump data created.");
        }
    }

    private void createDefaultUsers(){
        // User comum
        UserComumCreateDTO userComumCreateDTO = new UserComumCreateDTO();
        userComumCreateDTO.setNomeCompleto("usuarioDev");

        userComumCreateDTO.setSenha("123");
        userComumCreateDTO.setCpf("123456789");
        userComumCreateDTO.setEmail("usuariodev@email.com");
        userComumCreateDTO.setTelefone("81999999999");
        userComumCreateDTO.setDataNascimento(Date.valueOf(LocalDate.of(2000,1,1)));

        // User advogado
        UserAdvogadoCreateDTO userAdvogadoCreateDTO = new UserAdvogadoCreateDTO();
        userAdvogadoCreateDTO.setNomeCompleto("advogadoDev");
        userAdvogadoCreateDTO.setSenha("123");
        userAdvogadoCreateDTO.setCpf("987654321");
        userAdvogadoCreateDTO.setEmail("advogadodev@email.com");
        userAdvogadoCreateDTO.setTelefone("81988888888");
        userAdvogadoCreateDTO.setDataNascimento(Date.valueOf(LocalDate.of(1990,1,1)));
        userAdvogadoCreateDTO.setDescricao("Advogado experiente");
        userAdvogadoCreateDTO.setExperiencia("10 anos");
        userAdvogadoCreateDTO.setNumeroOab("OAB123456");

        userComumService.createUserComum(userComumCreateDTO);
        userAdvogadoService.createUserAdvogado(userAdvogadoCreateDTO);
    }

    private void createDumpData(){

        for (int i=0; i<100; i++){
            UserComumCreateDTO userComumCreateDTO = new UserComumCreateDTO();
            userComumCreateDTO.setNomeCompleto("user"+i);
            userComumCreateDTO.setSenha("123");
            userComumCreateDTO.setCpf("123456789"+i);
            userComumCreateDTO.setEmail("user"+i+"@email.com");
            userComumCreateDTO.setTelefone("8199999999");
            userComumCreateDTO.setDataNascimento(Date.valueOf(LocalDate.of(2000,1,1)));

            userComumService.createUserComum(userComumCreateDTO);
        }
    }
}