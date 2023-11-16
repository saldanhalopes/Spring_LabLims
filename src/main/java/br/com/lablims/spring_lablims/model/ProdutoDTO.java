package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ProdutoDTO {

    private Integer id;

    private Boolean controleEspecial;

    @Size(max = 255)
    private String fiscalizado;

    private Integer codigo;

    @Size(max = 255)
    private String produto;

    private Integer tipoProduto;

    private Short version;

}
