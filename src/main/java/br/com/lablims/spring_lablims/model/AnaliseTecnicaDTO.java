package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AnaliseTecnicaDTO {

    private Integer id;

    @Size(max = 255)
    private String analiseTecnica;

    @Size(max = 255)
    private String descricaoAnaliseTecnica;

    private Short version;

}
