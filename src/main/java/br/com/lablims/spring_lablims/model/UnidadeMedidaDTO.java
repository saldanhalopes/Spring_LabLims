package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UnidadeMedidaDTO {

    private Integer id;

    @Size(max = 255)
    private String unidade;

    private Integer escalaMedida;

    private Short version;

}
