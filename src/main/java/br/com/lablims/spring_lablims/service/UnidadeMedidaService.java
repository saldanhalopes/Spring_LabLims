package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Grandeza;
import br.com.lablims.spring_lablims.domain.PlanoAnaliseColuna;
import br.com.lablims.spring_lablims.domain.Reagente;
import br.com.lablims.spring_lablims.domain.SolucaoReagente;
import br.com.lablims.spring_lablims.domain.SolucaoRegistro;
import br.com.lablims.spring_lablims.domain.UnidadeMedida;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.model.UnidadeMedidaDTO;
import br.com.lablims.spring_lablims.repos.GrandezaRepository;
import br.com.lablims.spring_lablims.repos.PlanoAnaliseColunaRepository;
import br.com.lablims.spring_lablims.repos.ReagenteRepository;
import br.com.lablims.spring_lablims.repos.SolucaoReagenteRepository;
import br.com.lablims.spring_lablims.repos.SolucaoRegistroRepository;
import br.com.lablims.spring_lablims.repos.UnidadeMedidaRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UnidadeMedidaService {

    private final UnidadeMedidaRepository unidadeMedidaRepository;
    private final GrandezaRepository grandezaRepository;
    private final PlanoAnaliseColunaRepository planoAnaliseColunaRepository;
    private final ReagenteRepository reagenteRepository;
    private final SolucaoRegistroRepository solucaoRegistroRepository;
    private final SolucaoReagenteRepository solucaoReagenteRepository;

    public UnidadeMedida findById(Integer id){
        return unidadeMedidaRepository.findById(id).orElse(null);
    }

    public SimplePage<UnidadeMedidaDTO> findAll(final String filter, final Pageable pageable) {
        Page<UnidadeMedida> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = unidadeMedidaRepository.findAllById(integerFilter, pageable);
        } else {
            page = unidadeMedidaRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(unidadeMedida -> mapToDTO(unidadeMedida, new UnidadeMedidaDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public UnidadeMedidaDTO get(final Integer id) {
        return unidadeMedidaRepository.findById(id)
                .map(unidadeMedida -> mapToDTO(unidadeMedida, new UnidadeMedidaDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final UnidadeMedidaDTO unidadeMedidaDTO) {
        final UnidadeMedida unidadeMedida = new UnidadeMedida();
        mapToEntity(unidadeMedidaDTO, unidadeMedida);
        return unidadeMedidaRepository.save(unidadeMedida).getId();
    }

    public void update(final Integer id, final UnidadeMedidaDTO unidadeMedidaDTO) {
        final UnidadeMedida unidadeMedida = unidadeMedidaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(unidadeMedidaDTO, unidadeMedida);
        unidadeMedidaRepository.save(unidadeMedida);
    }

    public void delete(final Integer id) {
        unidadeMedidaRepository.deleteById(id);
    }

    private UnidadeMedidaDTO mapToDTO(final UnidadeMedida unidadeMedida,
            final UnidadeMedidaDTO unidadeMedidaDTO) {
        unidadeMedidaDTO.setId(unidadeMedida.getId());
        unidadeMedidaDTO.setUnidade(unidadeMedida.getUnidade());
        unidadeMedidaDTO.setGrandeza(unidadeMedida.getGrandeza() == null ? null : unidadeMedida.getGrandeza().getId());
        unidadeMedidaDTO.setVersion(unidadeMedida.getVersion());
        return unidadeMedidaDTO;
    }

    private UnidadeMedida mapToEntity(final UnidadeMedidaDTO unidadeMedidaDTO,
            final UnidadeMedida unidadeMedida) {
        unidadeMedida.setUnidade(unidadeMedidaDTO.getUnidade());
        final Grandeza grandeza = unidadeMedidaDTO.getGrandeza() == null ? null : grandezaRepository.findById(unidadeMedidaDTO.getGrandeza())
                .orElseThrow(() -> new NotFoundException("grandeza not found"));
        unidadeMedida.setGrandeza(grandeza);
        return unidadeMedida;
    }

    public String getReferencedWarning(final Integer id) {
        final UnidadeMedida unidadeMedida = unidadeMedidaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final PlanoAnaliseColuna unidadePlanoAnaliseColuna = planoAnaliseColunaRepository.findFirstByUnidade(unidadeMedida);
        if (unidadePlanoAnaliseColuna != null) {
            return WebUtils.getMessage("unidadeMedida.planoAnaliseColuna.unidade.referenced", unidadePlanoAnaliseColuna.getId());
        }
        final Reagente unidadeReagente = reagenteRepository.findFirstByUnidade(unidadeMedida);
        if (unidadeReagente != null) {
            return WebUtils.getMessage("unidadeMedida.reagente.unidade.referenced", unidadeReagente.getId());
        }
        final SolucaoRegistro unidadeSolucaoRegistro = solucaoRegistroRepository.findFirstByUnidade(unidadeMedida);
        if (unidadeSolucaoRegistro != null) {
            return WebUtils.getMessage("unidadeMedida.solucaoRegistro.unidade.referenced", unidadeSolucaoRegistro.getId());
        }
        final SolucaoReagente unidadeSolucaoReagente = solucaoReagenteRepository.findFirstByUnidade(unidadeMedida);
        if (unidadeSolucaoReagente != null) {
            return WebUtils.getMessage("unidadeMedida.solucaoReagente.unidade.referenced", unidadeSolucaoReagente.getId());
        }
        return null;
    }

}
