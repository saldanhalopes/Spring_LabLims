package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SolucaoParemetroDTO {

    private Integer id;

    @Size(max = 255)
    private String paremetro;

    @Size(max = 255)
    private String valor;

    private Integer solucaoRegistro;

    private Short version;
}
