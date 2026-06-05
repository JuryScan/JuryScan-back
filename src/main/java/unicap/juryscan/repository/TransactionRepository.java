package unicap.juryscan.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import unicap.juryscan.enums.TipoTransacaoEnum;
import unicap.juryscan.model.Transaction;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Optional<Transaction> findByStripeCheckoutId(String stripeCheckoutId);

    Page<Transaction> findAllByUsuarioId(UUID usuarioId, Pageable pageable);

    // ===== Agregações para o dashboard do advogado =====

    @Query("SELECT COALESCE(SUM(t.quantidade), 0) FROM tb_transacao t " +
            "WHERE t.usuario.id = :uid AND t.tipoTransacao = :tipo")
    long sumQuantidadeByUsuarioAndTipo(@Param("uid") UUID uid, @Param("tipo") TipoTransacaoEnum tipo);
}

