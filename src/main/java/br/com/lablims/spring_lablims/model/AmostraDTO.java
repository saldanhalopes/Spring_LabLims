package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


@Getter
@Setter
public class AmostraDTO {

    private Integer id;

    @Size(max = 255)
    private String obs;

    private Short version;

    @Size(max = 255)
    @NotNull
    @NotBlank
    private String codigoAmostra;

    private Boolean usoInterno;

    private Double qtdAmostra;

    private Integer unidade;

    private String unidadeName;

    @Size(max = 255)
    private String fracionamento;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataEntrada;

    private Integer usuarioResponsavel;

    private String responsavel;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataLiberacaoCQ;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataEnvioDocumentacao;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataImpressao;

    @Size(max = 255)
    private String localArmazenamento;

    @Size(max = 255)
    private String condicoesArmazenamento;

    @Size(max = 255)
    private String pontoAmostragem;

    @Size(max = 255)
    private String complemento;

    @Size(max = 255)
    private String referenciaCliente;

    private byte[] anexo;

    @NotNull
    private Integer lote;

    private String loteNumero;

    @NotNull
    private Integer amostraTipo;

    private String amostraTipoNome;

    private String produtoName;

    private Integer produtoCodigo;

}












