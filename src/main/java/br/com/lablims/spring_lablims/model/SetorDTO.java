package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SetorDTO {

    private Integer id;

    @Size(max = 255)
    private String setor;

    @Size(max = 255)
    private String siglaSetor;

    @Size(max = 255)
    private String descricao;

    @Size(max = 255)
    private String tipo;

    private Integer departamento;

    private Short version;

}
