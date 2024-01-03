package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.enums.MovimentacaoTipo;
import br.com.lablims.spring_lablims.model.EstoqueDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.util.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.util.Precision;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final EstoqueSaldoRepository estoqueSaldoRepository;
    private final SetorRepository setorRepository;
    private final MaterialRepository materialRepository;
    private final StorageEnderecoRepository storageEnderecoRepository;

    public Estoque findById(Integer id) {
        return estoqueRepository.findById(id).orElse(null);
    }


    public SimplePage<EstoqueDTO> findAll(final String filter, final Pageable pageable) {
        Page<Estoque> page;
        if (filter != null) {
            page = estoqueRepository.findAllByKeyword(filter, pageable);
        } else {
            page = estoqueRepository.findAllOfEstoque(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(estoque -> mapToDTO(estoque, new EstoqueDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public SimplePage<EstoqueDTO> findAllByMaterial(final Integer id, final Pageable pageable) {
        Page<Estoque> page = estoqueRepository.findAllByMaterial(id, pageable);
        return new SimplePage<>(page.getContent()
                .stream()
                .map(estoque -> mapToDTO(estoque, new EstoqueDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public List<Arquivos> findArquivosByEstoque(final Integer id) {
        final Estoque estoque = estoqueRepository.findArquivosByEstoque(id);
        return estoque.getArquivos()
                .stream()
                .toList();
    }

    public Estoque findEstoqueWithArquivos(final Integer id) {
        return estoqueRepository.findArquivosByEstoque(id);
    }

    public EstoqueDTO get(final Integer id) {
        return estoqueRepository.findEstoqueById(id)
                .map(estoque -> mapToDTO(estoque, new EstoqueDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public void create(final EstoqueDTO estoqueDTO) {
        final Estoque estoque = new Estoque();
        mapToEntity(estoqueDTO, estoque);
        estoqueRepository.save(estoque).getId();
        final EstoqueSaldo estoqueSaldo = estoqueSaldoRepository
                .findWithMaterial(estoque.getMaterial().getId());
        if (estoqueSaldo == null) {
            EstoqueSaldo estqSaldo = new EstoqueSaldo();
            mapToEntityEstoqueSaldo(estoque, estqSaldo);
            estoqueSaldoRepository.save(estqSaldo);
        } else {
            if (estoque.getMovimentacaoTipo() == MovimentacaoTipo.ENTRADA) {
                estoqueSaldo.setQtdMaterial(estoqueSaldo.getQtdMaterial().add(estoque.getQtdMaterial()));
                estoqueSaldo.setValorTotal(estoqueSaldo.getValorTotal() + estoque.getValorTotal());
            } else {
                estoqueSaldo.setQtdMaterial(estoqueSaldo.getQtdMaterial().subtract(estoque.getQtdMaterial()));
                estoqueSaldo.setValorTotal(estoqueSaldo.getValorTotal() - estoque.getValorTotal());
            }
            estoqueSaldoRepository.save(estoqueSaldo);
        }
    }

    public void update(final Estoque estoque) {
        estoqueRepository.save(estoque);
    }

    public void delete(final Integer id) {
        final Estoque estoque = estoqueRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final EstoqueSaldo estoqueSaldo = estoqueSaldoRepository
                .findWithMaterial(estoque.getMaterial().getId());
        if (estoque.getMovimentacaoTipo() == MovimentacaoTipo.ENTRADA) {
            estoqueSaldo.setQtdMaterial(estoqueSaldo.getQtdMaterial().subtract(estoque.getQtdMaterial()));
            estoqueSaldo.setValorTotal(estoqueSaldo.getValorTotal() - estoque.getValorTotal());
        } else {
            estoqueSaldo.setQtdMaterial(estoqueSaldo.getQtdMaterial().add(estoque.getQtdMaterial()));
            estoqueSaldo.setValorTotal(estoqueSaldo.getValorTotal() + estoque.getValorTotal());
        }
        estoqueSaldoRepository.save(estoqueSaldo);
        estoqueRepository.deleteById(id);
    }

    private EstoqueDTO mapToDTO(final Estoque estoque,
                                final EstoqueDTO estoqueDTO) {
        estoqueDTO.setId(estoque.getId());
        estoqueDTO.setMovimentacaoTipo(estoque.getMovimentacaoTipo().name());
        estoqueDTO.setQtdMaterial(estoque.getQtdMaterial());
        estoqueDTO.setValorUnitario(estoque.getValorUnitario());
        estoqueDTO.setValorTotal(estoque.getValorTotal());
        estoqueDTO.setSetor(estoque.getSetor() == null ? null : estoque.getSetor().getId());
        estoqueDTO.setSetorNome(estoque.getSetor().getSetor() == null ? null : estoque.getSetor().getSetor());
        estoqueDTO.setMaterial(estoque.getMaterial() == null ? null : estoque.getMaterial().getId());
        estoqueDTO.setStorageEndereco(estoque.getStorageEndereco() == null ? null : estoque.getStorageEndereco().getId());
        estoqueDTO.setMaterialNome(estoque.getMaterial().getMaterial() == null ? null : estoque.getMaterial().getMaterial());
        estoqueDTO.setMaterialUnidade(estoque.getMaterial() == null ? null : estoque.getMaterial().getUnidade().getUnidade());
        estoqueDTO.setMaterialCategoria(estoque.getMaterial() == null ? null : estoque.getMaterial().getCategoria().getCategoria());
        estoqueDTO.setMaterialFornecedor(estoque.getMaterial() == null ? null : estoque.getMaterial().getFornecedor().getFornecedor());
        estoqueDTO.setStorageEnderecoNome(estoque.getStorageEndereco() == null ? null : estoque.getStorageEndereco().getEndereco());
        estoqueDTO.setVersion(estoque.getVersion());
        return estoqueDTO;
    }


    private Estoque mapToEntity(final EstoqueDTO estoqueDTO, final Estoque estoque) {
        estoque.setMovimentacaoTipo(MovimentacaoTipo.valueOf(estoqueDTO.getMovimentacaoTipo()));
        estoque.setQtdMaterial(estoqueDTO.getQtdMaterial());
        estoque.setValorUnitario(estoqueDTO.getValorUnitario());
        estoque.setValorTotal(Precision.round(estoqueDTO.getQtdMaterial().multiply(new BigDecimal(estoqueDTO.getValorUnitario())).doubleValue(), 2));
        final Setor setor = estoqueDTO.getSetor() == null ? null : setorRepository.findById(estoqueDTO.getSetor())
                .orElseThrow(() -> new NotFoundException("Setor nao encontrado"));
        estoque.setSetor(setor);
        final Material material = estoqueDTO.getMaterial() == null ? null : materialRepository.findById(estoqueDTO.getMaterial())
                .orElseThrow(() -> new NotFoundException("Unidade Medida nao encontrada"));
        estoque.setMaterial(material);
        final StorageEndereco storageEndereco = estoqueDTO.getStorageEndereco() == null ? null : storageEnderecoRepository.findById(estoqueDTO.getStorageEndereco())
                .orElseThrow(() -> new NotFoundException("Endereco nao encontrado"));
        estoque.setStorageEndereco(storageEndereco);
        return estoque;
    }

    private EstoqueSaldo mapToEntityEstoqueSaldo(final Estoque estoque, final EstoqueSaldo estoqueSaldo) {
        estoqueSaldo.setQtdMaterial(estoque.getQtdMaterial());
        estoqueSaldo.setValorTotal(estoque.getValorTotal());
        final Setor setor = estoque.getSetor() == null ? null : setorRepository.findById(estoque.getSetor().getId())
                .orElseThrow(() -> new NotFoundException("Setor nao encontrado"));
        estoqueSaldo.setSetor(setor);
        final Material material = estoque.getMaterial() == null ? null : materialRepository.findById(estoque.getMaterial().getId())
                .orElseThrow(() -> new NotFoundException("Unidade Medida nao encontrada"));
        estoqueSaldo.setMaterial(material);
        return estoqueSaldo;
    }

}
