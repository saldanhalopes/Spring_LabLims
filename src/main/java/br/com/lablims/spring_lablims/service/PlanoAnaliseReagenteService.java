package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.PlanoAnaliseReagente;
import br.com.lablims.spring_lablims.domain.Reagente;
import br.com.lablims.spring_lablims.model.PlanoAnaliseReagenteDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.PlanoAnaliseReagenteRepository;
import br.com.lablims.spring_lablims.repos.ReagenteRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class PlanoAnaliseReagenteService {

    private final PlanoAnaliseReagenteRepository planoAnaliseReagenteRepository;
    private final ReagenteRepository reagenteRepository;

    public PlanoAnaliseReagente findById(Integer id){
        return planoAnaliseReagenteRepository.findById(id).orElse(null);
    }

    public PlanoAnaliseReagenteService(
            final PlanoAnaliseReagenteRepository planoAnaliseReagenteRepository,
            final ReagenteRepository reagenteRepository) {
        this.planoAnaliseReagenteRepository = planoAnaliseReagenteRepository;
        this.reagenteRepository = reagenteRepository;
    }

    public SimplePage<PlanoAnaliseReagenteDTO> findAll(final String filter,
            final Pageable pageable) {
        Page<PlanoAnaliseReagente> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = planoAnaliseReagenteRepository.findAllById(integerFilter, pageable);
        } else {
            page = planoAnaliseReagenteRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(planoAnaliseReagente -> mapToDTO(planoAnaliseReagente, new PlanoAnaliseReagenteDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public PlanoAnaliseReagenteDTO get(final Integer id) {
        return planoAnaliseReagenteRepository.findById(id)
                .map(planoAnaliseReagente -> mapToDTO(planoAnaliseReagente, new PlanoAnaliseReagenteDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final PlanoAnaliseReagenteDTO planoAnaliseReagenteDTO) {
        final PlanoAnaliseReagente planoAnaliseReagente = new PlanoAnaliseReagente();
        mapToEntity(planoAnaliseReagenteDTO, planoAnaliseReagente);
        return planoAnaliseReagenteRepository.save(planoAnaliseReagente).getId();
    }

    public void update(final Integer id, final PlanoAnaliseReagenteDTO planoAnaliseReagenteDTO) {
        final PlanoAnaliseReagente planoAnaliseReagente = planoAnaliseReagenteRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(planoAnaliseReagenteDTO, planoAnaliseReagente);
        planoAnaliseReagenteRepository.save(planoAnaliseReagente);
    }

    public void delete(final Integer id) {
        planoAnaliseReagenteRepository.deleteById(id);
    }

    private PlanoAnaliseReagenteDTO mapToDTO(final PlanoAnaliseReagente planoAnaliseReagente,
            final PlanoAnaliseReagenteDTO planoAnaliseReagenteDTO) {
        planoAnaliseReagenteDTO.setId(planoAnaliseReagente.getId());
        planoAnaliseReagenteDTO.setQtdUtilizada(planoAnaliseReagente.getQtdUtilizada());
        planoAnaliseReagenteDTO.setObs(planoAnaliseReagente.getObs());
        planoAnaliseReagenteDTO.setReagente(planoAnaliseReagente.getReagente() == null ? null : planoAnaliseReagente.getReagente().getId());
        planoAnaliseReagenteDTO.setVersion(planoAnaliseReagente.getVersion());
        return planoAnaliseReagenteDTO;
    }

    private PlanoAnaliseReagente mapToEntity(final PlanoAnaliseReagenteDTO planoAnaliseReagenteDTO,
            final PlanoAnaliseReagente planoAnaliseReagente) {
        planoAnaliseReagente.setQtdUtilizada(planoAnaliseReagenteDTO.getQtdUtilizada());
        planoAnaliseReagente.setObs(planoAnaliseReagenteDTO.getObs());
        final Reagente reagente = planoAnaliseReagenteDTO.getReagente() == null ? null : reagenteRepository.findById(planoAnaliseReagenteDTO.getReagente())
                .orElseThrow(() -> new NotFoundException("reagente not found"));
        planoAnaliseReagente.setReagente(reagente);
        return planoAnaliseReagente;
    }

}
