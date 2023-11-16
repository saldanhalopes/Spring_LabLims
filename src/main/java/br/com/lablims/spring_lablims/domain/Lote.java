package br.com.lablims.spring_lablims.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.Set;


@Entity
@Getter
@Setter
@Audited(withModifiedFlag = true)
public class Lote {

    @Version
    private Short version;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String lote;

    @Column
    private Double tamanhoLote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id")
    private UnidadeMedida unidade;

    @Column
    private LocalDate dataFabricacao;

    @Column
    private LocalDate dataValidade;

    @Column
    private String localFabricacao;

    @Column
    private String obs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToMany
    @JoinTable(
            name = "lote_arquivo",
            joinColumns = @JoinColumn(name = "lote_id"),
            inverseJoinColumns = @JoinColumn(name = "arquivo_id")
    )
    private Set<Arquivos> arquivos;

}
