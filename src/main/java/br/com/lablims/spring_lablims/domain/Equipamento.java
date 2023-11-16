package br.com.lablims.spring_lablims.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;


@Entity
@Getter
@Setter
@DynamicUpdate()
@Audited(withModifiedFlag = true)
public class Equipamento {

    @Version
    private Short version;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String descricao;

    @Column
    private String tag;

    @Column
    private String fabricante;

    @Column
    private String marca;

    @Column
    private String modelo;

    @Column
    private LocalDate ultimaCalibracao;

    @Column
    private LocalDate proximaCalibracao;

    @Column
    private Boolean ativo;

    @Column
    private String obs;

    @NotAudited
    @Lob
    @Column()
    @Basic(fetch = FetchType.LAZY)
    private byte[] imagem;

    @Column
    private String serialNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_id")
    private EquipamentoTipo tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_id")
    private Setor setor;

    @ManyToMany
    @JoinTable(
            name = "equipamento_arquivo",
            joinColumns = @JoinColumn(name = "equipamento_id"),
            inverseJoinColumns = @JoinColumn(name = "arquivo_id")
    )
    private Set<Arquivos> arquivos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grandeza_id")
    private Grandeza grandeza;

}
