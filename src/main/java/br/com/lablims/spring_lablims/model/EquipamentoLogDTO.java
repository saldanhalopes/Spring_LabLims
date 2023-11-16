package br.com.lablims.spring_lablims.model;

import br.com.lablims.spring_lablims.domain.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@Setter
public class EquipamentoLogDTO {

    private Integer id;

    @Size(max = 2555)
    @NotNull
    @NotBlank
    private String descricao;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataInicio;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataFim;

    @Size(max = 2555)
    private String obs;

    @NotNull
    private Integer atividade;

    private String atividadeName;

    private Integer equipamento;

    private String equipamentoName;

    private Integer usuarioInicio;

    private String usuarioInicioName;

    private Integer usuarioFim;

    private String usuarioFimName;

    private List<Integer> amostra;

    private List<Integer> arquivos;

    private Short version;

    private Integer usuarioConfencia;

    private String usuarioConfenciaName;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dataConfencia;

}
