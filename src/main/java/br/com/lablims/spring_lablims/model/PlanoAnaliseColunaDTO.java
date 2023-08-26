package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PlanoAnaliseColunaDTO {

    private Integer id;

    private Integer qtdUtilizada;

    @Size(max = 255)
    private String obs;

    private Integer planoAnalise;

    private Integer coluna;

    private Integer unidade;

    private Short version;

}
