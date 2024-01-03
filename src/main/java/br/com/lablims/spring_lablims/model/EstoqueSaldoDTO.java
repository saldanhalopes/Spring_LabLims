package br.com.lablims.spring_lablims.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.NumberFormat;

import java.math.BigDecimal;


@Getter
@Setter
public class EstoqueSaldoDTO {

    private Integer id;

    private Short version;

    private Integer material;

    private String materialNome;

    private String materialUnidade;

    private Integer setor;

    private String setorNome;

    private String materialCategoria;

    private String materialFornecedor;

    private BigDecimal qtdMaterial;

    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private Double valorTotal;

}
