package br.com.lablims.spring_lablims.model;

import br.com.lablims.spring_lablims.enums.SegurancaTipo;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


@Getter
@Setter
public class SegurancaDTO {

    private Integer id;

    private Short version;

    private SegurancaTipo segurancaTipo;

    private String value;

}
