package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.domain.MetodologiaVersao;
import br.com.lablims.spring_lablims.model.ArquivosDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Transactional
public class ArquivosService {

    private final ArquivosRepository arquivosRepository;
    private final ColunaUtilRepository colunaUtilRepository;
    private final MetodologiaVersaoRepository metodologiaVersaoRepository;
    private final EquipamentoRepository equipamentoRepository;
    private final ColunaLogRepository colunaLogRepository;
    private final ReagenteRepository reagenteRepository;
    private final EquipamentoLogRepository equipamentoLogRepository;
    private final LoteRepository loteRepository;
    private final AmostraRepository amostraRepository;

    public Arquivos findById(Integer id) {
        return arquivosRepository.findById(id).orElse(null);
    }

    public ArquivosService(final ArquivosRepository arquivosRepository,
                           final ColunaUtilRepository colunaUtilRepository,
                           final MetodologiaVersaoRepository metodologiaVersaoRepository,
                           final EquipamentoRepository equipamentoRepository,
                           final ColunaLogRepository colunaLogRepository,
                           final ReagenteRepository reagenteRepository,
                           final EquipamentoLogRepository equipamentoLogRepository,
                           final LoteRepository loteRepository,
                           final AmostraRepository amostraRepository) {
        this.arquivosRepository = arquivosRepository;
        this.colunaUtilRepository = colunaUtilRepository;
        this.metodologiaVersaoRepository = metodologiaVersaoRepository;
        this.equipamentoRepository = equipamentoRepository;
        this.colunaLogRepository = colunaLogRepository;
        this.reagenteRepository = reagenteRepository;
        this.equipamentoLogRepository = equipamentoLogRepository;
        this.loteRepository = loteRepository;
        this.amostraRepository = amostraRepository;
    }

    public SimplePage<ArquivosDTO> findAll(final String filter, final Pageable pageable) {
        Page<Arquivos> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = arquivosRepository.findAllById(integerFilter, pageable);
        } else {
            page = arquivosRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(arquivos -> mapToDTO(arquivos, new ArquivosDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public ArquivosDTO get(final Integer id) {
        return arquivosRepository.findById(id)
                .map(arquivos -> mapToDTO(arquivos, new ArquivosDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final ArquivosDTO arquivosDTO) {
        final Arquivos arquivos = new Arquivos();
        mapToEntity(arquivosDTO, arquivos);
        return arquivosRepository.save(arquivos).getId();
    }

    public void update(final Integer id, final ArquivosDTO arquivosDTO) {
        final Arquivos arquivos = arquivosRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(arquivosDTO, arquivos);
        arquivosRepository.save(arquivos);
    }

    public void delete(final Integer id) {
        final Arquivos arquivos = arquivosRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        arquivosRepository.delete(arquivos);
    }

    public void deleteEquipamento(final Integer id) {
        final Arquivos arquivos = arquivosRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        equipamentoRepository.findAllByArquivos(arquivos)
                .forEach(equipamento -> equipamento.getArquivos().remove(arquivos));
        arquivosRepository.delete(arquivos);
    }

    public void deleteLote(final Integer id) {
        final Arquivos arquivos = arquivosRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        loteRepository.findAllByArquivos(arquivos)
                .forEach(lote -> lote.getArquivos().remove(arquivos));
        arquivosRepository.delete(arquivos);
    }

    public void deleteEquipamentoLog(final Integer id) {
        final Arquivos arquivos = arquivosRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        equipamentoLogRepository.findAllByArquivos(arquivos)
                .forEach(equipamentoLog -> equipamentoLog.getArquivos().remove(arquivos));
        arquivosRepository.delete(arquivos);
    }

    public void deleteAmostra(final Integer id) {
        final Arquivos arquivos = arquivosRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        amostraRepository.findAllByArquivos(arquivos)
                .forEach(amostra -> amostra.getArquivos().remove(arquivos));
        arquivosRepository.delete(arquivos);
    }

    private ArquivosDTO mapToDTO(final Arquivos arquivos, final ArquivosDTO arquivosDTO) {
        arquivosDTO.setId(arquivos.getId());
        arquivosDTO.setNome(arquivos.getNome());
        arquivosDTO.setTipo(arquivos.getTipo());
        arquivosDTO.setDescricao(arquivos.getDescricao());
        arquivosDTO.setTamanho(arquivos.getTamanho());
        arquivosDTO.setArquivo(arquivos.getArquivo());
        arquivosDTO.setDataCriacao(arquivos.getDataCriacao());
        arquivosDTO.setVersion(arquivos.getVersion());
        return arquivosDTO;
    }

    private Arquivos mapToEntity(final ArquivosDTO arquivosDTO, final Arquivos arquivos) {
        arquivos.setNome(arquivosDTO.getNome());
        arquivos.setTipo(arquivosDTO.getTipo());
        arquivos.setDescricao(arquivosDTO.getDescricao());
        arquivos.setTamanho(arquivosDTO.getTamanho());
        arquivos.setArquivo(arquivosDTO.getArquivo());
        arquivos.setDataCriacao(arquivosDTO.getDataCriacao());
        return arquivos;
    }


}
