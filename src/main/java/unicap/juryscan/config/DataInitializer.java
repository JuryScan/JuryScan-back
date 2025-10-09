package unicap.juryscan.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
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
            createDefaultUsers();
            System.out.println("🔧 COMMAND LINE RUNNER : Default users created.");
        }

        if (createDumpData){
            createDumpData();
            System.out.println("🔧 COMMAND LINE RUNNER : Dump data created.");
        }
    }

    private void createDefaultUsers(){
        UserComumCreateDTO userComumCreateDTO = new UserComumCreateDTO();
        userComumCreateDTO.setNomeCompleto("user1");
        userComumCreateDTO.setSenha("123456");
        userComumCreateDTO.setCpf("123456789");
        userComumCreateDTO.setEmail("user1@email.com");
        userComumCreateDTO.setTelefone("81999999999");
        userComumCreateDTO.setDataNascimento(Date.valueOf(LocalDate.of(2000,1,1)));

        UserAdvogadoCreateDTO userAdvogadoCreateDTO = new UserAdvogadoCreateDTO();
        userAdvogadoCreateDTO.setNomeCompleto("advogado1");
        userAdvogadoCreateDTO.setSenha("123456");
        userAdvogadoCreateDTO.setCpf("987654321");
        userAdvogadoCreateDTO.setEmail("advogado1@email.com");
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
            userComumCreateDTO.setSenha("123456");
            userComumCreateDTO.setCpf("123456789"+i);
            userComumCreateDTO.setEmail("user"+i+"@email.com");
            userComumCreateDTO.setTelefone("8199999999");
            userComumCreateDTO.setDataNascimento(Date.valueOf(LocalDate.of(2000,1,1)));

            userComumService.createUserComum(userComumCreateDTO);
        }
    }
}
