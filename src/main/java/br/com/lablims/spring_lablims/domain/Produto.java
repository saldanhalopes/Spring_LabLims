package br.com.lablims.spring_lablims.domain;

import jakarta.persistence.*;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;


@Entity
@Getter
@Setter
@Audited(withModifiedFlag = true)
public class Produto {

    @Version
    private Short version;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Boolean controleEspecial;

    @Column
    private String fiscalizado;

    @Column
    private Integer codigo;

    @Column
    private String produto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_produto_id")
    private ProdutoTipo tipoProduto;

    @ManyToMany(mappedBy = "produto")
    private Set<MetodologiaVersao> metodologiaVersao;

}
