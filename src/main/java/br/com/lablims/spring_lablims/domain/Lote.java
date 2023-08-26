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
public class Lote {

    @Version
    private Short version;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String lote;

    @Column
    private Double qtdEstoque;

    @Column
    private LocalDateTime dataStatus;

    @Column
    private LocalDateTime dataEntrada;

    @Column
    private LocalDateTime dataInicioAnalise;

    @Column
    private LocalDateTime dataLiberacao;

    @Column
    private LocalDateTime dataEnvioGarantia;

    @Column
    private LocalDateTime dataNecessidade;

    @Column
    private LocalDateTime dataValidade;

    @Column
    private LocalDateTime dataImpressao;

    @Column
    private String numeroDocumento;

    @Column
    private String complemento;

    @Column
    private String obs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id")
    private Material material;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "amostra_tipo_id")
    private AmostraTipo amostraTipo;

}
