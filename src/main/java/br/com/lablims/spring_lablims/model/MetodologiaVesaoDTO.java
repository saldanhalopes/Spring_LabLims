package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@Setter
public class MetodologiaVesaoDTO {

    private Integer id;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataRevisao;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataProximaRevisao;

    @Size(max = 255)
    private String obs;

    private Integer metodologia;

    private Integer anexo;

    private List<Integer> material;

    private Integer status;

    private Short version;

}
