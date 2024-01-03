package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.NumberFormat;

import java.math.BigDecimal;
import java.util.List;


@Getter
@Setter
public class EstoqueDTO {

    private Integer id;

    private Short version;

    private String movimentacaoTipo;

    @NotNull
    private Integer material;

    private String materialNome;

    private String materialUnidade;

    @NotNull
    private Integer setor;

    private String setorNome;

    @NotNull
    private BigDecimal qtdMaterial;

    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private Double valorUnitario;

    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private Double valorTotal;

    private String materialCategoria;

    private String materialFornecedor;

    private Integer storageEndereco;

    private String storageEnderecoNome;

    private List<Integer> arquivos;

}
