package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ColunaStorageDTO {

    private Integer id;

    @Size(max = 255)
    private String codigo;

    @Size(max = 255)
    private String obs;

    private Integer setor;

    private Integer tipo;

    private Short version;

}
