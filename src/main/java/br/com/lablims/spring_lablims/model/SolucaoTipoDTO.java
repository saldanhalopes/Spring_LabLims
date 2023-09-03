package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SolucaoTipoDTO {

    private Integer id;

    @Size(max = 255)
    private String siglaSolucao;

    @Size(max = 255)
    private String tipoSolucao;

    private Short version;

}
