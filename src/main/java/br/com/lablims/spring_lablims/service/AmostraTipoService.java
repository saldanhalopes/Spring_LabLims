package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.AmostraTipo;
import br.com.lablims.spring_lablims.domain.CustomRevisionEntity;
import br.com.lablims.spring_lablims.domain.Amostra;
import br.com.lablims.spring_lablims.model.AmostraTipoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.AmostraTipoRepository;
import br.com.lablims.spring_lablims.repos.AmostraRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@RequiredArgsConstructor
public class AmostraTipoService {

    private final AmostraTipoRepository amostraTipoRepository;
    private final AmostraRepository amostraRepository;

    public AmostraTipo findById(Integer id) {
        return amostraTipoRepository.findById(id).orElse(null);
    }

    public SimplePage<AmostraTipoDTO> findAll(final String filter, final Pageable pageable) {
        Page<AmostraTipo> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = amostraTipoRepository.findAllById(integerFilter, pageable);
        } else {
            page = amostraTipoRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(amostraTipo -> mapToDTO(amostraTipo, new AmostraTipoDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public AmostraTipoDTO get(final Integer id) {
        return amostraTipoRepository.findById(id)
                .map(amostraTipo -> mapToDTO(amostraTipo, new AmostraTipoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final AmostraTipoDTO amostraTipoDTO) {
        final AmostraTipo amostraTipo = new AmostraTipo();
        mapToEntity(amostraTipoDTO, amostraTipo);
        return amostraTipoRepository.save(amostraTipo).getId();
    }

    public void update(final Integer id, final AmostraTipoDTO amostraTipoDTO) {
        final AmostraTipo amostraTipo = amostraTipoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(amostraTipoDTO, amostraTipo);
        amostraTipoRepository.save(amostraTipo);
    }

    public void delete(final Integer id) {
        amostraTipoRepository.deleteById(id);
    }

    private AmostraTipoDTO mapToDTO(final AmostraTipo amostraTipo,
                                    final AmostraTipoDTO amostraTipoDTO) {
        amostraTipoDTO.setId(amostraTipo.getId());
        amostraTipoDTO.setTipo(amostraTipo.getTipo());
        amostraTipoDTO.setVersion(amostraTipo.getVersion());
        return amostraTipoDTO;
    }

    private AmostraTipo mapToEntity(final AmostraTipoDTO amostraTipoDTO,
                                    final AmostraTipo amostraTipo) {
        amostraTipo.setTipo(amostraTipoDTO.getTipo());
        return amostraTipo;
    }

    public String getReferencedWarning(final Integer id) {
        final AmostraTipo amostraTipo = amostraTipoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Amostra amostraTipoAmostra = amostraRepository.findFirstByAmostraTipo(amostraTipo);
        if (amostraTipoAmostra != null) {
            return WebUtils.getMessage("amostraTipo.amostra.amostraTipo.referenced", amostraTipoAmostra.getId());
        }
        return null;
    }

}
