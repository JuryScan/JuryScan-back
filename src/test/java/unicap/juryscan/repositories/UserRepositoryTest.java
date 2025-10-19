package unicap.juryscan.repositories;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import unicap.juryscan.dto.userComum.UserComumCreateDTO;
import unicap.juryscan.enums.TipoUserEnum;
import unicap.juryscan.mapper.UserComumMapper;
import unicap.juryscan.model.User;
import unicap.juryscan.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;
import java.util.Optional;
import java.util.UUID;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager entityManager;

    private final UserComumMapper userComumMapper = new UserComumMapper();

    @Test
    @DisplayName("Deve retornar um usuário do tipo comum armazenado no banco de dados")
    void findByTipoUsuarioandIdCase1(){
        UserComumCreateDTO userComumCreateDTO = new UserComumCreateDTO("Carlos Pinheiro", "carlos@gmail.com", "81992448333", "123", new Date(28, 05, 2005), "11997494422");
        User user = userComumMapper.toEntity(userComumCreateDTO);
        user.setTipoUsuario(TipoUserEnum.COMUM);
        User createdUser = userRepository.save(user);

        Optional<User> result = userRepository.findByTipoUsuarioAndId(TipoUserEnum.COMUM, createdUser.getId());

        assertThat(result.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Não deve retornar um usuário do tipo comum quando naõ existir")
    void findByTipoUsuarioandIdCase2(){
        UUID nonExistentId = UUID.randomUUID();

        Optional<User> result = userRepository.findByTipoUsuarioAndId(TipoUserEnum.COMUM, nonExistentId);

        assertThat(result.isEmpty()).isTrue();
    }

    // Método auxiliar para crar novo usuário do tipo comum
    private User createUserComum(UserComumCreateDTO data){
        User user = userComumMapper.toEntity(data);
        entityManager.persist(user);
        return user;
    }
}
