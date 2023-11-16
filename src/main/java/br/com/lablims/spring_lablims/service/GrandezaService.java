package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Equipamento;
import br.com.lablims.spring_lablims.domain.Grandeza;
import br.com.lablims.spring_lablims.domain.UnidadeMedida;
import br.com.lablims.spring_lablims.model.GrandezaDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.EquipamentoRepository;
import br.com.lablims.spring_lablims.repos.GrandezaRepository;
import br.com.lablims.spring_lablims.repos.UnidadeMedidaRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class GrandezaService {

    private final GrandezaRepository grandezaRepository;
    private final UnidadeMedidaRepository unidadeMedidaRepository;
    private final EquipamentoRepository equipamentoRepository;

    public Grandeza findById(Integer id){
        return grandezaRepository.findById(id).orElse(null);
    }

    public SimplePage<GrandezaDTO> findAll(final String filter, final Pageable pageable) {
        Page<Grandeza> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = grandezaRepository.findAllById(integerFilter, pageable);
        } else {
            page = grandezaRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(grandeza -> mapToDTO(grandeza, new GrandezaDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public GrandezaDTO get(final Integer id) {
        return grandezaRepository.findById(id)
                .map(grandeza -> mapToDTO(grandeza, new GrandezaDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final GrandezaDTO grandezaDTO) {
        final Grandeza grandeza = new Grandeza();
        mapToEntity(grandezaDTO, grandeza);
        return grandezaRepository.save(grandeza).getId();
    }

    public void update(final Integer id, final GrandezaDTO grandezaDTO) {
        final Grandeza grandeza = grandezaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(grandezaDTO, grandeza);
        grandezaRepository.save(grandeza);
    }

    public void delete(final Integer id) {
        grandezaRepository.deleteById(id);
    }

    private GrandezaDTO mapToDTO(final Grandeza grandeza,
            final GrandezaDTO grandezaDTO) {
        grandezaDTO.setId(grandeza.getId());
        grandezaDTO.setGrandeza(grandeza.getGrandeza());
        grandezaDTO.setVersion(grandeza.getVersion());
        return grandezaDTO;
    }

    private Grandeza mapToEntity(final GrandezaDTO grandezaDTO,
            final Grandeza grandeza) {
        grandeza.setGrandeza(grandezaDTO.getGrandeza());
        return grandeza;
    }

    public String getReferencedWarning(final Integer id) {
        final Grandeza grandeza = grandezaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final UnidadeMedida grandezaUnidadeMedida = unidadeMedidaRepository.findFirstByGrandeza(grandeza);
        if (grandezaUnidadeMedida != null) {
            return WebUtils.getMessage("grandeza.unidadeMedida.grandeza.referenced", grandezaUnidadeMedida.getId());
        }
        final Equipamento grandezaEquipamento = equipamentoRepository.findFirstByGrandeza(grandeza);
        if (grandezaEquipamento != null) {
            return WebUtils.getMessage("grandeza.equipamento.grandeza.referenced", grandezaEquipamento.getId());
        }
        return null;
    }

}
