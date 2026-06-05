package unicap.juryscan.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unicap.juryscan.enums.StatusLeadEnum;
import unicap.juryscan.model.Failure;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface FailureRepository extends JpaRepository<Failure, UUID> {

    Page<Failure> findAllByAnaliseId(UUID analiseId, Pageable pageable);

    // ===== Agregações para o dashboard do advogado =====
    // Falhas das análises por trás dos leads adquiridos por um advogado.

    @Query("SELECT COUNT(f) FROM tb_falha f JOIN f.analise a WHERE EXISTS " +
            "(SELECT 1 FROM tb_lead l WHERE l.analise.id = a.id AND l.advogado.id = :advId AND l.status = :status)")
    long countByAdvogadoAcquiredLeads(@Param("advId") UUID advId, @Param("status") StatusLeadEnum status);

    @Query("SELECT a.dataCriacao FROM tb_falha f JOIN f.analise a WHERE a.dataCriacao >= :since AND EXISTS " +
            "(SELECT 1 FROM tb_lead l WHERE l.analise.id = a.id AND l.advogado.id = :advId AND l.status = :status)")
    List<Timestamp> findFailureAnalysisDatesSince(@Param("advId") UUID advId,
                                                  @Param("status") StatusLeadEnum status,
                                                  @Param("since") Timestamp since);
}
