package unicap.juryscan.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unicap.juryscan.enums.StatusLeadEnum;
import unicap.juryscan.model.Lead;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeadRepository extends JpaRepository<Lead, UUID> {

    Page<Lead> findByStatus(StatusLeadEnum status, Pageable pageable);

    Page<Lead> findByAdvogadoIdAndStatus(UUID advogadoId, StatusLeadEnum status, Pageable pageable);

    Page<Lead> findByAdvogadoId(UUID advogadoId, Pageable pageable);

    Page<Lead> findByUsuarioClienteId(UUID clienteId, Pageable pageable);

    Optional<Lead> findByAnaliseId(UUID analysisId);

    boolean existsByAnaliseId(UUID analysisId);

    long countByAdvogadoIdAndStatus(UUID advogadoId, StatusLeadEnum status);

    long countByStatus(StatusLeadEnum status);

    @Query("SELECT COUNT(l) FROM tb_lead l WHERE l.advogado.id = :advogadoId AND l.status = :status AND l.dataAquisicao >= :inicio")
    long countByAdvogadoAndStatusAndDataAquisicaoApos(@Param("advogadoId") UUID advogadoId,
                                                      @Param("status") StatusLeadEnum status,
                                                      @Param("inicio") Timestamp inicio);

    @Query("SELECT COUNT(DISTINCT l.usuarioCliente.id) FROM tb_lead l WHERE l.advogado.id = :advogadoId AND l.status = :status")
    long countDistinctClientesByAdvogadoAndStatus(@Param("advogadoId") UUID advogadoId,
                                                  @Param("status") StatusLeadEnum status);

    @Query("SELECT COALESCE(SUM(l.custoCreditos), 0) FROM tb_lead l WHERE l.advogado.id = :advogadoId AND l.status = :status")
    long sumCustoCreditosByAdvogadoAndStatus(@Param("advogadoId") UUID advogadoId,
                                             @Param("status") StatusLeadEnum status);

    @Query("SELECT l.dataAquisicao FROM tb_lead l WHERE l.advogado.id = :advogadoId AND l.status = :status AND l.dataAquisicao >= :inicio")
    List<Timestamp> findDatasAquisicaoDesde(@Param("advogadoId") UUID advogadoId,
                                            @Param("status") StatusLeadEnum status,
                                            @Param("inicio") Timestamp inicio);
}

