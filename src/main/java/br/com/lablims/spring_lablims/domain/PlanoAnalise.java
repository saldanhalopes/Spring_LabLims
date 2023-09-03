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
    private Integer leadTimeMin;

    @Column
    private Integer leadTimeMedio;

    @Column
    private Integer leadTimeMax;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metodologia_versao_id")
    private MetodologiaVesao metodologiaVersao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analise_id")
    private Analise analise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analise_tipo_id")
    private AnaliseTipo analiseTipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_id")
    private Setor setor;

}
