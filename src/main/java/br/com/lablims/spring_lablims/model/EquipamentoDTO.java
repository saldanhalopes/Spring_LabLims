package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@Setter
public class EquipamentoDTO {

    private Integer id;

    @Size(max = 255)
    private String descricao;

    @Size(max = 255)
    private String tag;

    @Size(max = 255)
    private String fabricante;

    @Size(max = 255)
    private String marca;

    @Size(max = 255)
    private String modelo;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ultimaCalibracao;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime proximaCalibracao;

    private Boolean ativo;

    @Size(max = 255)
    private String obs;

    private byte[] imagem;

    @Size(max = 255)
    private String serialNumber;

    private Integer tipo;

    private Integer setor;

    private Integer certificado;

    private Integer manual;

    private Integer procedimento;

    private Integer escala;

    private Short version;

}
