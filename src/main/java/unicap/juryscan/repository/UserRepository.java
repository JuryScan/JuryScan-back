package unicap.juryscan.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;
import unicap.juryscan.enums.TipoUserEnum;
import unicap.juryscan.model.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Page<User> findAllByTipoUsuario(TipoUserEnum tipoUsuario, Pageable pageable);

    @Query("SELECT u FROM tb_usuario u LEFT JOIN u.endereco e WHERE u.tipoUsuario = :tipo "
            + "AND (:busca IS NULL OR LOWER(u.nomeCompleto) LIKE LOWER(CONCAT('%', :busca, '%')) "
            + "OR LOWER(u.numeroOab) LIKE LOWER(CONCAT('%', :busca, '%'))) "
            + "AND (:cidade IS NULL OR LOWER(e.cidade) LIKE LOWER(CONCAT('%', :cidade, '%'))) "
            + "AND (:estado IS NULL OR LOWER(e.estado) = LOWER(:estado))")
    Page<User> searchAdvogados(@Param("tipo") TipoUserEnum tipo,
                               @Param("busca") String busca,
                               @Param("cidade") String cidade,
                               @Param("estado") String estado,
                               Pageable pageable);

    Optional<User> findByTipoUsuarioAndId(TipoUserEnum tipoUsuario, UUID id);

    UserDetails findByEmailIgnoreCase(String email);
}