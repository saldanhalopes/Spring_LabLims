package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.MetodologiaStatus;
import br.com.lablims.spring_lablims.domain.MetodologiaVersao;
import br.com.lablims.spring_lablims.model.MetodologiaStatusDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.MetodologiaStatusRepository;
import br.com.lablims.spring_lablims.repos.MetodologiaVersaoRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MetodologiaStatusService {

    private final MetodologiaStatusRepository metodologiaStatusRepository;
    private final MetodologiaVersaoRepository metodologiaVersaoRepository;

    public MetodologiaStatus findById(Integer id){
        return metodologiaStatusRepository.findById(id).orElse(null);
    }

    public SimplePage<MetodologiaStatusDTO> findAll(final String filter, final Pageable pageable) {
        Page<MetodologiaStatus> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = metodologiaStatusRepository.findAllById(integerFilter, pageable);
        } else {
            page = metodologiaStatusRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(metodologiaStatus -> mapToDTO(metodologiaStatus, new MetodologiaStatusDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public MetodologiaStatusDTO get(final Integer id) {
        return metodologiaStatusRepository.findById(id)
                .map(metodologiaStatus -> mapToDTO(metodologiaStatus, new MetodologiaStatusDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final MetodologiaStatusDTO metodologiaStatusDTO) {
        final MetodologiaStatus metodologiaStatus = new MetodologiaStatus();
        mapToEntity(metodologiaStatusDTO, metodologiaStatus);
        return metodologiaStatusRepository.save(metodologiaStatus).getId();
    }

    public void update(final Integer id, final MetodologiaStatusDTO metodologiaStatusDTO) {
        final MetodologiaStatus metodologiaStatus = metodologiaStatusRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(metodologiaStatusDTO, metodologiaStatus);
        metodologiaStatusRepository.save(metodologiaStatus);
    }

    public void delete(final Integer id) {
        metodologiaStatusRepository.deleteById(id);
    }

    private MetodologiaStatusDTO mapToDTO(final MetodologiaStatus metodologiaStatus,
            final MetodologiaStatusDTO metodologiaStatusDTO) {
        metodologiaStatusDTO.setId(metodologiaStatus.getId());
        metodologiaStatusDTO.setStatus(metodologiaStatus.getStatus());
        metodologiaStatusDTO.setVersion(metodologiaStatus.getVersion());
        return metodologiaStatusDTO;
    }

    private MetodologiaStatus mapToEntity(final MetodologiaStatusDTO metodologiaStatusDTO,
            final MetodologiaStatus metodologiaStatus) {
        metodologiaStatus.setStatus(metodologiaStatusDTO.getStatus());
        return metodologiaStatus;
    }

    public String getReferencedWarning(final Integer id) {
        final MetodologiaStatus metodologiaStatus = metodologiaStatusRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final MetodologiaVersao statusMetodologiaVersao = metodologiaVersaoRepository.findFirstByStatus(metodologiaStatus);
        if (statusMetodologiaVersao != null) {
            return WebUtils.getMessage("entity.referenced", statusMetodologiaVersao.getId());
        }
        return null;
    }

}
