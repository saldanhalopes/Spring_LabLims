package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


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

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ultimaCalibracao;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate proximaCalibracao;

    private Boolean ativo;

    @Size(max = 255)
    private String obs;

    private byte[] imagem;

    @Size(max = 255)
    private String serialNumber;

    private Integer tipo;

    private String tipoName;

    private Integer setor;

    private String setorName;

    private Integer grandeza;

    private String grandezaName;

    private Short version;

    private List<Integer> arquivos;

}
