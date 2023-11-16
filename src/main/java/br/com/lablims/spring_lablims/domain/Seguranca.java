package br.com.lablims.spring_lablims.domain;

import br.com.lablims.spring_lablims.enums.SegurancaTipo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;


@Entity
@Getter
@Setter
@Audited(withModifiedFlag = true)
public class Seguranca {

    @Version
    private Short version;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    @Enumerated(EnumType.STRING)
    private SegurancaTipo segurancaTipo;

    @Column
    private String value;

}
