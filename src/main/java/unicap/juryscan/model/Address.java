package unicap.juryscan.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import unicap.juryscan.enums.TipoEnderecoEnum;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tb_endereco")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tipo_endereco")
    @Enumerated(EnumType.STRING)
    private TipoEnderecoEnum tipoEndereco;

    private String rua;
    private String cidade;
    private String bairro;
    private String estado;
    private String cep;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_usuario")
    private User usuario;
}
