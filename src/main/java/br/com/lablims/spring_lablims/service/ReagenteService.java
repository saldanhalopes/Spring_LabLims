package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.ReagenteDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ReagenteService {

    private final ReagenteRepository reagenteRepository;
    private final UnidadeMedidaRepository unidadeMedidaRepository;
    private final ArquivosRepository arquivosRepository;
    private final PlanoAnaliseReagenteRepository planoAnaliseReagenteRepository;
    private final SolucaoReagenteRepository solucaoReagenteRepository;

    public Reagente findById(Integer id){
        return reagenteRepository.findById(id).orElse(null);
    }

    public SimplePage<ReagenteDTO> findAll(final String filter, final Pageable pageable) {
        Page<Reagente> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = reagenteRepository.findAllById(integerFilter, pageable);
        } else {
            page = reagenteRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(reagente -> mapToDTO(reagente, new ReagenteDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public ReagenteDTO get(final Integer id) {
        return reagenteRepository.findById(id)
                .map(reagente -> mapToDTO(reagente, new ReagenteDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final ReagenteDTO reagenteDTO) {
        final Reagente reagente = new Reagente();
        mapToEntity(reagenteDTO, reagente);
        return reagenteRepository.save(reagente).getId();
    }

    public void update(final Integer id, final ReagenteDTO reagenteDTO) {
        final Reagente reagente = reagenteRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(reagenteDTO, reagente);
        reagenteRepository.save(reagente);
    }

    public void delete(final Integer id) {
        reagenteRepository.deleteById(id);
    }

    private ReagenteDTO mapToDTO(final Reagente reagente, final ReagenteDTO reagenteDTO) {
        reagenteDTO.setId(reagente.getId());
        reagenteDTO.setCodigo(reagente.getCodigo());
        reagenteDTO.setReagente(reagente.getReagente());
        reagenteDTO.setCasNumber(reagente.getCasNumber());
        reagenteDTO.setQtdEstoqueMin(reagente.getQtdEstoqueMin());
        reagenteDTO.setQtdEstoqueMax(reagente.getQtdEstoqueMax());
        reagenteDTO.setCompraUnica(reagente.getCompraUnica());
        reagenteDTO.setEnderecamento(reagente.getEnderecamento());
        reagenteDTO.setNumeroIdentificacao(reagente.getNumeroIdentificacao());
        reagenteDTO.setArmazenamento(reagente.getArmazenamento());
        reagenteDTO.setGrau(reagente.getGrau());
        reagenteDTO.setPureza(reagente.getPureza());
        reagenteDTO.setClasse(reagente.getClasse());
        reagenteDTO.setControlado(reagente.getControlado());
        reagenteDTO.setSaude(reagente.getSaude());
        reagenteDTO.setInflamabilidade(reagente.getInflamabilidade());
        reagenteDTO.setReatividade(reagente.getReatividade());
        reagenteDTO.setEspecifico(reagente.getEspecifico());
        reagenteDTO.setObs(reagente.getObs());
        reagenteDTO.setUnidade(reagente.getUnidade() == null ? null : reagente.getUnidade().getId());
        reagenteDTO.setFds(reagente.getFds() == null ? null : reagente.getFds().getId());
        reagenteDTO.setVersion(reagente.getVersion());
        return reagenteDTO;
    }

    private Reagente mapToEntity(final ReagenteDTO reagenteDTO, final Reagente reagente) {
        reagente.setCodigo(reagenteDTO.getCodigo());
        reagente.setReagente(reagenteDTO.getReagente());
        reagente.setCasNumber(reagenteDTO.getCasNumber());
        reagente.setQtdEstoqueMin(reagenteDTO.getQtdEstoqueMin());
        reagente.setQtdEstoqueMax(reagenteDTO.getQtdEstoqueMax());
        reagente.setCompraUnica(reagenteDTO.getCompraUnica());
        reagente.setEnderecamento(reagenteDTO.getEnderecamento());
        reagente.setNumeroIdentificacao(reagenteDTO.getNumeroIdentificacao());
        reagente.setArmazenamento(reagenteDTO.getArmazenamento());
        reagente.setGrau(reagenteDTO.getGrau());
        reagente.setPureza(reagenteDTO.getPureza());
        reagente.setClasse(reagenteDTO.getClasse());
        reagente.setControlado(reagenteDTO.getControlado());
        reagente.setSaude(reagenteDTO.getSaude());
        reagente.setInflamabilidade(reagenteDTO.getInflamabilidade());
        reagente.setReatividade(reagenteDTO.getReatividade());
        reagente.setEspecifico(reagenteDTO.getEspecifico());
        reagente.setObs(reagenteDTO.getObs());
        final UnidadeMedida unidade = reagenteDTO.getUnidade() == null ? null : unidadeMedidaRepository.findById(reagenteDTO.getUnidade())
                .orElseThrow(() -> new NotFoundException("unidade not found"));
        reagente.setUnidade(unidade);
        final Arquivos fds = reagenteDTO.getFds() == null ? null : arquivosRepository.findById(reagenteDTO.getFds())
                .orElseThrow(() -> new NotFoundException("fds not found"));
        reagente.setFds(fds);
        return reagente;
    }

    public String getReferencedWarning(final Integer id) {
        final Reagente reagente = reagenteRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final PlanoAnaliseReagente reagentePlanoAnaliseReagente = planoAnaliseReagenteRepository.findFirstByReagente(reagente);
        if (reagentePlanoAnaliseReagente != null) {
            return WebUtils.getMessage("reagente.planoAnaliseReagente.reagente.referenced", reagentePlanoAnaliseReagente.getId());
        }
        final SolucaoReagente reagenteSolucaoReagente = solucaoReagenteRepository.findFirstByReagente(reagente);
        if (reagenteSolucaoReagente != null) {
            return WebUtils.getMessage("reagente.solucaoReagente.reagente.referenced", reagenteSolucaoReagente.getId());
        }
        return null;
    }

}
