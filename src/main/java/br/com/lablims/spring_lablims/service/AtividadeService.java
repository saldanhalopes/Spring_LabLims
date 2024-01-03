package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Atividade;
import br.com.lablims.spring_lablims.domain.EquipamentoLog;
import br.com.lablims.spring_lablims.model.AtividadeDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.AtividadeRepository;
import br.com.lablims.spring_lablims.repos.EquipamentoLogRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AtividadeService {

    private final AtividadeRepository atividadeRepository;
    private final EquipamentoLogRepository equipamentoLogRepository;

    public Atividade findById(Integer id){
        return atividadeRepository.findById(id).orElse(null);
    }

    public SimplePage<AtividadeDTO> findAll(final String filter,
                                            final Pageable pageable) {
        Page<Atividade> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = atividadeRepository.findAllById(integerFilter, pageable);
        } else {
            page = atividadeRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(equipamentoAtividade -> mapToDTO(equipamentoAtividade, new AtividadeDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public AtividadeDTO get(final Integer id) {
        return atividadeRepository.findById(id)
                .map(equipamentoAtividade -> mapToDTO(equipamentoAtividade, new AtividadeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final AtividadeDTO atividadeDTO) {
        final Atividade atividade = new Atividade();
        mapToEntity(atividadeDTO, atividade);
        return atividadeRepository.save(atividade).getId();
    }

    public void update(final Integer id, final AtividadeDTO atividadeDTO) {
        final Atividade atividade = atividadeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(atividadeDTO, atividade);
        atividadeRepository.save(atividade);
    }

    public void delete(final Integer id) {
        atividadeRepository.deleteById(id);
    }

    private AtividadeDTO mapToDTO(final Atividade atividade,
                                  final AtividadeDTO atividadeDTO) {
        atividadeDTO.setId(atividade.getId());
        atividadeDTO.setAtividade(atividade.getAtividade());
        atividadeDTO.setProdutivo(atividade.isProdutivo());
        atividadeDTO.setSigla(atividade.getSigla());
        atividadeDTO.setDescricao(atividade.getDescricao());
        atividadeDTO.setCor(atividade.getCor());
        atividadeDTO.setVersion(atividade.getVersion());
        return atividadeDTO;
    }

    private Atividade mapToEntity(final AtividadeDTO atividadeDTO,
                                  final Atividade atividade) {
        atividade.setAtividade(atividadeDTO.getAtividade());
        atividade.setProdutivo(atividadeDTO.isProdutivo());
        atividade.setSigla(atividadeDTO.getSigla());
        atividade.setDescricao(atividadeDTO.getDescricao());
        atividade.setCor(atividadeDTO.getCor());
        return atividade;
    }

    public String getReferencedWarning(final Integer id) {
        final Atividade atividade = atividadeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final EquipamentoLog atividadeEquipamentoLog = equipamentoLogRepository.findFirstByAtividade(atividade);
        if (atividadeEquipamentoLog != null) {
            return WebUtils.getMessage("entity.referenced", atividadeEquipamentoLog.getId());
        }
        return null;
    }

}
