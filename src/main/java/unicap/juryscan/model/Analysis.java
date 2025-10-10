package unicap.juryscan.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tb_analise")
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String titulo;

    @Column(name = "descricao_geral")
    private String descricaoGeral;

    @Column(name = "data_criacao")
    @CreationTimestamp
    private Timestamp dataCriacao;

    @OneToMany(mappedBy = "analise", cascade = CascadeType.ALL)
    private List<Failure> falhas;
}
