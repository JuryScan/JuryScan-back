package unicap.juryscan.model;

import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import unicap.juryscan.enums.UserStatusEnum;
import unicap.juryscan.enums.TipoUserEnum;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tb_usuario")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tipo_usuario")
    @Enumerated(EnumType.STRING)
    private TipoUserEnum tipoUsuario;

    @Column(name = "nome_completo")
    private String nomeCompleto;

    private String email;

    private String telefone;

    private String senha;

    private String emailRecuperacao;

    private Date dataNascimento;

    private String cpf;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatusEnum status;

    private Boolean emailVerificado;

    @Column(name = "data_criacao")
    @CreationTimestamp
    private Timestamp dataCriacao;
    @Column(name = "data_ultima_atualizacao")
    @UpdateTimestamp
    private Timestamp dataUltimaAtualizacao;

    // dados do advogado
    private String descricao;
    @Column(name = "numero_oab")
    private String numeroOab;
    private String experiencia;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_endereco")
    private Address endereco;
}
