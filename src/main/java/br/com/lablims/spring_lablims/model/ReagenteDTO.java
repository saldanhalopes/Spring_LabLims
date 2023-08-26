package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ReagenteDTO {

    private Integer id;

    @Size(max = 255)
    private String codigo;

    @Size(max = 255)
    private String reagente;

    @Size(max = 255)
    private String casNumber;

    private Integer qtdEstoqueMin;

    private Integer qtdEstoqueMax;

    private Boolean compraUnica;

    @Size(max = 255)
    private String enderecamento;

    @Size(max = 255)
    private String numeroIdentificacao;

    @Size(max = 255)
    private String armazenamento;

    @Size(max = 255)
    private String grau;

    @Size(max = 255)
    private String pureza;

    @Size(max = 255)
    private String classe;

    @Size(max = 255)
    private String controlado;

    private Integer saude;

    private Integer inflamabilidade;

    private Integer reatividade;

    @Size(max = 255)
    private String especifico;

    @Size(max = 255)
    private String obs;

    private Integer unidade;

    private Integer fds;

    private Short version;

}
