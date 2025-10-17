package unicap.juryscan.model;

import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import unicap.juryscan.enums.UserStatusEnum;
import unicap.juryscan.enums.TipoUserEnum;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tb_usuario")
public class User implements UserDetails {
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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "id_endereco")
    private Address endereco;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Analysis> analises;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.tipoUsuario == TipoUserEnum.ADMIN)
            return List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_ADVOGADO"),
                    new SimpleGrantedAuthority("ROLE_COMUM"));
        else if (this.tipoUsuario == TipoUserEnum.ADVOGADO)
            return List.of(new SimpleGrantedAuthority("ROLE_ADVOGADO"));
        else return List.of(new SimpleGrantedAuthority("ROLE_COMUM"));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
