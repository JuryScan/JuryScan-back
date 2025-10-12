package unicap.juryscan.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unicap.juryscan.model.Failure;

import java.util.UUID;

@Repository
public interface FailureRepository extends JpaRepository<Failure, UUID> {

    Page<Failure> findAllByAnaliseId(UUID analiseId, Pageable pageable);
}
