package unicap.juryscan.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unicap.juryscan.enums.TipoUserEnum;
import unicap.juryscan.model.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Page<User> findAllByTipoUsuario(TipoUserEnum tipoUsuario, Pageable pageable);

    Optional<User> findByTipoUsuarioAndId(TipoUserEnum tipoUsuario, UUID id);
}