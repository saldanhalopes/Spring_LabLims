package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@Setter
public class UsuarioDTO {

    private Integer id;

    @Size(max = 255)
    private String cep;

    private Boolean changePass;

    @Size(max = 255)
    private String cidade;

    @Size(max = 255)
    private String cpf;

    @Size(max = 255)
    private String detalhes;

    @NotNull
    @Size(max = 255)
    private String email;

    @Size(max = 255)
    private String endereco;

    @Size(max = 255)
    private String estado;

    private Integer failedAccessCount;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastChangePass;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLogin;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLogout;

    @Size(max = 255)
    private String nome;

    @Size(max = 255)
    private String pais;

    @NotNull
    @Size(max = 255)
    private String password;

    @Size(max = 255)
    private String sobrenome;

    @Size(max = 255)
    private String telefone;

    @NotNull
    @Size(max = 255)
    private String username;

    private List<Integer> grupos;

    private Short version;

}
