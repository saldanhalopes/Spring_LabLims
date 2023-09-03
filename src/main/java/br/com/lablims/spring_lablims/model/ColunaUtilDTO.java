package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@Setter
public class ColunaUtilDTO {

    private Integer id;

    @Size(max = 255)
    private String codigoColuna;

    @Size(max = 255)
    private String serialNumber;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataAtivacao;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataVerificacao;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataDescarte;

    private Boolean estoque;

    @Size(max = 255)
    private String obs;

    private Integer coluna;

    private Integer setor;

    private Integer metodologiaVersao;

    private Integer analise;

    private Integer colunaVaga;

    private Integer certificado;

    private List<Integer> anexos;

    private Short version;

}
