package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TurnoDTO {

    private Integer id;

    @Size(max = 255)
    private String turno;

    private Short version;

}
