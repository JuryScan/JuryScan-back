package unicap.juryscan.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unicap.juryscan.model.Analysis;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, UUID> {

    Page<Analysis> findAllByUsuarioId(UUID usuarioId, Pageable pageable);

    // ===== Agregações para o dashboard do advogado =====

    long countByUsuarioId(UUID usuarioId);

    long countByUsuarioIdAndDataCriacaoGreaterThanEqual(UUID usuarioId, Timestamp since);

    @Query("SELECT a.dataCriacao FROM tb_analise a " +
            "WHERE a.usuario.id = :advId AND a.dataCriacao >= :since")
    List<Timestamp> findAnalysisDatesSince(@Param("advId") UUID advId, @Param("since") Timestamp since);
}