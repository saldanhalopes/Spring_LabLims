package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.SolucaoEquipamento;
import br.com.lablims.spring_lablims.domain.SolucaoRegistro;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.model.SolucaoEquipamentoDTO;
import br.com.lablims.spring_lablims.repos.SolucaoEquipamentoRepository;
import br.com.lablims.spring_lablims.repos.SolucaoRegistroRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class SolucaoEquipamentoService {

    private final SolucaoEquipamentoRepository solucaoEquipamentoRepository;
    private final SolucaoRegistroRepository solucaoRegistroRepository;

    public SolucaoEquipamento findById(Integer id){
        return solucaoEquipamentoRepository.findById(id).orElse(null);
    }

    public SolucaoEquipamentoService(
            final SolucaoEquipamentoRepository solucaoEquipamentoRepository,
            final SolucaoRegistroRepository solucaoRegistroRepository) {
        this.solucaoEquipamentoRepository = solucaoEquipamentoRepository;
        this.solucaoRegistroRepository = solucaoRegistroRepository;
    }

    public SimplePage<SolucaoEquipamentoDTO> findAll(final String filter, final Pageable pageable) {
        Page<SolucaoEquipamento> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = solucaoEquipamentoRepository.findAllById(integerFilter, pageable);
        } else {
            page = solucaoEquipamentoRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(solucaoEquipamento -> mapToDTO(solucaoEquipamento, new SolucaoEquipamentoDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public SolucaoEquipamentoDTO get(final Integer id) {
        return solucaoEquipamentoRepository.findById(id)
                .map(solucaoEquipamento -> mapToDTO(solucaoEquipamento, new SolucaoEquipamentoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final SolucaoEquipamentoDTO solucaoEquipamentoDTO) {
        final SolucaoEquipamento solucaoEquipamento = new SolucaoEquipamento();
        mapToEntity(solucaoEquipamentoDTO, solucaoEquipamento);
        return solucaoEquipamentoRepository.save(solucaoEquipamento).getId();
    }

    public void update(final Integer id, final SolucaoEquipamentoDTO solucaoEquipamentoDTO) {
        final SolucaoEquipamento solucaoEquipamento = solucaoEquipamentoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(solucaoEquipamentoDTO, solucaoEquipamento);
        solucaoEquipamentoRepository.save(solucaoEquipamento);
    }

    public void delete(final Integer id) {
        solucaoEquipamentoRepository.deleteById(id);
    }

    private SolucaoEquipamentoDTO mapToDTO(final SolucaoEquipamento solucaoEquipamento,
            final SolucaoEquipamentoDTO solucaoEquipamentoDTO) {
        solucaoEquipamentoDTO.setId(solucaoEquipamento.getId());
        solucaoEquipamentoDTO.setSolucaoRegistro(solucaoEquipamento.getSolucaoRegistro() == null ? null : solucaoEquipamento.getSolucaoRegistro().getId());
        solucaoEquipamentoDTO.setVersion(solucaoEquipamento.getVersion());
        return solucaoEquipamentoDTO;
    }

    private SolucaoEquipamento mapToEntity(final SolucaoEquipamentoDTO solucaoEquipamentoDTO,
            final SolucaoEquipamento solucaoEquipamento) {
        final SolucaoRegistro solucaoRegistro = solucaoEquipamentoDTO.getSolucaoRegistro() == null ? null : solucaoRegistroRepository.findById(solucaoEquipamentoDTO.getSolucaoRegistro())
                .orElseThrow(() -> new NotFoundException("solucaoRegistro not found"));
        solucaoEquipamento.setSolucaoRegistro(solucaoRegistro);
        return solucaoEquipamento;
    }

}
