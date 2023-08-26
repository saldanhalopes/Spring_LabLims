package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AnaliseProdutividadeDTO {

    private Integer id;

    @Size(max = 255)
    private String analiseProdutividade;

    @Size(max = 255)
    private String siglaProdutividade;

    @Size(max = 255)
    private String descricaoProdutividade;

    @Size(max = 255)
    private String corProdutividade;

    private Short version;

}
