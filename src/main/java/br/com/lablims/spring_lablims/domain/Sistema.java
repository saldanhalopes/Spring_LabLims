package br.com.lablims.spring_lablims.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Audited(withModifiedFlag = true)
public class Sistema {

    @Version
    private Short version;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String sistemaNome;

    @Column
    private String sistemaCriador;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private LocalDateTime dataUpdate;

    @Column
    private Double builderVersao;

    @Column(columnDefinition = "TEXT")
    private String detalhes;


}
