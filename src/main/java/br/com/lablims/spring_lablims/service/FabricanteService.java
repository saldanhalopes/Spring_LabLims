package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Fabricante;
import br.com.lablims.spring_lablims.model.FabricanteDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.FabricanteRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class FabricanteService {

    private final FabricanteRepository fabricanteRepository;

    public Fabricante findById(Integer id) {
        return fabricanteRepository.findById(id).orElse(null);
    }


    public SimplePage<FabricanteDTO> findAll(final String filter, final Pageable pageable) {
        Page<Fabricante> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = fabricanteRepository.findAllById(integerFilter, pageable);
        } else {
            page = fabricanteRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(Fabricante -> mapToDTO(Fabricante, new FabricanteDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public FabricanteDTO get(final Integer id) {
        return fabricanteRepository.findById(id)
                .map(fabricante -> mapToDTO(fabricante, new FabricanteDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final FabricanteDTO fabricanteDTO) {
        final Fabricante fabricante = new Fabricante();
        mapToEntity(fabricanteDTO, fabricante);
        return fabricanteRepository.save(fabricante).getId();
    }

    public void update(final Integer id, final FabricanteDTO fabricanteDTO) {
        final Fabricante fabricante = fabricanteRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(fabricanteDTO, fabricante);
        fabricanteRepository.save(fabricante);
    }

    public void delete(final Integer id) {
        fabricanteRepository.deleteById(id);
    }

    private FabricanteDTO mapToDTO(final Fabricante fabricante,
                                   final FabricanteDTO fabricanteDTO) {
        fabricanteDTO.setId(fabricante.getId());
        fabricanteDTO.setFabricante(fabricante.getFabricante());
        fabricanteDTO.setDescricao(fabricante.getDescricao());
        fabricanteDTO.setObs(fabricante.getObs());
        fabricanteDTO.setVersion(fabricante.getVersion());
        return fabricanteDTO;
    }

    private Fabricante mapToEntity(final FabricanteDTO fabricanteDTO,
                                   final Fabricante fabricante) {
        fabricante.setFabricante(fabricanteDTO.getFabricante());
        fabricante.setDescricao(fabricanteDTO.getDescricao());
        fabricante.setObs(fabricanteDTO.getObs());
        return fabricante;
    }

}
