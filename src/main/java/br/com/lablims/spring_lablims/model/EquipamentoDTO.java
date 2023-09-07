package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


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

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime ultimaCalibracao;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime proximaCalibracao;

    private Boolean ativo;

    @Size(max = 255)
    private String obs;

    private byte[] imagem;

    @Size(max = 255)
    private String serialNumber;

    private Integer tipo;

    private Integer setor;

    private byte[] certificado;

    private byte[] manual;

    private byte[] procedimento;

    private Integer escala;

    private Short version;

}
