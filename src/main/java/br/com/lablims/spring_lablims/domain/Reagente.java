package br.com.lablims.spring_lablims.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;


@Entity
@Getter
@Setter
@Audited(withModifiedFlag = true)
public class Reagente {

    @Version
    private Short version;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String codigo;

    @Column
    private String reagente;

    @Column
    private String casNumber;

    @Column
    private Integer qtdEstoqueMin;

    @Column
    private Integer qtdEstoqueMax;

    @Column
    private Boolean compraUnica;

    @Column
    private String enderecamento;

    @Column
    private String numeroIdentificacao;

    @Column
    private String armazenamento;

    @Column
    private String grau;

    @Column
    private String pureza;

    @Column
    private String classe;

    @Column
    private String controlado;

    @Column
    private Integer saude;

    @Column
    private Integer inflamabilidade;

    @Column
    private Integer reatividade;

    @Column
    private String especifico;

    @Column
    private String obs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unidade_id")
    private UnidadeMedida unidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fds_id")
    private Arquivos fds;

}
