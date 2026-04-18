package unicap.juryscan.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import unicap.juryscan.enums.TipoTransacaoEnum;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tb_transacao")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private User usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_transacao")
    private TipoTransacaoEnum tipoTransacao;

    private Integer quantidade;

    @Column(name = "stripe_checkout_id")
    private String stripeCheckoutId;

    @CreationTimestamp
    @Column(name = "data_criacao")
    private Timestamp dataCriacao;
}