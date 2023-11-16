package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.AnaliseTipo;
import br.com.lablims.spring_lablims.domain.PlanoAnalise;
import br.com.lablims.spring_lablims.model.AnaliseTipoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.AnaliseTipoRepository;
import br.com.lablims.spring_lablims.repos.PlanoAnaliseRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AnaliseTipoService {

    private final AnaliseTipoRepository analiseTipoRepository;
    private final PlanoAnaliseRepository planoAnaliseRepository;

    public AnaliseTipo findById(Integer id){
        return analiseTipoRepository.findById(id).orElse(null);
    }

    public SimplePage<AnaliseTipoDTO> findAll(final String filter, final Pageable pageable) {
        Page<AnaliseTipo> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = analiseTipoRepository.findAllById(integerFilter, pageable);
        } else {
            page = analiseTipoRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(analiseTipo -> mapToDTO(analiseTipo, new AnaliseTipoDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public AnaliseTipoDTO get(final Integer id) {
        return analiseTipoRepository.findById(id)
                .map(analiseTipo -> mapToDTO(analiseTipo, new AnaliseTipoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final AnaliseTipoDTO analiseTipoDTO) {
        final AnaliseTipo analiseTipo = new AnaliseTipo();
        mapToEntity(analiseTipoDTO, analiseTipo);
        return analiseTipoRepository.save(analiseTipo).getId();
    }

    public void update(final Integer id, final AnaliseTipoDTO analiseTipoDTO) {
        final AnaliseTipo analiseTipo = analiseTipoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(analiseTipoDTO, analiseTipo);
        analiseTipoRepository.save(analiseTipo);
    }

    public void delete(final Integer id) {
        analiseTipoRepository.deleteById(id);
    }

    private AnaliseTipoDTO mapToDTO(final AnaliseTipo analiseTipo,
            final AnaliseTipoDTO analiseTipoDTO) {
        analiseTipoDTO.setId(analiseTipo.getId());
        analiseTipoDTO.setAnaliseTipo(analiseTipo.getAnaliseTipo());
        analiseTipoDTO.setSiglaAnaliseTipo(analiseTipo.getSiglaAnaliseTipo());
        analiseTipoDTO.setDescricaoAnaliseTipo(analiseTipo.getDescricaoAnaliseTipo());
        analiseTipoDTO.setVersion(analiseTipo.getVersion());
        return analiseTipoDTO;
    }

    private AnaliseTipo mapToEntity(final AnaliseTipoDTO analiseTipoDTO,
            final AnaliseTipo analiseTipo) {
        analiseTipo.setAnaliseTipo(analiseTipoDTO.getAnaliseTipo());
        analiseTipo.setSiglaAnaliseTipo(analiseTipoDTO.getSiglaAnaliseTipo());
        analiseTipo.setDescricaoAnaliseTipo(analiseTipoDTO.getDescricaoAnaliseTipo());
        return analiseTipo;
    }

    public String getReferencedWarning(final Integer id) {
        final AnaliseTipo analiseTipo = analiseTipoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final PlanoAnalise analiseTipoPlanoAnalise = planoAnaliseRepository.findFirstByAnaliseTipo(analiseTipo);
        if (analiseTipoPlanoAnalise != null) {
            return WebUtils.getMessage("analiseTipo.planoAnalise.analiseTipo.referenced", analiseTipoPlanoAnalise.getId());
        }
        return null;
    }

}
