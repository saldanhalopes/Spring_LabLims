package br.com.lablims.spring_lablims.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;


@Entity
@Getter
@Setter
@Audited(withModifiedFlag = true)
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
    private String email;

    @ManyToMany
    @JoinTable(
            name = "usuario_grupo",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "grupo_id")
    )
    private Set<Grupo> grupos;

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
