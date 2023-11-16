package br.com.lablims.spring_lablims.domain;

import br.com.lablims.spring_lablims.enums.MovimentacaoTipo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Audited(withModifiedFlag = true)
public class Estoque {

	@Version
	private Short version;

	@Id
	@Column(nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column
	@Enumerated(EnumType.STRING)
	private MovimentacaoTipo movimentacaoTipo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "produto_id")
	private Material material;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "setor_id")
	private Setor setor;

	@Column
	private BigDecimal qtdMaterial;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unidade_id")
	private UnidadeMedida unidade;

	@Column
	private Double valorUnitario;

}
