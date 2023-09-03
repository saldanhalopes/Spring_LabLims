package br.com.lablims.spring_lablims.model;

import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@Setter
public class RegistrationRequest {

    @NotNull
    @Size(max = 255)
    private String email;

    @Size(max = 255)
    private String nome;

    @NotNull
    @Size(max = 255)
    private String password;

    @Size(max = 255)
    private String sobrenome;

    @NotNull
    @Size(max = 255)
    private String username;

}
