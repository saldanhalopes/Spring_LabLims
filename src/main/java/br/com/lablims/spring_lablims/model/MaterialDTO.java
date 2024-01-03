package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Digits;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
public class MaterialDTO {

    private Integer id;

    //    @NotNull
//    @NotBlank
    private String codigo;

    //    @NotNull
//    @NotBlank
    private String material;

    private boolean ativo;

    private Short version;

    //    @NotNull
    private Integer produtoTipo;

    private String produtoTipoNome;

    //    @NotNull
    private Integer fornecedor;

    private String fornecedorNome;

    //    @NotNull
    private Integer unidade;

    private String unidadeNome;

    //    @NotNull
    @Digits(integer = 6, fraction = 6)
    private BigDecimal estoqueMin;

    //    @NotNull
    @Digits(integer = 6, fraction = 6)
    private BigDecimal estoqueMax;

    private double percentual;

    //    @Size(max = 2555)
    private String descricao;

    private Integer categoria;

    private String categoriaNome;

    private String partNumber;

    private String casNumber;

    private String serialNumber;

    private String numeroIdentificacao;

    private Integer fabricante;

    private String fabricanteNome;

    private String marca;

    private String modelo;

    private boolean compraUnica;

    private String grau;

    private String pureza;

    private String classe;

    private String controlado;

    private Integer saude;

    private Integer inflamabilidade;

    private Integer reatividade;

    private String especifico;

    private String obs;

}
