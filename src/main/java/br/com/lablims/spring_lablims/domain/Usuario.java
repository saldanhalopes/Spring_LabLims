package br.com.lablims.spring_lablims.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Set;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.security.core.userdetails.UserDetails;


@Entity
@Getter
@Setter
@Audited(withModifiedFlag = true)
@NamedEntityGraph(name = "usuario.grupo",
        attributeNodes = @NamedAttributeNode("grupo")
)
public class Usuario {

    @Version
    private Short version;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    @Email
    private String email;

    @Column
    @ColumnDefault("false")
    private boolean token;

    @Column
    @ColumnDefault("false")
    private boolean ativo;

    @Column
    private String secret;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    @Column
    private String nome;

    @Column
    private String sobrenome;

    @Column
    private String cpf;

    @Column
    private String pais;

    @Column
    private String estado;

    @Column
    private String cidade;

    @Column
    private String endereco;

    @Column
    private String cep;

    @Column
    private String telefone;

    @Column
    private String detalhes;

    @Column
    private Boolean changePass;

    @Column
    private Integer failedAccessCount;

    @Column
    private LocalDateTime lastChangePass;

    @Column
    private LocalDateTime lastLogin;

    @Column
    private LocalDateTime lastLogout;

}
