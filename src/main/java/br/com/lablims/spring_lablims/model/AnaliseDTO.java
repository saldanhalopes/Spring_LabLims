package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AnaliseDTO {

    private Integer id;

    @Size(max = 255)
    private String analise;

    @Size(max = 255)
    private String descricaoAnalise;

    @Size(max = 255)
    private String siglaAnalise;

    private Short version;

}
