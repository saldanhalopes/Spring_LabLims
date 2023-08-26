package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AnaliseStatusDTO {

    private Integer id;

    @Size(max = 255)
    private String analiseStatus;

    @Size(max = 255)
    private String siglaAnaliseStatus;

    @Size(max = 255)
    private String descricaoAnaliseStatus;

    private Integer analiseProdutividade;

    private Short version;
}
