package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


@Getter
@Setter
public class SistemaDTO {

    private Integer id;

    private Short version;

    @Size(max = 255)
    private String sistemaNome;

    @Size(max = 255)
    private String sistemaCriador;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDate dataUpdate;

    private Double builderVersao;

    @Size(max = 2555)
    private String detalhes;

}
