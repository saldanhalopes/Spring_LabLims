package br.com.lablims.spring_lablims.model;

import br.com.lablims.spring_lablims.domain.Categoria;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AtributoDTO {

    private Integer id;

    private Integer categoria;

    private String categoriaNome;

    @Size(max = 255)
    private String atributo;

    @Size(max = 255)
    private String valor;

    @Size(max = 255)
    private String descricao;

    private Short version;

}
