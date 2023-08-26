package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DepartamentoDTO {

    private Integer id;

    @Size(max = 255)
    private String departamento;

    @Size(max = 255)
    private String siglaDepartamento;

    @Size(max = 255)
    private String descricao;

    private Short version;

}
