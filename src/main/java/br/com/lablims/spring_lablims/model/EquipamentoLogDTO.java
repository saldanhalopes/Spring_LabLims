package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@Setter
public class EquipamentoLogDTO {

    private Integer id;

    @Size(max = 255)
    private String descricao;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataInicio;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataFim;

    @Size(max = 255)
    private String obs;

    private Integer atividade;

    private Integer equipamento;

    private Integer usuarioInicio;

    private Integer usuarioFim;

    private Integer anexo;

    private Short version;

}
