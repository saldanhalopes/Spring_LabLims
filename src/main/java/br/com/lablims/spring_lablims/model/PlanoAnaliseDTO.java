package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PlanoAnaliseDTO {

    private Integer id;

    @Size(max = 255)
    private String descricao;

    private Integer leadTimeMin;

    private Integer leadTimeMedio;

    private Integer leadTimeMax;

    private Integer metodologiaVersao;

    private Integer analise;

    private Integer analiseTipo;

    private Integer setor;

    private Short version;

}
