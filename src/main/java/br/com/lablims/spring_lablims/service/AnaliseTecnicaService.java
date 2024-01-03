package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.AnaliseTecnica;
import br.com.lablims.spring_lablims.domain.PlanoAnalise;
import br.com.lablims.spring_lablims.model.AnaliseTecnicaDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.AnaliseTecnicaRepository;
import br.com.lablims.spring_lablims.repos.PlanoAnaliseRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AnaliseTecnicaService {

    private final AnaliseTecnicaRepository analiseTecnicaRepository;
    private final PlanoAnaliseRepository planoAnaliseRepository;

    public AnaliseTecnica findById(Integer id){
        return analiseTecnicaRepository.findById(id).orElse(null);
    }

    public SimplePage<AnaliseTecnicaDTO> findAll(final String filter, final Pageable pageable) {
        Page<AnaliseTecnica> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = analiseTecnicaRepository.findAllById(integerFilter, pageable);
        } else {
            page = analiseTecnicaRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(analiseTecnica -> mapToDTO(analiseTecnica, new AnaliseTecnicaDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public AnaliseTecnicaDTO get(final Integer id) {
        return analiseTecnicaRepository.findById(id)
                .map(analiseTecnica -> mapToDTO(analiseTecnica, new AnaliseTecnicaDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final AnaliseTecnicaDTO analiseTecnicaDTO) {
        final AnaliseTecnica analiseTecnica = new AnaliseTecnica();
        mapToEntity(analiseTecnicaDTO, analiseTecnica);
        return analiseTecnicaRepository.save(analiseTecnica).getId();
    }

    public void update(final Integer id, final AnaliseTecnicaDTO analiseTecnicaDTO) {
        final AnaliseTecnica analiseTecnica = analiseTecnicaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(analiseTecnicaDTO, analiseTecnica);
        analiseTecnicaRepository.save(analiseTecnica);
    }

    public void delete(final Integer id) {
        analiseTecnicaRepository.deleteById(id);
    }

    private AnaliseTecnicaDTO mapToDTO(final AnaliseTecnica analiseTecnica,
            final AnaliseTecnicaDTO analiseTecnicaDTO) {
        analiseTecnicaDTO.setId(analiseTecnica.getId());
        analiseTecnicaDTO.setAnaliseTecnica(analiseTecnica.getAnaliseTecnica());
        analiseTecnicaDTO.setDescricaoAnaliseTecnica(analiseTecnica.getDescricaoAnaliseTecnica());
        analiseTecnicaDTO.setVersion(analiseTecnica.getVersion());
        return analiseTecnicaDTO;
    }

    private AnaliseTecnica mapToEntity(final AnaliseTecnicaDTO analiseTecnicaDTO,
            final AnaliseTecnica analiseTecnica) {
        analiseTecnica.setAnaliseTecnica(analiseTecnicaDTO.getAnaliseTecnica());
        analiseTecnica.setDescricaoAnaliseTecnica(analiseTecnicaDTO.getDescricaoAnaliseTecnica());
        return analiseTecnica;
    }

    public String getReferencedWarning(final Integer id) {
        final AnaliseTecnica analiseTecnica = analiseTecnicaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final PlanoAnalise analiseTecnicaPlanoAnalise = planoAnaliseRepository.findFirstByAnaliseTecnica(analiseTecnica);
        if (analiseTecnicaPlanoAnalise != null) {
            return WebUtils.getMessage("entity.referenced", analiseTecnicaPlanoAnalise.getId());
        }
        return null;
    }

}
