package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ColunaDTO {

    private Integer id;

    @Size(max = 255)
    private String codigo;

    @Size(max = 255)
    private String partNumber;

    @Size(max = 255)
    private String obs;

    private Integer tipoColuna;

    private Integer fabricanteColuna;

    private Integer marcaColuna;

    private Integer faseColuna;

    private Integer tamanhoColuna;

    private Integer diametroColuna;

    private Integer particulaColuna;

    private Short version;

}
