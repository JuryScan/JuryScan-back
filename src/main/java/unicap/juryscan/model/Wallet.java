package unicap.juryscan.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tb_carteira")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", referencedColumnName = "id")
    private User usuario;

    private Integer saldo;

    @Column(name = "data_criacao")
    @CreationTimestamp
    private Timestamp dataCriacao;
    @Column(name = "data_ultima_atualizacao")
    @UpdateTimestamp
    private Timestamp dataUltimaAtualizacao;
}