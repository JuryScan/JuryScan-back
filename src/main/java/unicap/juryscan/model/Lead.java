package unicap.juryscan.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import unicap.juryscan.enums.StatusLeadEnum;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tb_lead")
public class Lead {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_cliente", nullable = false)
    private User usuarioCliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_analise", nullable = false)
    private Analysis analise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_advogado")
    private User advogado;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusLeadEnum status;

    @Column(name = "custo_creditos", nullable = false)
    private Integer custoCreditos;

    @CreationTimestamp
    @Column(name = "data_criacao")
    private Timestamp dataCriacao;

    @Column(name = "data_aquisicao")
    private Timestamp dataAquisicao;

    @Column(name = "data_expiracao")
    private Timestamp dataExpiracao;
}

