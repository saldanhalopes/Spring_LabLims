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

    private Integer leadTimeSetup;

    private Integer leadTimeAnalise;

    private Integer leadTimeLimpeza;

    private Integer metodologiaVersao;

    private Integer analise;

    private String analiseNome;

    private String analiseTipo;

    private Integer analiseTecnica;

    private String analiseTecnicaNome;

    private Integer setor;

    private Short version;

}
