package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class GrupoDTO {

    private Integer id;

    @Size(max = 255)
    private String grupo;

    @Size(max = 255)
    private String tipo;

    @Size(max = 255)
    private String regra;

    private Short version;

}
