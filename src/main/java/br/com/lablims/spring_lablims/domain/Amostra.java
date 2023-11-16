package br.com.lablims.spring_lablims.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDateTime;
import java.util.Set;


@Entity
@Getter
@Setter
@Audited(withModifiedFlag = true)
public class Amostra {

    @Version
    private Short version;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String codigoAmostra;

    @Column
    private Boolean usoInterno;

    @Column
    private Double qtdAmostra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id")
    private UnidadeMedida unidade;

    @Column
    private String fracionamento;

    @Column
    private LocalDateTime dataEntrada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_responsavel_id")
    private Usuario usuarioResponsavel;

    @Column
    private String localArmazenamento;

    @Column
    private String condicoesArmazenamento;

    @Column
    private String pontoAmostragem;

    @Column
    private String complemento;

    @Column
    private String referenciaCliente;

    @Column
    private LocalDateTime dataLiberacaoCQ;

    @Column
    private LocalDateTime dataEnvioDocumentacao;

    @Column
    private LocalDateTime dataImpressao;

    @ManyToMany
    @Cascade(CascadeType.REMOVE)
    @JoinTable(
            name = "amostra_arquivo",
            joinColumns = @JoinColumn(name = "amostra_id"),
            inverseJoinColumns = @JoinColumn(name = "arquivo_id")
    )
    private Set<Arquivos> arquivos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private Lote lote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "amostra_tipo_id")
    private AmostraTipo amostraTipo;

    @Column
    private String obs;

}
