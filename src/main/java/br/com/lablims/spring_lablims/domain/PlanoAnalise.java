package br.com.lablims.spring_lablims.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;


@Entity
@Getter
@Setter
@Audited(withModifiedFlag = true)
public class PlanoAnalise {

    @Version
    private Short version;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String descricao;

    @Column
    private Integer leadTimeSetup;

    @Column
    private Integer leadTimeAnalise;

    @Column
    private Integer leadTimeLimpeza;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metodologia_versao_id")
    private MetodologiaVersao metodologiaVersao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analise_id")
    private Analise analise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analise_tecnica_id")
    private AnaliseTecnica analiseTecnica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_id")
    private Setor setor;

}
