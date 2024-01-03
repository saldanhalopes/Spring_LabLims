package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class StorageDTO {

    private Integer id;

    @Size(max = 255)
    private String storage;

    @Size(max = 255)
    private String obs;

    private Integer setor;

    private String setorNome;

    private Integer tipo;

    private String tipoNome;

    private Short version;

}
