package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Estoque;
import br.com.lablims.spring_lablims.domain.Material;
import br.com.lablims.spring_lablims.domain.Setor;
import br.com.lablims.spring_lablims.domain.UnidadeMedida;
import br.com.lablims.spring_lablims.enums.MovimentacaoTipo;
import br.com.lablims.spring_lablims.model.EstoqueDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.EstoqueRepository;
import br.com.lablims.spring_lablims.repos.MaterialRepository;
import br.com.lablims.spring_lablims.repos.SetorRepository;
import br.com.lablims.spring_lablims.repos.UnidadeMedidaRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final SetorRepository setorRepository;
    private final MaterialRepository materialRepository;

    private final UnidadeMedidaRepository unidadeMedidaRepository;

    public Estoque findById(Integer id) {
        return estoqueRepository.findById(id).orElse(null);
    }


    public SimplePage<EstoqueDTO> findAll(final String filter, final Pageable pageable) {
        Page<Estoque> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = estoqueRepository.findAllById(integerFilter, pageable);
        } else {
            page = estoqueRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(Estoque -> mapToDTO(Estoque, new EstoqueDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }


    public SimplePage<EstoqueDTO> findAllOfEstoque(final Pageable pageable) {
        Page<Estoque> page;
        page = estoqueRepository.findAllOfEstoque(pageable);
        return new SimplePage<>(page.getContent()
                .stream()
                .map(estoque -> mapToDTO(estoque, new EstoqueDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }


    public EstoqueDTO get(final Integer id) {
        return estoqueRepository.findById(id)
                .map(estoque -> mapToDTO(estoque, new EstoqueDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final EstoqueDTO estoqueDTO) {
        final Estoque estoque = new Estoque();
        mapToEntity(estoqueDTO, estoque);
        return estoqueRepository.save(estoque).getId();
    }

    public void update(final Integer id, final EstoqueDTO estoqueDTO) {
        final Estoque estoque = estoqueRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(estoqueDTO, estoque);
        estoqueRepository.save(estoque);
    }

    public void delete(final Integer id) {
        estoqueRepository.deleteById(id);
    }

    private EstoqueDTO mapToDTO(final Estoque estoque,
                                   final EstoqueDTO estoqueDTO) {
        estoqueDTO.setId(estoque.getId());
        estoqueDTO.setMovimentacaoTipo(estoque.getMovimentacaoTipo().name());
        estoqueDTO.setQtdMaterial(estoque.getQtdMaterial());
        estoqueDTO.setValorUnitario(estoque.getValorUnitario());
        estoqueDTO.setSetor(estoque.getSetor() == null ? null : estoque.getSetor().getId());
        estoqueDTO.setSetorNome(estoque.getSetor().getSetor() == null ? null : estoque.getSetor().getSetor());
        estoqueDTO.setMaterial(estoque.getMaterial() == null ? null : estoque.getMaterial().getId());
        estoqueDTO.setMaterialNome(estoque.getMaterial().getMaterial() == null ? null : estoque.getMaterial().getMaterial());
        estoqueDTO.setUnidade(estoque.getUnidade() == null ? null : estoque.getUnidade().getId());
        estoqueDTO.setUnidadeNome(estoque.getUnidade().getUnidade() == null ? null : estoque.getUnidade().getUnidade());
        estoqueDTO.setVersion(estoque.getVersion());
        return estoqueDTO;
    }

    private Estoque mapToEntity(final EstoqueDTO estoqueDTO, final Estoque estoque) {
        estoque.setMovimentacaoTipo(MovimentacaoTipo.valueOf(estoqueDTO.getMovimentacaoTipo()));
        estoque.setQtdMaterial(estoqueDTO.getQtdMaterial());
        estoque.setValorUnitario(estoqueDTO.getValorUnitario());
        final Setor setor = estoqueDTO.getSetor() == null ? null : setorRepository.findById(estoqueDTO.getSetor())
                .orElseThrow(() -> new NotFoundException("Setor nao encontrado"));
        estoque.setSetor(setor);
        final Material material = estoqueDTO.getMaterial() == null ? null : materialRepository.findById(estoqueDTO.getMaterial())
                .orElseThrow(() -> new NotFoundException("Unidade Medida nao encontrada"));
        estoque.setMaterial(material);
        final UnidadeMedida unidade = estoqueDTO.getUnidade() == null ? null : unidadeMedidaRepository.findById(estoqueDTO.getUnidade())
                .orElseThrow(() -> new NotFoundException("UnidadeMedida not found"));
        estoque.setUnidade(unidade);
        return estoque;
    }

}
