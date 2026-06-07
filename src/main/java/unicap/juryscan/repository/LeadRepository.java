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

    // Query customizada com JOIN FETCH para carregar usuarioCliente e analise
    @Query("SELECT l FROM tb_lead l " +
           "JOIN FETCH l.usuarioCliente " +
           "JOIN FETCH l.analise " +
           "WHERE l.status = :status")
    Page<Lead> findByStatusWithDetails(@Param("status") StatusLeadEnum status, Pageable pageable);

    Page<Lead> findByAdvogadoIdAndStatus(UUID advogadoId, StatusLeadEnum status, Pageable pageable);

    Page<Lead> findByAdvogadoId(UUID advogadoId, Pageable pageable);

    // Query customizada com JOIN FETCH para leads do advogado
    @Query("SELECT l FROM tb_lead l " +
           "JOIN FETCH l.usuarioCliente " +
           "JOIN FETCH l.analise " +
           "LEFT JOIN FETCH l.advogado " +
           "WHERE l.advogado.id = :advogadoId")
    Page<Lead> findByAdvogadoIdWithDetails(@Param("advogadoId") UUID advogadoId, Pageable pageable);

    Page<Lead> findByUsuarioClienteId(UUID clienteId, Pageable pageable);

    // Query customizada com JOIN FETCH para leads do cliente
    @Query("SELECT l FROM tb_lead l " +
           "JOIN FETCH l.usuarioCliente " +
           "JOIN FETCH l.analise " +
           "LEFT JOIN FETCH l.advogado " +
           "WHERE l.usuarioCliente.id = :clienteId")
    Page<Lead> findByUsuarioClienteIdWithDetails(@Param("clienteId") UUID clienteId, Pageable pageable);

    // Query para buscar por ID com todos os detalhes
    @Query("SELECT l FROM tb_lead l " +
           "JOIN FETCH l.usuarioCliente " +
           "JOIN FETCH l.analise a " +
           "JOIN FETCH a.usuario " +
           "LEFT JOIN FETCH l.advogado " +
           "WHERE l.id = :leadId")
    Optional<Lead> findByIdWithAllDetails(@Param("leadId") UUID leadId);

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

