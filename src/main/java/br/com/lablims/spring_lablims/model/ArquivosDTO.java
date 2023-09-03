package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@Setter
public class ArquivosDTO {

    private Integer id;

    @Size(max = 255)
    private String nome;

    @Size(max = 255)
    private String tipo;

    @Size(max = 255)
    private String descricao;

    private Double tamanho;

    private byte[] arquivo;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataCriacao;

    private Short version;

}
