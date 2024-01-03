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
public class MetodologiaVersao {

    @Version
    private Short version;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Double revisao;

    @Column
    private LocalDateTime dataRevisao;

    @Column
    private LocalDateTime dataProximaRevisao;

    @Column
    private String obs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metodologia_id")
    private Metodologia metodologia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anexo_id")
    private Arquivos anexo;

    @ManyToMany
    @JoinTable(
            name = "metodologia_vesao_produto",
            joinColumns = @JoinColumn(name = "metodologia_vesao_id"),
            inverseJoinColumns = @JoinColumn(name = "produto_id")
    )
    private Set<Produto> produto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id")
    private MetodologiaStatus status;

}
