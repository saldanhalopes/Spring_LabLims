package br.com.lablims.spring_lablims.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.br.CNPJ;


@Entity
@Getter
@Setter
@Audited(withModifiedFlag = true)
public class Fornecedor {

    @Version
    private Short version;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String fornecedor;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column
    private String cnpj;

    @Column
    private String obs;

}
