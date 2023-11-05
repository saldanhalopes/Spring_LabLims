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
public class AmostraStatus {

    @Version
    private Short version;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private LocalDateTime dataStatus;

    @Column
    private LocalDateTime dataInicioAnalise;

    @Column
    private LocalDateTime dataFimAnalise;

    @Column
    private LocalDateTime dataNecessidade;

    @Column
    private LocalDateTime dataPrevisaoLiberacao;

    @Column
    private LocalDateTime dataProgramado;

    @Column
    private LocalDateTime dataConferencia1;

    @Column
    private LocalDateTime dataConferencia2;

    @Column
    private String obs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "amostra_id")
    private Amostra amostra;

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
