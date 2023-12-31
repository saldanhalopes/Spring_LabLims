package br.com.lablims.spring_lablims.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;


@Entity
@Getter
@Setter
@Audited(withModifiedFlag = true)
public class Analise {

    @Version
    private Short version;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String analise;

    @Column
    private String descricaoAnalise;

    @Column
    private String siglaAnalise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analise_tipo_id")
    private AnaliseTipo analiseTipo;

}
