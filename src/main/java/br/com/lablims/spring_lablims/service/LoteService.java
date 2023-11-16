package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.LoteDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class LoteService {

    private final LoteRepository loteRepository;
    private final ProdutoRepository produtoRepository;
    private final UnidadeMedidaRepository unidadeMedidaRepository;
    private final ClienteRepository clienteRepository;
    private final AmostraRepository amostraRepository;
    private final ArquivosRepository arquivosRepository;

    public Lote findById(Integer id) {
        return loteRepository.findById(id).orElse(null);
    }

    public SimplePage<LoteDTO> findAll(final String filter, final Pageable pageable) {
        Page<Lote> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = loteRepository.findAllById(integerFilter, pageable);
        } else {
            page = loteRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(lote -> mapToDTO(lote, new LoteDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public SimplePage<LoteDTO> findAllOfLotes(final Pageable pageable) {
        Page<Lote> page;
        page = loteRepository.findAllOfLotes(pageable);
        return new SimplePage<>(page.getContent()
                .stream()
                .map(lote -> mapToDTOByLotes(lote, new LoteDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public LoteDTO get(final Integer id) {
        return loteRepository.findById(id)
                .map(lote -> mapToDTO(lote, new LoteDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final LoteDTO loteDTO) {
        final Lote lote = new Lote();
        mapToEntity(loteDTO, lote);
        return loteRepository.save(lote).getId();
    }

    public void update(final Integer id, final LoteDTO loteDTO) {
        final Lote lote = loteRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(loteDTO, lote);
        loteRepository.save(lote);
    }

    public void updateArquivo(final Lote lote) {
        loteRepository.save(lote);
    }

    public void delete(final Integer id) {
        final Lote lote = loteRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        loteRepository.findArquivosByLote(lote.getId()).getArquivos()
                .forEach(arquivos -> lote.getArquivos().remove(arquivos));
        loteRepository.delete(lote);
    }

    private LoteDTO mapToDTO(final Lote lote, final LoteDTO loteDTO) {
        loteDTO.setId(lote.getId());
        loteDTO.setLote(lote.getLote());
        loteDTO.setTamanhoLote(lote.getTamanhoLote());
        loteDTO.setDataFabricacao(lote.getDataFabricacao());
        loteDTO.setDataValidade(lote.getDataValidade());
        loteDTO.setLocalFabricacao(lote.getLocalFabricacao());
        loteDTO.setObs(lote.getObs());
        loteDTO.setUnidade(lote.getUnidade() == null ? null : lote.getUnidade().getId());
        loteDTO.setProduto(lote.getProduto() == null ? null : lote.getProduto().getId());
        loteDTO.setCliente(lote.getCliente() == null ? null : lote.getCliente().getId());
        loteDTO.setVersion(lote.getVersion());
        loteDTO.setArquivos(lote.getArquivos().stream()
                .map(arquivos -> arquivos.getId())
                .toList());
        return loteDTO;
    }

    public List<Arquivos> findArquivosByLote(final Integer id) {
        final Lote lote = loteRepository.findArquivosByLote(id);
        return lote.getArquivos()
                .stream()
                .toList();
    }

    public Lote findLoteWithArquivos(final Integer id) {
        return loteRepository.findArquivosByLote(id);
    }


    private LoteDTO mapToDTOByLotes(final Lote lote, final LoteDTO loteDTO) {
        loteDTO.setId(lote.getId());
        loteDTO.setLote(lote.getLote());
        loteDTO.setTamanhoLote(lote.getTamanhoLote());
        loteDTO.setDataFabricacao(lote.getDataFabricacao());
        loteDTO.setDataValidade(lote.getDataValidade());
        loteDTO.setLocalFabricacao(lote.getLocalFabricacao());
        loteDTO.setObs(lote.getObs());
        loteDTO.setUnidadeName(lote.getUnidade() == null ? null : lote.getUnidade().getUnidade());
        loteDTO.setProduto(lote.getProduto() == null ? null : lote.getProduto().getCodigo());
        loteDTO.setProdutoName(lote.getProduto() == null ? null : lote.getProduto().getProduto());
        loteDTO.setClienteName(lote.getCliente() == null ? null : lote.getCliente().getCliente());
        loteDTO.setVersion(lote.getVersion());
        return loteDTO;
    }

    private Lote mapToEntity(final LoteDTO loteDTO, final Lote lote) {
        lote.setLote(loteDTO.getLote());
        lote.setTamanhoLote(loteDTO.getTamanhoLote());
        lote.setDataFabricacao(loteDTO.getDataFabricacao());
        lote.setDataValidade(loteDTO.getDataValidade());
        lote.setLocalFabricacao(loteDTO.getLocalFabricacao());
        lote.setObs(loteDTO.getObs());
        final UnidadeMedida unidadeMedida = loteDTO.getUnidade() == null ? null : unidadeMedidaRepository.findById(loteDTO.getUnidade())
                .orElseThrow(() -> new NotFoundException("Amostra Tipo nao encontrada"));
        lote.setUnidade(unidadeMedida);
        final Produto produto = loteDTO.getProduto() == null ? null : produtoRepository.findById(loteDTO.getProduto())
                .orElseThrow(() -> new NotFoundException("Produto nao encontrado"));
        lote.setProduto(produto);
        final Cliente cliente = loteDTO.getCliente() == null ? null : clienteRepository.findById(loteDTO.getCliente())
                .orElseThrow(() -> new NotFoundException("Cliente nao encontrado"));
        lote.setCliente(cliente);
        final List<Arquivos> arquivos = arquivosRepository.findAllById(
                loteDTO.getArquivos() == null ? Collections.emptyList() : loteDTO.getArquivos());
        if (arquivos.size() != (loteDTO.getArquivos() == null ? 0 : loteDTO.getArquivos().size())) {
            throw new NotFoundException("Arquivos nao encontrados");
        }
        lote.setArquivos(arquivos.stream().collect(Collectors.toSet()));
        return lote;
    }

    public boolean loteExists(final String lote) {
        return loteRepository.existsByLoteIgnoreCase(lote);
    }

    public String getReferencedWarning(final Integer id) {
        final Lote lote = loteRepository.findById(id).orElseThrow(NotFoundException::new);
        final Amostra amostraLote = amostraRepository.findFirstByLote(lote);
        if (amostraLote != null) {
            return WebUtils.getMessage("lote.amostra.lote.referenced", amostraLote.getId());
        }
        return null;
    }


}
