package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CNPJ;


@Getter
@Setter
public class FabricanteDTO {

    private Integer id;

    @Size(max = 255)
    @NotNull
    @NotBlank
    private String fabricante;

    @Size(max = 2555)
    private String descricao;

    @Size(max = 255)
    private String obs;

    private Short version;

}
