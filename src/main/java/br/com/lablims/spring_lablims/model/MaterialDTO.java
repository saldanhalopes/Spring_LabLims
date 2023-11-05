package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MaterialDTO {

    private Integer id;

    private Boolean controleEspecial;

    @Size(max = 255)
    private String fiscalizado;

    private Integer codigo;

    @Size(max = 255)
    private String material;

    private Integer tipoMaterial;

    private Short version;

}
