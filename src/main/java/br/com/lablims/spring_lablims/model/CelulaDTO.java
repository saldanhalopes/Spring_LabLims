package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CelulaDTO {

    private Integer id;

    @Size(max = 255)
    private String celula;

    @Size(max = 255)
    private String descricao;

    private List<Integer> equipamento;

    private List<Integer> usuario;

    private Integer tipo;

    private Short version;

}
