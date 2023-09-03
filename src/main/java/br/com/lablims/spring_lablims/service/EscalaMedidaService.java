package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Equipamento;
import br.com.lablims.spring_lablims.domain.EscalaMedida;
import br.com.lablims.spring_lablims.domain.UnidadeMedida;
import br.com.lablims.spring_lablims.model.EscalaMedidaDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.EquipamentoRepository;
import br.com.lablims.spring_lablims.repos.EscalaMedidaRepository;
import br.com.lablims.spring_lablims.repos.UnidadeMedidaRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class EscalaMedidaService {

    private final EscalaMedidaRepository escalaMedidaRepository;
    private final UnidadeMedidaRepository unidadeMedidaRepository;
    private final EquipamentoRepository equipamentoRepository;

    public EscalaMedida findById(Integer id){
        return escalaMedidaRepository.findById(id).orElse(null);
    }

    public EscalaMedidaService(final EscalaMedidaRepository escalaMedidaRepository,
            final UnidadeMedidaRepository unidadeMedidaRepository,
            final EquipamentoRepository equipamentoRepository) {
        this.escalaMedidaRepository = escalaMedidaRepository;
        this.unidadeMedidaRepository = unidadeMedidaRepository;
        this.equipamentoRepository = equipamentoRepository;
    }

    public SimplePage<EscalaMedidaDTO> findAll(final String filter, final Pageable pageable) {
        Page<EscalaMedida> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = escalaMedidaRepository.findAllById(integerFilter, pageable);
        } else {
            page = escalaMedidaRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(escalaMedida -> mapToDTO(escalaMedida, new EscalaMedidaDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public EscalaMedidaDTO get(final Integer id) {
        return escalaMedidaRepository.findById(id)
                .map(escalaMedida -> mapToDTO(escalaMedida, new EscalaMedidaDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final EscalaMedidaDTO escalaMedidaDTO) {
        final EscalaMedida escalaMedida = new EscalaMedida();
        mapToEntity(escalaMedidaDTO, escalaMedida);
        return escalaMedidaRepository.save(escalaMedida).getId();
    }

    public void update(final Integer id, final EscalaMedidaDTO escalaMedidaDTO) {
        final EscalaMedida escalaMedida = escalaMedidaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(escalaMedidaDTO, escalaMedida);
        escalaMedidaRepository.save(escalaMedida);
    }

    public void delete(final Integer id) {
        escalaMedidaRepository.deleteById(id);
    }

    private EscalaMedidaDTO mapToDTO(final EscalaMedida escalaMedida,
            final EscalaMedidaDTO escalaMedidaDTO) {
        escalaMedidaDTO.setId(escalaMedida.getId());
        escalaMedidaDTO.setEscala(escalaMedida.getEscala());
        escalaMedidaDTO.setVersion(escalaMedida.getVersion());
        return escalaMedidaDTO;
    }

    private EscalaMedida mapToEntity(final EscalaMedidaDTO escalaMedidaDTO,
            final EscalaMedida escalaMedida) {
        escalaMedida.setEscala(escalaMedidaDTO.getEscala());
        return escalaMedida;
    }

    public String getReferencedWarning(final Integer id) {
        final EscalaMedida escalaMedida = escalaMedidaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final UnidadeMedida escalaMedidaUnidadeMedida = unidadeMedidaRepository.findFirstByEscalaMedida(escalaMedida);
        if (escalaMedidaUnidadeMedida != null) {
            return WebUtils.getMessage("escalaMedida.unidadeMedida.escalaMedida.referenced", escalaMedidaUnidadeMedida.getId());
        }
        final Equipamento escalaEquipamento = equipamentoRepository.findFirstByEscala(escalaMedida);
        if (escalaEquipamento != null) {
            return WebUtils.getMessage("escalaMedida.equipamento.escala.referenced", escalaEquipamento.getId());
        }
        return null;
    }

}
