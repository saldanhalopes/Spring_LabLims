package br.com.lablims.spring_lablims.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Audited(withModifiedFlag = true)
public class Material {

	@Version
	private Short version;

	@Id
	@Column(nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true)
	private String codigo;

	@Column
	private String material;

	@Column(columnDefinition = "TEXT")
	private String descricao;

	@ManyToOne
	@JoinColumn(name="produto_tipo_id")
	private MaterialTipo materialTipo;

	@Column
	private String partNumber;

	@Column
	private String casNumber;

	@Column
	private String serialNumber;

	@Column
	private String numeroIdentificacao;

	@Column
	private String fabricante;

	@Column
	private String marca;

	@Column
	private String modelo;

	@ManyToOne
	@JoinColumn(name="fornecedor_id")
	private Fornecedor fornecedor;

	@Column
	private boolean compraUnica;

	@Column
	private BigDecimal estoqueMin;

	@Column
	private BigDecimal estoqueMax;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "unidade_id")
	private UnidadeMedida unidade;

	@Column
	private String grau;

	@Column
	private String pureza;

	@Column
	private String classe;

	@Column
	private String controlado;

	@Column
	private Integer saude;

	@Column
	private Integer inflamabilidade;

	@Column
	private Integer reatividade;

	@Column
	private String especifico;

	@Column
	private boolean ativo;

	@Column
	private String obs;


}
