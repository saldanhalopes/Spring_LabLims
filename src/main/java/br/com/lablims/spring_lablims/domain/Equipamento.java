package br.com.lablims.spring_lablims.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;


@Entity
@Getter
@Setter
@Audited(withModifiedFlag = true)
public class Equipamento {

    @Version
    private Short version;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String descricao;

    @Column
    private String tag;

    @Column
    private String fabricante;

    @Column
    private String marca;

    @Column
    private String modelo;

    @Column
    private LocalDateTime ultimaCalibracao;

    @Column
    private LocalDateTime proximaCalibracao;

    @Column
    private Boolean ativo;

    @Column
    private String obs;

    @NotAudited
    @Lob
    @Column(name = "imagem")
    @Basic(fetch = FetchType.LAZY)
    private byte[] imagem;

    @Column
    private String serialNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_id")
    private EquipamentoTipo tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_id")
    private Setor setor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certificado_id")
    private Arquivos certificado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manual_id")
    private Arquivos manual;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procedimento_id")
    private Arquivos procedimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escala_id")
    private EscalaMedida escala;

}
