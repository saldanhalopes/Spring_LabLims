package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AtaTurnoDTO {

    private Integer id;

    private String passagem;

    @Size(max = 255)
    private String agrupador;

    private Integer turno;

    private Integer setor;

    private Integer usuario;

    private List<Integer> equipamentos;

    private Short version;

}
