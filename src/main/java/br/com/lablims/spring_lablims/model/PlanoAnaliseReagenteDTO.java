package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PlanoAnaliseReagenteDTO {

    private Integer id;

    private Double qtdUtilizada;

    @Size(max = 255)
    private String obs;

    private Integer reagente;

    private Short version;

}
