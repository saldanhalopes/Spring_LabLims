package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


@Getter
@Setter
public class ColunaLogDTO {

    private Integer id;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataIncio;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataFim;

    private String sentido;

    private String precoluna;

    private String prefiltro;

    private Integer injecoes;

    private Integer colunaUtil;

    private Integer usuarioInicio;

    private Integer usuarioFim;

    private Integer campanha;

    private Integer analise;

    private Integer equipamento;

    private Integer anexo;

    private Short version;

}
