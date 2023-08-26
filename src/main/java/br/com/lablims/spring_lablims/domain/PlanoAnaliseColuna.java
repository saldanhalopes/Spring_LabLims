package br.com.lablims.spring_lablims.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;


@Entity
@Getter
@Setter
@Audited(withModifiedFlag = true)
public class PlanoAnaliseColuna {

    @Version
    private Short version;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Integer qtdUtilizada;

    @Column
    private String obs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plano_analise_id")
    private PlanoAnalise planoAnalise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coluna_id")
    private Coluna coluna;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id")
    private UnidadeMedida unidade;

}
