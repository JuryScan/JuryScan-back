package unicap.juryscan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unicap.juryscan.enums.TipoUserEnum;
import unicap.juryscan.model.User;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    List<User> findAllByTipoUsuario(TipoUserEnum tipoUsuario);
}