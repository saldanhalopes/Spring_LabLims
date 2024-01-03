package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.EstoqueSaldoDTO;
import br.com.lablims.spring_lablims.model.ProdutoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.LoteRepository;
import br.com.lablims.spring_lablims.repos.ProdutoRepository;
import br.com.lablims.spring_lablims.repos.ProdutoTipoRepository;
import br.com.lablims.spring_lablims.repos.MetodologiaVersaoRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ProdutoTipoRepository produtoTipoRepository;
    private final MetodologiaVersaoRepository metodologiaVersaoRepository;
    private final LoteRepository loteRepository;

    public Produto findById(Integer id){
        return produtoRepository.findById(id).orElse(null);
    }

    public SimplePage<ProdutoDTO> findAll(final String filter, final Pageable pageable) {
        Page<Produto> page;
        if (filter != null) {
            page = produtoRepository.findAllByKeyword(filter, pageable);
        } else {
            page = produtoRepository.findAllOfProduto(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(produto -> mapToDTO(produto, new ProdutoDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public ProdutoDTO get(final Integer id) {
        return produtoRepository.findProdutoById(id)
                .map(produto -> mapToDTO(produto, new ProdutoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final ProdutoDTO produtoDTO) {
        final Produto produto = new Produto();
        mapToEntity(produtoDTO, produto);
        return produtoRepository.save(produto).getId();
    }

    public void update(final Integer id, final ProdutoDTO produtoDTO) {
        final Produto produto = produtoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(produtoDTO, produto);
        produtoRepository.save(produto);
    }

    public void delete(final Integer id) {
        final Produto produto = produtoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        metodologiaVersaoRepository.findAllByProduto(produto)
                .forEach(metodologiaVersao -> metodologiaVersao.getProduto().remove(produto));
        produtoRepository.delete(produto);
    }

    private ProdutoDTO mapToDTO(final Produto produto, final ProdutoDTO produtoDTO) {
        produtoDTO.setId(produto.getId());
        produtoDTO.setControleEspecial(produto.getControleEspecial());
        produtoDTO.setFiscalizado(produto.getFiscalizado());
        produtoDTO.setCodigo(produto.getCodigo());
        produtoDTO.setProduto(produto.getProduto());
        produtoDTO.setProdutoTipo(produto.getProdutoTipo() == null ? null : produto.getProdutoTipo().getId());
        produtoDTO.setProdutoTipoNome(produto.getProdutoTipo() == null ? null : produto.getProdutoTipo().getSigla());
        produtoDTO.setVersion(produto.getVersion());
        return produtoDTO;
    }

    private Produto mapToEntity(final ProdutoDTO produtoDTO, final Produto produto) {
        produto.setControleEspecial(produtoDTO.getControleEspecial());
        produto.setFiscalizado(produtoDTO.getFiscalizado());
        produto.setCodigo(produtoDTO.getCodigo());
        produto.setProduto(produtoDTO.getProduto());
        final ProdutoTipo produtoTipo = produtoDTO.getProdutoTipo() == null ? null : produtoTipoRepository.findById(produtoDTO.getProdutoTipo())
                .orElseThrow(() -> new NotFoundException("tipoProduto not found"));
        produto.setProdutoTipo(produtoTipo);
        return produto;
    }

    public String getReferencedWarning(final Integer id) {
        final Produto produto = produtoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final MetodologiaVersao produtoMetodologiaVersao = metodologiaVersaoRepository.findFirstByProduto(produto);
        if (produtoMetodologiaVersao != null) {
            return WebUtils.getMessage("entity.referenced", produtoMetodologiaVersao.getId());
        }
        final Lote produtoLote = loteRepository.findFirstByProduto(produto);
        if (produtoLote != null) {
            return WebUtils.getMessage("entity.referenced", produtoLote.getId());
        }
        return null;
    }

}
