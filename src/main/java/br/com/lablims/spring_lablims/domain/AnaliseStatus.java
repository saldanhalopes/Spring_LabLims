package br.com.lablims.spring_lablims.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;


@Entity
@Getter
@Setter
@Audited(withModifiedFlag = true)
public class AnaliseStatus {

    @Version
    private Short version;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String analiseStatus;

    @Column
    private String siglaAnaliseStatus;

    @Column
    private String descricaoAnaliseStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analise_atividade_id")
    private Atividade atividade;

}
