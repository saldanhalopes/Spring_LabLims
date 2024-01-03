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
	@JoinColumn(name = "material_id")
	private Material material;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "setor_id")
	private Setor setor;

	@Column
	private BigDecimal qtdMaterial;

	@Column
	private Double valorUnitario;

	@Column
	private Double valorTotal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "storage_endereco_id")
	private StorageEndereco storageEndereco;

	@ManyToMany
	@JoinTable(
			name = "equipamento_arquivo",
			joinColumns = @JoinColumn(name = "equipamento_id"),
			inverseJoinColumns = @JoinColumn(name = "arquivo_id")
	)
	private Set<Arquivos> arquivos;

}
