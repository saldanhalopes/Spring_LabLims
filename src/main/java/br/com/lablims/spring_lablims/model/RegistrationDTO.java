package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class RegistrationDTO {

    @NotNull
    @Size(max = 255)
    @Email
    private String email;

    @NotNull
    @Size(max = 255)
    private String nome;

    @NotNull
    @Size(max = 255)
    private String password;

    @NotNull
    @Size(max = 255)
    private String sobrenome;

    @NotNull
    @Size(max = 255)
    private String username;

    private String secret;

    private Short version;

    private Integer grupo;

}
