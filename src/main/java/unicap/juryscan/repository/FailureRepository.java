package unicap.juryscan.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unicap.juryscan.model.Failure;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface FailureRepository extends JpaRepository<Failure, UUID> {

    Page<Failure> findAllByAnaliseId(UUID analiseId, Pageable pageable);

    @Query("SELECT COUNT(f) FROM tb_falha f WHERE f.analise.usuario.id = :usuarioId")
    long countByAnaliseUsuarioId(@Param("usuarioId") UUID usuarioId);

    @Query("SELECT f.analise.dataCriacao FROM tb_falha f WHERE f.analise.usuario.id = :usuarioId AND f.analise.dataCriacao >= :inicio")
    List<Timestamp> findDatasAnaliseDesde(@Param("usuarioId") UUID usuarioId, @Param("inicio") Timestamp inicio);
}
