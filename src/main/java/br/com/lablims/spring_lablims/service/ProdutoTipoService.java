package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Produto;
import br.com.lablims.spring_lablims.domain.ProdutoTipo;
import br.com.lablims.spring_lablims.model.ProdutoTipoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.ProdutoRepository;
import br.com.lablims.spring_lablims.repos.ProdutoTipoRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ProdutoTipoService {

    private final ProdutoTipoRepository produtoTipoRepository;
    private final ProdutoRepository produtoRepository;

    public ProdutoTipo findById(Integer id){
        return produtoTipoRepository.findById(id).orElse(null);
    }

    public SimplePage<ProdutoTipoDTO> findAll(final String filter, final Pageable pageable) {
        Page<ProdutoTipo> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = produtoTipoRepository.findAllById(integerFilter, pageable);
        } else {
            page = produtoTipoRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(produtoTipo -> mapToDTO(produtoTipo, new ProdutoTipoDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public ProdutoTipoDTO get(final Integer id) {
        return produtoTipoRepository.findById(id)
                .map(produtoTipo -> mapToDTO(produtoTipo, new ProdutoTipoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final ProdutoTipoDTO produtoTipoDTO) {
        final ProdutoTipo produtoTipo = new ProdutoTipo();
        mapToEntity(produtoTipoDTO, produtoTipo);
        return produtoTipoRepository.save(produtoTipo).getId();
    }

    public void update(final Integer id, final ProdutoTipoDTO produtoTipoDTO) {
        final ProdutoTipo produtoTipo = produtoTipoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(produtoTipoDTO, produtoTipo);
        produtoTipoRepository.save(produtoTipo);
    }

    public void delete(final Integer id) {
        produtoTipoRepository.deleteById(id);
    }

    private ProdutoTipoDTO mapToDTO(final ProdutoTipo produtoTipo,
                                    final ProdutoTipoDTO produtoTipoDTO) {
        produtoTipoDTO.setId(produtoTipo.getId());
        produtoTipoDTO.setSigla(produtoTipo.getSigla());
        produtoTipoDTO.setTipo(produtoTipo.getTipo());
        produtoTipoDTO.setVersion(produtoTipo.getVersion());
        return produtoTipoDTO;
    }

    private ProdutoTipo mapToEntity(final ProdutoTipoDTO produtoTipoDTO,
                                    final ProdutoTipo produtoTipo) {
        produtoTipo.setSigla(produtoTipoDTO.getSigla());
        produtoTipo.setTipo(produtoTipoDTO.getTipo());
        return produtoTipo;
    }

    public String getReferencedWarning(final Integer id) {
        final ProdutoTipo produtoTipo = produtoTipoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Produto tipoProdutoProduto = produtoRepository.findFirstByTipoProduto(produtoTipo);
        if (tipoProdutoProduto != null) {
            return WebUtils.getMessage("produtoTipo.produto.tipoProduto.referenced", tipoProdutoProduto.getId());
        }
        return null;
    }

}
