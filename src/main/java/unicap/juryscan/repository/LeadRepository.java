package unicap.juryscan.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unicap.juryscan.enums.StatusLeadEnum;
import unicap.juryscan.model.Lead;

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
}

