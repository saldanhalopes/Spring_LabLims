package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.AmostraStatus;
import br.com.lablims.spring_lablims.domain.AnaliseStatus;
import br.com.lablims.spring_lablims.domain.Atividade;
import br.com.lablims.spring_lablims.model.AnaliseStatusDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.AmostraStatusRepository;
import br.com.lablims.spring_lablims.repos.AnaliseStatusRepository;
import br.com.lablims.spring_lablims.repos.AtividadeRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnaliseStatusService {

    private final AnaliseStatusRepository analiseStatusRepository;

    private final AtividadeRepository atividadeRepository;
    private final AmostraStatusRepository amostraStatusRepository;

    public AnaliseStatus findById(Integer id){
        return analiseStatusRepository.findById(id).orElse(null);
    }

    public SimplePage<AnaliseStatusDTO> findAll(final String filter, final Pageable pageable) {
        Page<AnaliseStatus> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = analiseStatusRepository.findAllById(integerFilter, pageable);
        } else {
            page = analiseStatusRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(analiseStatus -> mapToDTO(analiseStatus, new AnaliseStatusDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public AnaliseStatusDTO get(final Integer id) {
        return analiseStatusRepository.findById(id)
                .map(analiseStatus -> mapToDTO(analiseStatus, new AnaliseStatusDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final AnaliseStatusDTO analiseStatusDTO) {
        final AnaliseStatus analiseStatus = new AnaliseStatus();
        mapToEntity(analiseStatusDTO, analiseStatus);
        return analiseStatusRepository.save(analiseStatus).getId();
    }

    public void update(final Integer id, final AnaliseStatusDTO analiseStatusDTO) {
        final AnaliseStatus analiseStatus = analiseStatusRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(analiseStatusDTO, analiseStatus);
        analiseStatusRepository.save(analiseStatus);
    }

    public void delete(final Integer id) {
        analiseStatusRepository.deleteById(id);
    }

    private AnaliseStatusDTO mapToDTO(final AnaliseStatus analiseStatus,
            final AnaliseStatusDTO analiseStatusDTO) {
        analiseStatusDTO.setId(analiseStatus.getId());
        analiseStatusDTO.setAnaliseStatus(analiseStatus.getAnaliseStatus());
        analiseStatusDTO.setSiglaAnaliseStatus(analiseStatus.getSiglaAnaliseStatus());
        analiseStatusDTO.setDescricaoAnaliseStatus(analiseStatus.getDescricaoAnaliseStatus());
        analiseStatusDTO.setAtividade(analiseStatus.getAtividade() == null ? null : analiseStatus.getAtividade().getId());
        analiseStatusDTO.setAtividadeNome(analiseStatus.getAtividade().getAtividade() == null ? null : analiseStatus.getAtividade().getAtividade());
        analiseStatusDTO.setAtividadeCor(analiseStatus.getAtividade().getCor() == null ? null : analiseStatus.getAtividade().getCor());
        analiseStatusDTO.setAtividadeProdutivo(analiseStatus.getAtividade().isProdutivo());
        analiseStatusDTO.setAtividadeSigla(analiseStatus.getAtividade().getSigla() == null ? null : analiseStatus.getAtividade().getSigla());
        return analiseStatusDTO;
    }

    private AnaliseStatus mapToEntity(final AnaliseStatusDTO analiseStatusDTO,
            final AnaliseStatus analiseStatus) {
        analiseStatus.setAnaliseStatus(analiseStatusDTO.getAnaliseStatus());
        analiseStatus.setSiglaAnaliseStatus(analiseStatusDTO.getSiglaAnaliseStatus());
        analiseStatus.setDescricaoAnaliseStatus(analiseStatusDTO.getDescricaoAnaliseStatus());
        final Atividade atividade = analiseStatusDTO.getAtividade() == null ? null :
                atividadeRepository.findById(analiseStatusDTO.getAtividade())
                .orElseThrow(() -> new NotFoundException("Atividade not found"));
        analiseStatus.setAtividade(atividade);
        return analiseStatus;
    }

    public String getReferencedWarning(final Integer id) {
        final AnaliseStatus analiseStatus = analiseStatusRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final AmostraStatus analiseStatusAmostraStatus = amostraStatusRepository.findFirstByAnaliseStatus(analiseStatus);
        if (analiseStatusAmostraStatus != null) {
            return WebUtils.getMessage("entity.referenced", analiseStatusAmostraStatus.getId());
        }
        return null;
    }

}
