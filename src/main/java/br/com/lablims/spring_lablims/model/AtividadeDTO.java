package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AtividadeDTO {

    private Integer id;

    @Size(max = 255)
    private String atividade;

    private boolean produtivo;

    @Size(max = 10)
    @NotNull
    private String sigla;

    @Size(max = 255)
    private String descricao;

    @Size(max = 7)
    @NotNull
    private String cor;

    private Short version;

}
