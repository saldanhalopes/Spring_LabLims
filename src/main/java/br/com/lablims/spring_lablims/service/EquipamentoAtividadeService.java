package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.EquipamentoAtividade;
import br.com.lablims.spring_lablims.domain.EquipamentoLog;
import br.com.lablims.spring_lablims.model.EquipamentoAtividadeDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.EquipamentoAtividadeRepository;
import br.com.lablims.spring_lablims.repos.EquipamentoLogRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EquipamentoAtividadeService {

    private final EquipamentoAtividadeRepository equipamentoAtividadeRepository;
    private final EquipamentoLogRepository equipamentoLogRepository;

    public EquipamentoAtividade findById(Integer id){
        return equipamentoAtividadeRepository.findById(id).orElse(null);
    }

    public SimplePage<EquipamentoAtividadeDTO> findAll(final String filter,
            final Pageable pageable) {
        Page<EquipamentoAtividade> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = equipamentoAtividadeRepository.findAllById(integerFilter, pageable);
        } else {
            page = equipamentoAtividadeRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(equipamentoAtividade -> mapToDTO(equipamentoAtividade, new EquipamentoAtividadeDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public EquipamentoAtividadeDTO get(final Integer id) {
        return equipamentoAtividadeRepository.findById(id)
                .map(equipamentoAtividade -> mapToDTO(equipamentoAtividade, new EquipamentoAtividadeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final EquipamentoAtividadeDTO equipamentoAtividadeDTO) {
        final EquipamentoAtividade equipamentoAtividade = new EquipamentoAtividade();
        mapToEntity(equipamentoAtividadeDTO, equipamentoAtividade);
        return equipamentoAtividadeRepository.save(equipamentoAtividade).getId();
    }

    public void update(final Integer id, final EquipamentoAtividadeDTO equipamentoAtividadeDTO) {
        final EquipamentoAtividade equipamentoAtividade = equipamentoAtividadeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(equipamentoAtividadeDTO, equipamentoAtividade);
        equipamentoAtividadeRepository.save(equipamentoAtividade);
    }

    public void delete(final Integer id) {
        equipamentoAtividadeRepository.deleteById(id);
    }

    private EquipamentoAtividadeDTO mapToDTO(final EquipamentoAtividade equipamentoAtividade,
            final EquipamentoAtividadeDTO equipamentoAtividadeDTO) {
        equipamentoAtividadeDTO.setId(equipamentoAtividade.getId());
        equipamentoAtividadeDTO.setAtividade(equipamentoAtividade.getAtividade());
        equipamentoAtividadeDTO.setVersion(equipamentoAtividade.getVersion());
        return equipamentoAtividadeDTO;
    }

    private EquipamentoAtividade mapToEntity(final EquipamentoAtividadeDTO equipamentoAtividadeDTO,
            final EquipamentoAtividade equipamentoAtividade) {
        equipamentoAtividade.setAtividade(equipamentoAtividadeDTO.getAtividade());
        return equipamentoAtividade;
    }

    public String getReferencedWarning(final Integer id) {
        final EquipamentoAtividade equipamentoAtividade = equipamentoAtividadeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final EquipamentoLog atividadeEquipamentoLog = equipamentoLogRepository.findFirstByAtividade(equipamentoAtividade);
        if (atividadeEquipamentoLog != null) {
            return WebUtils.getMessage("equipamentoAtividade.equipamentoLog.atividade.referenced", atividadeEquipamentoLog.getId());
        }
        return null;
    }

}
