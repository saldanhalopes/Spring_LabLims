package br.com.lablims.spring_lablims.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.envers.Audited;


@Entity
@Data
@Audited(withModifiedFlag = true)
public class EquipamentoLog {

    @Version
    private Short version;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column
    private LocalDateTime dataInicio;

    @Column
    private LocalDateTime dataFim;

    @Column
    private String obs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atividade_id")
    private EquipamentoAtividade atividade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipamento_id")
    private Equipamento equipamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_inicio_id")
    private Usuario usuarioInicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_fim_id")
    private Usuario usuarioFim;

    @ManyToMany
    @Cascade(CascadeType.REMOVE)
    @JoinTable(
            name = "equipamentoLog_arquivo",
            joinColumns = @JoinColumn(name = "equipamentoLog_Id"),
            inverseJoinColumns = @JoinColumn(name = "arquivo_id")
    )
    private Set<Arquivos> arquivos;

    @ManyToMany
    @Cascade(CascadeType.REMOVE)
    @JoinTable(
            name = "equipamentoLog_amostras",
            joinColumns = @JoinColumn(name = "equipamentoLog_Id"),
            inverseJoinColumns = @JoinColumn(name = "amostra_Id")
    )
    private Set<Amostra> amostra;

}
