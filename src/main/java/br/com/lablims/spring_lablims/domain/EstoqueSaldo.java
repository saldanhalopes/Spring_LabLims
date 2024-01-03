package br.com.lablims.spring_lablims.domain;

import br.com.lablims.spring_lablims.enums.MovimentacaoTipo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Getter
@Setter
@Audited(withModifiedFlag = true)
public class EstoqueSaldo {

	@Version
	private Short version;

	@Id
	@Column(nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "material_id", unique = true)
	private Material material;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "setor_id")
	private Setor setor;

	@Column
	private BigDecimal qtdMaterial;

	@Column
	private Double valorTotal;

}
