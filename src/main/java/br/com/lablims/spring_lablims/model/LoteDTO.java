package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class LoteDTO {

    private Integer id;

    @Size(max = 255)
    @NotBlank
    @NotNull
    private String lote;

    private Double tamanhoLote;

    private Integer unidade;

    private String unidadeName;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataFabricacao;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataValidade;

    @Size(max = 255)
    private String localFabricacao;

    @Size(max = 255)
    private String obs;

    @NotNull
    private Integer material;

    private String  materialName;

    @NotNull
    private Integer cliente;

    private String clienteName;

    private Short version;

    private List<Integer> arquivos;


}
