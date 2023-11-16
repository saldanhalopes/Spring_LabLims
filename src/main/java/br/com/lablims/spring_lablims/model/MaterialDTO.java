package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
public class MaterialDTO {

    private Integer id;

    @Size(max = 50)
    @NotNull
    @NotBlank
    private String codigo;

    @Size(max = 255)
    @NotNull
    @NotBlank
    private String material;

    private boolean ativo;

    private Short version;

    @NotNull
    private Integer produtoTipo;

    private String produtoTipoNome;

    @NotNull
    private Integer fornecedor;

    private String fornecedorNome;

    @NotNull
    private Integer unidade;

    private String unidadeNome;

    @NotNull
    private BigDecimal estoqueMin;

    @NotNull
    private BigDecimal estoqueMax;

    private double percentual;

    @Size(max = 2555)
    private String descricao;

    private Integer materialTipo;

    private String materialTipoNome;

    private String partNumber;

    private String casNumber;

    private String serialNumber;

    private String numeroIdentificacao;

    private String fabricante;

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
