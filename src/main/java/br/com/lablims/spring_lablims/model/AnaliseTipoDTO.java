package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AnaliseTipoDTO {

    private Integer id;

    @Size(max = 255)
    private String analiseTipo;

    @Size(max = 255)
    private String siglaAnaliseTipo;

    @Size(max = 255)
    private String descricaoAnaliseTipo;

    private Short version;

}
