package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.EstoqueSaldo;
import br.com.lablims.spring_lablims.domain.Material;
import br.com.lablims.spring_lablims.domain.Setor;
import br.com.lablims.spring_lablims.domain.UnidadeMedida;
import br.com.lablims.spring_lablims.enums.MovimentacaoTipo;
import br.com.lablims.spring_lablims.model.EstoqueSaldoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.EstoqueSaldoRepository;
import br.com.lablims.spring_lablims.repos.MaterialRepository;
import br.com.lablims.spring_lablims.repos.SetorRepository;
import br.com.lablims.spring_lablims.repos.UnidadeMedidaRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.util.Precision;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class EstoqueSaldoService {

    private final EstoqueSaldoRepository estoqueSaldoRepository;
    private final SetorRepository setorRepository;
    private final MaterialRepository materialRepository;


    public EstoqueSaldo findById(Integer id) {
        return estoqueSaldoRepository.findById(id).orElse(null);
    }


    public SimplePage<EstoqueSaldoDTO> findAll(final String filter, final Pageable pageable) {
        Page<EstoqueSaldo> page;
        if (filter != null) {
            page = estoqueSaldoRepository.findAllByKeyword(filter, pageable);
        } else {
            page = estoqueSaldoRepository.findAllOfEstoqueSaldo(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(estoqueSaldo -> mapToDTO(estoqueSaldo, new EstoqueSaldoDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }


    public EstoqueSaldoDTO get(final Integer id) {
        return estoqueSaldoRepository.findEstoqueSaldoById(id)
                .map(estoqueSaldo -> mapToDTO(estoqueSaldo, new EstoqueSaldoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final EstoqueSaldoDTO estoqueSaldoDTO) {
        final EstoqueSaldo estoqueSaldo = new EstoqueSaldo();
        mapToEntity(estoqueSaldoDTO, estoqueSaldo);
        return estoqueSaldoRepository.save(estoqueSaldo).getId();
    }

    public void update(final Integer id, final EstoqueSaldoDTO estoqueSaldoDTO) {
        final EstoqueSaldo estoqueSaldo = estoqueSaldoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(estoqueSaldoDTO, estoqueSaldo);
        estoqueSaldoRepository.save(estoqueSaldo);
    }

    public void delete(final Integer id) {
        estoqueSaldoRepository.deleteById(id);
    }

    private EstoqueSaldoDTO mapToDTO(final EstoqueSaldo estoqueSaldo,
                                final EstoqueSaldoDTO estoqueSaldoDTO) {
        estoqueSaldoDTO.setId(estoqueSaldo.getId());
        estoqueSaldoDTO.setQtdMaterial(estoqueSaldo.getQtdMaterial());
        estoqueSaldoDTO.setValorTotal(estoqueSaldo.getValorTotal());
        estoqueSaldoDTO.setSetor(estoqueSaldo.getSetor() == null ? null : estoqueSaldo.getSetor().getId());
        estoqueSaldoDTO.setSetorNome(estoqueSaldo.getSetor().getSetor() == null ? null : estoqueSaldo.getSetor().getSetor());
        estoqueSaldoDTO.setMaterial(estoqueSaldo.getMaterial() == null ? null : estoqueSaldo.getMaterial().getId());
        estoqueSaldoDTO.setMaterialNome(estoqueSaldo.getMaterial().getMaterial() == null ? null : estoqueSaldo.getMaterial().getMaterial());
        estoqueSaldoDTO.setMaterialCategoria(estoqueSaldo.getMaterial() == null ? null : estoqueSaldo.getMaterial().getCategoria().getCategoria());
        estoqueSaldoDTO.setMaterialFornecedor(estoqueSaldo.getMaterial() == null ? null : estoqueSaldo.getMaterial().getFornecedor().getFornecedor());
        estoqueSaldoDTO.setMaterialUnidade(estoqueSaldo.getMaterial() == null ? null : estoqueSaldo.getMaterial().getUnidade().getUnidade());
        estoqueSaldoDTO.setVersion(estoqueSaldo.getVersion());
        return estoqueSaldoDTO;
    }

    private EstoqueSaldo mapToEntity(final EstoqueSaldoDTO estoqueSaldoDTO, final EstoqueSaldo estoqueSaldo) {
        estoqueSaldo.setQtdMaterial(estoqueSaldoDTO.getQtdMaterial());
        estoqueSaldo.setValorTotal(estoqueSaldoDTO.getValorTotal());
        final Setor setor = estoqueSaldoDTO.getSetor() == null ? null : setorRepository.findById(estoqueSaldoDTO.getSetor())
                .orElseThrow(() -> new NotFoundException("Setor nao encontrado"));
        estoqueSaldo.setSetor(setor);
        final Material material = estoqueSaldoDTO.getMaterial() == null ? null : materialRepository.findById(estoqueSaldoDTO.getMaterial())
                .orElseThrow(() -> new NotFoundException("Unidade Medida nao encontrada"));
        estoqueSaldo.setMaterial(material);
        return estoqueSaldo;
    }

}
