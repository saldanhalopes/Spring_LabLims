package br.com.lablims.spring_lablims.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;


@Entity
@Getter
@Setter
@Audited(withModifiedFlag = true)
public class SolucaoRegistro {

    @Version
    private Short version;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String descricao;

    @Column
    private LocalDateTime lote;

    @Column
    private String tipo;

    @Column
    private String referencia;

    @Column
    private String armazenamento;

    @Column
    private LocalDateTime dataPreparo;

    @Column
    private LocalDateTime dataValidade;

    @Column
    private LocalDateTime dataConferencia;

    @Column
    private Double qtd;

    @Column
    private Boolean ativo;

    @Column
    private String obs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solucao_tipo_id")
    private SolucaoTipo solucaoTipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criador_id")
    private Usuario criador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conferente_id")
    private Usuario conferente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id")
    private UnidadeMedida unidade;

}
