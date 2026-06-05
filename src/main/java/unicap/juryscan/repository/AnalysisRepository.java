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
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, UUID> {

    @Query("SELECT DISTINCT a FROM tb_analise a LEFT JOIN FETCH a.falhas WHERE a.usuario.id = :usuarioId")
    Page<Analysis> findAllByUsuarioId(@Param("usuarioId") UUID usuarioId, Pageable pageable);

    @Query("SELECT a FROM tb_analise a LEFT JOIN FETCH a.falhas WHERE a.id = :id")
    Optional<Analysis> findById(@Param("id") UUID id);

    @Query("SELECT COUNT(a) FROM tb_analise a WHERE a.usuario.id = :usuarioId AND a.dataCriacao >= :dataInicio")
    long countByUsuarioIdAndDataCriacaoAfter(@Param("usuarioId") UUID usuarioId, @Param("dataInicio") Timestamp dataInicio);

    @Query("SELECT COUNT(a) FROM tb_analise a WHERE a.usuario.id = :usuarioId")
    long countByUsuarioId(@Param("usuarioId") UUID usuarioId);

    @Query("SELECT a.dataCriacao FROM tb_analise a WHERE a.usuario.id = :usuarioId AND a.dataCriacao >= :inicio")
    List<Timestamp> findDatasCriacaoDesde(@Param("usuarioId") UUID usuarioId, @Param("inicio") Timestamp inicio);
}