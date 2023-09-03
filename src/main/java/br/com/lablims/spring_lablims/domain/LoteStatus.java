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
public class LoteStatus {

    @Version
    private Short version;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private LocalDateTime dataStatus;

    @Column
    private LocalDateTime dataPrevisao;

    @Column
    private LocalDateTime dataProgramado;

    @Column
    private LocalDateTime dataConferencia1;

    @Column
    private LocalDateTime dataConferencia2;

    @Column
    private String obs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private Lote lote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plano_analise_id")
    private PlanoAnalise planoAnalise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analise_status_id")
    private AnaliseStatus analiseStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conferente1_id")
    private Usuario conferente1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conferente2_id")
    private Usuario conferente2;

}
