package br.com.lablims.spring_lablims.domain;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;


@Entity
@Getter
@Setter
@Audited(withModifiedFlag = true)
public class SolucaoReagente {

    @Version
    private Short version;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String lote;

    @Column
    private Double qtd;

    @Column
    private String fabricante;

    @Column
    private LocalDate validade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solucao_registro_id")
    private SolucaoRegistro solucaoRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reagente_id")
    private Reagente reagente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id")
    private UnidadeMedida unidade;

}
