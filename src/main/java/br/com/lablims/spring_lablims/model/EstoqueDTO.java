package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
public class EstoqueDTO {

    private Integer id;

    private Short version;

    @NotNull
    private String movimentacaoTipo;

    @NotNull
    private Integer material;

    private String materialNome;

    @NotNull
    private Integer unidade;

    private String unidadeNome;

    @NotNull
    private Integer setor;

    private String setorNome;

    @NotNull
    private BigDecimal qtdMaterial;

    private Double valorUnitario;

}
