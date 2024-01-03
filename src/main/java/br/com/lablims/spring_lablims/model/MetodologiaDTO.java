package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class MetodologiaDTO {

    private Integer id;

    @Size(max = 255)
    private String codigo;

    @Size(max = 255)
    private String metodo;

    @Size(max = 255)
    private String obs;

    private Integer categoriaMetodologia;

    private String categoriaMetodologiaNome;

    private Short version;

}
