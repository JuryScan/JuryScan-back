package unicap.juryscan.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import unicap.juryscan.model.Transaction;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Optional<Transaction> findByStripeCheckoutId(String stripeCheckoutId);

    Page<Transaction> findAllByUsuarioId(UUID usuarioId, Pageable pageable);
}

