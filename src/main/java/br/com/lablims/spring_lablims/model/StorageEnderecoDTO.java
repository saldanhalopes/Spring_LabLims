package br.com.lablims.spring_lablims.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class StorageEnderecoDTO {

    private Integer id;

    @NotNull
    @NotBlank
    private String endereco;

    @Size(max = 255)
    private String obs;

    private Integer storage;

    private String storageNome;

    private Short version;

}
