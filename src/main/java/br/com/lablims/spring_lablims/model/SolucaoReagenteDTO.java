package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@Setter
public class SolucaoReagenteDTO {

    private Integer id;

    @Size(max = 255)
    private String lote;

    private Double qtd;

    @Size(max = 255)
    private String fabricante;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate validade;

    private Integer solucaoRegistro;

    private Integer reagente;

    private Integer unidade;

    private Short version;

}
