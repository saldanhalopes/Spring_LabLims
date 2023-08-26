package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Coluna;
import br.com.lablims.spring_lablims.domain.PlanoAnalise;
import br.com.lablims.spring_lablims.domain.PlanoAnaliseColuna;
import br.com.lablims.spring_lablims.domain.UnidadeMedida;
import br.com.lablims.spring_lablims.model.PlanoAnaliseColunaDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.ColunaRepository;
import br.com.lablims.spring_lablims.repos.PlanoAnaliseColunaRepository;
import br.com.lablims.spring_lablims.repos.PlanoAnaliseRepository;
import br.com.lablims.spring_lablims.repos.UnidadeMedidaRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class PlanoAnaliseColunaService {

    private final PlanoAnaliseColunaRepository planoAnaliseColunaRepository;
    private final PlanoAnaliseRepository planoAnaliseRepository;
    private final ColunaRepository colunaRepository;
    private final UnidadeMedidaRepository unidadeMedidaRepository;

    public PlanoAnaliseColuna findById(Integer id){
        return planoAnaliseColunaRepository.findById(id).orElse(null);
    }

    public PlanoAnaliseColunaService(
            final PlanoAnaliseColunaRepository planoAnaliseColunaRepository,
            final PlanoAnaliseRepository planoAnaliseRepository,
            final ColunaRepository colunaRepository,
            final UnidadeMedidaRepository unidadeMedidaRepository) {
        this.planoAnaliseColunaRepository = planoAnaliseColunaRepository;
        this.planoAnaliseRepository = planoAnaliseRepository;
        this.colunaRepository = colunaRepository;
        this.unidadeMedidaRepository = unidadeMedidaRepository;
    }

    public SimplePage<PlanoAnaliseColunaDTO> findAll(final String filter, final Pageable pageable) {
        Page<PlanoAnaliseColuna> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = planoAnaliseColunaRepository.findAllById(integerFilter, pageable);
        } else {
            page = planoAnaliseColunaRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(planoAnaliseColuna -> mapToDTO(planoAnaliseColuna, new PlanoAnaliseColunaDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public PlanoAnaliseColunaDTO get(final Integer id) {
        return planoAnaliseColunaRepository.findById(id)
                .map(planoAnaliseColuna -> mapToDTO(planoAnaliseColuna, new PlanoAnaliseColunaDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final PlanoAnaliseColunaDTO planoAnaliseColunaDTO) {
        final PlanoAnaliseColuna planoAnaliseColuna = new PlanoAnaliseColuna();
        mapToEntity(planoAnaliseColunaDTO, planoAnaliseColuna);
        return planoAnaliseColunaRepository.save(planoAnaliseColuna).getId();
    }

    public void update(final Integer id, final PlanoAnaliseColunaDTO planoAnaliseColunaDTO) {
        final PlanoAnaliseColuna planoAnaliseColuna = planoAnaliseColunaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(planoAnaliseColunaDTO, planoAnaliseColuna);
        planoAnaliseColunaRepository.save(planoAnaliseColuna);
    }

    public void delete(final Integer id) {
        planoAnaliseColunaRepository.deleteById(id);
    }

    private PlanoAnaliseColunaDTO mapToDTO(final PlanoAnaliseColuna planoAnaliseColuna,
            final PlanoAnaliseColunaDTO planoAnaliseColunaDTO) {
        planoAnaliseColunaDTO.setId(planoAnaliseColuna.getId());
        planoAnaliseColunaDTO.setQtdUtilizada(planoAnaliseColuna.getQtdUtilizada());
        planoAnaliseColunaDTO.setObs(planoAnaliseColuna.getObs());
        planoAnaliseColunaDTO.setPlanoAnalise(planoAnaliseColuna.getPlanoAnalise() == null ? null : planoAnaliseColuna.getPlanoAnalise().getId());
        planoAnaliseColunaDTO.setColuna(planoAnaliseColuna.getColuna() == null ? null : planoAnaliseColuna.getColuna().getId());
        planoAnaliseColunaDTO.setUnidade(planoAnaliseColuna.getUnidade() == null ? null : planoAnaliseColuna.getUnidade().getId());
        planoAnaliseColunaDTO.setVersion(planoAnaliseColuna.getVersion());
        return planoAnaliseColunaDTO;
    }

    private PlanoAnaliseColuna mapToEntity(final PlanoAnaliseColunaDTO planoAnaliseColunaDTO,
            final PlanoAnaliseColuna planoAnaliseColuna) {
        planoAnaliseColuna.setQtdUtilizada(planoAnaliseColunaDTO.getQtdUtilizada());
        planoAnaliseColuna.setObs(planoAnaliseColunaDTO.getObs());
        final PlanoAnalise planoAnalise = planoAnaliseColunaDTO.getPlanoAnalise() == null ? null : planoAnaliseRepository.findById(planoAnaliseColunaDTO.getPlanoAnalise())
                .orElseThrow(() -> new NotFoundException("planoAnalise not found"));
        planoAnaliseColuna.setPlanoAnalise(planoAnalise);
        final Coluna coluna = planoAnaliseColunaDTO.getColuna() == null ? null : colunaRepository.findById(planoAnaliseColunaDTO.getColuna())
                .orElseThrow(() -> new NotFoundException("coluna not found"));
        planoAnaliseColuna.setColuna(coluna);
        final UnidadeMedida unidade = planoAnaliseColunaDTO.getUnidade() == null ? null : unidadeMedidaRepository.findById(planoAnaliseColunaDTO.getUnidade())
                .orElseThrow(() -> new NotFoundException("unidade not found"));
        planoAnaliseColuna.setUnidade(unidade);
        return planoAnaliseColuna;
    }

}
