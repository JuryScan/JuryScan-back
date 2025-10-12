package unicap.juryscan.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import unicap.juryscan.enums.SeveridadeFalhaEnum;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tb_falha")
public class Failure {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String titulo;

    @Enumerated(EnumType.STRING)
    private SeveridadeFalhaEnum severidade;

    private String descricao;

    private Float confianca;

    @Column(name = "sugestao_correcao")
    private String sugestaoCorrecao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_analise")
    private Analysis analise;
}
