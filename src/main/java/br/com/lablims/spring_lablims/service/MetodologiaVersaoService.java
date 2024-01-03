package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.domain.MetodologiaVersao;
import br.com.lablims.spring_lablims.model.MetodologiaVersaoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.ArquivosRepository;
import br.com.lablims.spring_lablims.repos.ColunaUtilRepository;
import br.com.lablims.spring_lablims.repos.ProdutoRepository;
import br.com.lablims.spring_lablims.repos.MetodologiaRepository;
import br.com.lablims.spring_lablims.repos.MetodologiaStatusRepository;
import br.com.lablims.spring_lablims.repos.MetodologiaVersaoRepository;
import br.com.lablims.spring_lablims.repos.PlanoAnaliseRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MetodologiaVersaoService {

    private final MetodologiaVersaoRepository metodologiaVersaoRepository;
    private final MetodologiaRepository metodologiaRepository;
    private final ArquivosRepository arquivosRepository;
    private final ProdutoRepository produtoRepository;
    private final MetodologiaStatusRepository metodologiaStatusRepository;
    private final ColunaUtilRepository colunaUtilRepository;
    private final PlanoAnaliseRepository planoAnaliseRepository;

    public MetodologiaVersao findById(Integer id){
        return metodologiaVersaoRepository.findById(id).orElse(null);
    }

    public SimplePage<MetodologiaVersaoDTO> findAll(final String filter, final Pageable pageable) {
        Page<MetodologiaVersao> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = metodologiaVersaoRepository.findAllById(integerFilter, pageable);
        } else {
            page = metodologiaVersaoRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(metodologiaVersao -> mapToDTO(metodologiaVersao, new MetodologiaVersaoDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public MetodologiaVersaoDTO get(final Integer id) {
        return metodologiaVersaoRepository.findById(id)
                .map(metodologiaVersao -> mapToDTO(metodologiaVersao, new MetodologiaVersaoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final MetodologiaVersaoDTO metodologiaVersaoDTO) {
        final MetodologiaVersao metodologiaVersao = new MetodologiaVersao();
        mapToEntity(metodologiaVersaoDTO, metodologiaVersao);
        return metodologiaVersaoRepository.save(metodologiaVersao).getId();
    }

    public void update(final Integer id, final MetodologiaVersaoDTO metodologiaVersaoDTO) {
        final MetodologiaVersao metodologiaVersao = metodologiaVersaoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(metodologiaVersaoDTO, metodologiaVersao);
        metodologiaVersaoRepository.save(metodologiaVersao);
    }

    public void delete(final Integer id) {
        metodologiaVersaoRepository.deleteById(id);
    }

    private MetodologiaVersaoDTO mapToDTO(final MetodologiaVersao metodologiaVersao,
                                          final MetodologiaVersaoDTO metodologiaVersaoDTO) {
        metodologiaVersaoDTO.setId(metodologiaVersao.getId());
        metodologiaVersaoDTO.setDataRevisao(metodologiaVersao.getDataRevisao());
        metodologiaVersaoDTO.setDataProximaRevisao(metodologiaVersao.getDataProximaRevisao());
        metodologiaVersaoDTO.setObs(metodologiaVersao.getObs());
        metodologiaVersaoDTO.setMetodologia(metodologiaVersao.getMetodologia() == null ? null : metodologiaVersao.getMetodologia().getId());
        metodologiaVersaoDTO.setAnexo(metodologiaVersao.getAnexo() == null ? null : metodologiaVersao.getAnexo().getId());
        metodologiaVersaoDTO.setVersion(metodologiaVersao.getVersion());
        metodologiaVersaoDTO.setProduto(metodologiaVersao.getProduto().stream()
                .map(produto -> produto.getId())
                .toList());
        metodologiaVersaoDTO.setStatus(metodologiaVersao.getStatus() == null ? null : metodologiaVersao.getStatus().getId());
        return metodologiaVersaoDTO;
    }

    private MetodologiaVersao mapToEntity(final MetodologiaVersaoDTO metodologiaVersaoDTO,
                                          final MetodologiaVersao metodologiaVersao) {
        metodologiaVersao.setDataRevisao(metodologiaVersaoDTO.getDataRevisao());
        metodologiaVersao.setDataProximaRevisao(metodologiaVersaoDTO.getDataProximaRevisao());
        metodologiaVersao.setObs(metodologiaVersaoDTO.getObs());
        final Metodologia metodologia = metodologiaVersaoDTO.getMetodologia() == null ? null : metodologiaRepository.findById(metodologiaVersaoDTO.getMetodologia())
                .orElseThrow(() -> new NotFoundException("metodologia not found"));
        metodologiaVersao.setMetodologia(metodologia);
        final Arquivos anexo = metodologiaVersaoDTO.getAnexo() == null ? null : arquivosRepository.findById(metodologiaVersaoDTO.getAnexo())
                .orElseThrow(() -> new NotFoundException("anexo not found"));
        metodologiaVersao.setAnexo(anexo);
        final List<Produto> produto = produtoRepository.findAllById(
                metodologiaVersaoDTO.getProduto() == null ? Collections.emptyList() : metodologiaVersaoDTO.getProduto());
        if (produto.size() != (metodologiaVersaoDTO.getProduto() == null ? 0 : metodologiaVersaoDTO.getProduto().size())) {
            throw new NotFoundException("one of produto not found");
        }
        metodologiaVersao.setProduto(produto.stream().collect(Collectors.toSet()));
        final MetodologiaStatus status = metodologiaVersaoDTO.getStatus() == null ? null : metodologiaStatusRepository.findById(metodologiaVersaoDTO.getStatus())
                .orElseThrow(() -> new NotFoundException("status not found"));
        metodologiaVersao.setStatus(status);
        return metodologiaVersao;
    }

    public String getReferencedWarning(final Integer id) {
        final MetodologiaVersao metodologiaVersao = metodologiaVersaoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final ColunaUtil metodologiaVersaoColunaUtil = colunaUtilRepository.findFirstByMetodologiaVersao(metodologiaVersao);
        if (metodologiaVersaoColunaUtil != null) {
            return WebUtils.getMessage("entity.referenced", metodologiaVersaoColunaUtil.getId());
        }
        final PlanoAnalise metodologiaVersaoPlanoAnalise = planoAnaliseRepository.findFirstByMetodologiaVersao(metodologiaVersao);
        if (metodologiaVersaoPlanoAnalise != null) {
            return WebUtils.getMessage("entity.referenced", metodologiaVersaoPlanoAnalise.getId());
        }
        return null;
    }

}
