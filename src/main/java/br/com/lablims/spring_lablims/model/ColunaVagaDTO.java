package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ColunaVagaDTO {

    private Integer id;

    private Integer vaga;

    @Size(max = 255)
    private String obs;

    private Integer colunaStorage;

    private Short version;

}
