package br.com.lablims.spring_lablims.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;


@Entity
@Getter
@Setter
@Audited(withModifiedFlag = true)
public class ColunaUtil {

    @Version
    private Short version;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String codigoColuna;

    @Column
    private String serialNumber;

    @Column
    private LocalDateTime dataAtivacao;

    @Column
    private LocalDateTime dataVerificacao;

    @Column
    private LocalDateTime dataDescarte;

    @Column
    private Boolean estoque;

    @Column
    private String obs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coluna_id")
    private Coluna coluna;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_id")
    private Setor setor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metodologia_versao_id")
    private MetodologiaVesao metodologiaVersao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analise_id")
    private Analise analise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coluna_vaga_id")
    private ColunaVaga colunaVaga;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certificado_id")
    private Arquivos certificado;

    @ManyToMany
    @JoinTable(
            name = "arquivos_coluna_util",
            joinColumns = @JoinColumn(name = "coluna_util_id"),
            inverseJoinColumns = @JoinColumn(name = "arquivos_id")
    )
    private Set<Arquivos> anexos;

}
