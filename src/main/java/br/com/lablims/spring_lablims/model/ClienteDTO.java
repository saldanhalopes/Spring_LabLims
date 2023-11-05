package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ClienteDTO {

    private Integer id;

    @Size(max = 255)
    @NotNull
    @NotBlank
    private String cliente;

    @Size(max = 255)
    private String descricao;

    private Short version;

}
