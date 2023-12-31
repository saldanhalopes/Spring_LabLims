package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.AnaliseDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AnaliseService {

    private final AnaliseRepository analiseRepository;
    private final ColunaUtilRepository colunaUtilRepository;
    private final ColunaLogRepository colunaLogRepository;
    private final PlanoAnaliseRepository planoAnaliseRepository;
    private final AnaliseTipoRepository analiseTipoRepository;

    public Analise findById(Integer id){
        return analiseRepository.findById(id).orElse(null);
    }

    public SimplePage<AnaliseDTO> findAll(final String filter, final Pageable pageable) {
        Page<Analise> page;
        if (filter != null) {
            page = analiseRepository.findAllByKeyword(filter, pageable);
        } else {
            page = analiseRepository.findAllOfAnalise(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(analise -> mapToDTO(analise, new AnaliseDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public AnaliseDTO get(final Integer id) {
        return analiseRepository.findAnaliseById(id)
                .map(analise -> mapToDTO(analise, new AnaliseDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final AnaliseDTO analiseDTO) {
        final Analise analise = new Analise();
        mapToEntity(analiseDTO, analise);
        return analiseRepository.save(analise).getId();
    }

    public void update(final Integer id, final AnaliseDTO analiseDTO) {
        final Analise analise = analiseRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(analiseDTO, analise);
        analiseRepository.save(analise);
    }

    public void delete(final Integer id) {
        analiseRepository.deleteById(id);
    }

    private AnaliseDTO mapToDTO(final Analise analise, final AnaliseDTO analiseDTO) {
        analiseDTO.setId(analise.getId());
        analiseDTO.setAnalise(analise.getAnalise());
        analiseDTO.setDescricaoAnalise(analise.getDescricaoAnalise());
        analiseDTO.setSiglaAnalise(analise.getSiglaAnalise());
        analiseDTO.setAnaliseTipo(analise.getAnaliseTipo() == null ? null : analise.getAnaliseTipo().getId());
        analiseDTO.setAnaliseTipoNome(analise.getAnaliseTipo() == null ? null : analise.getAnaliseTipo().getAnaliseTipo());
        analiseDTO.setVersion(analise.getVersion());
        return analiseDTO;
    }

    private Analise mapToEntity(final AnaliseDTO analiseDTO, final Analise analise) {
        analise.setAnalise(analiseDTO.getAnalise());
        analise.setDescricaoAnalise(analiseDTO.getDescricaoAnalise());
        analise.setSiglaAnalise(analiseDTO.getSiglaAnalise());
        final AnaliseTipo analiseTipo = analiseDTO.getAnaliseTipo() == null ? null : analiseTipoRepository.findById(analiseDTO.getAnaliseTipo())
                .orElseThrow(() -> new NotFoundException("analiseTipo not found"));
        analise.setAnaliseTipo(analiseTipo);
        return analise;
    }

    public String getReferencedWarning(final Integer id) {
        final Analise analise = analiseRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final ColunaUtil analiseColunaUtil = colunaUtilRepository.findFirstByAnalise(analise);
        if (analiseColunaUtil != null) {
            return WebUtils.getMessage("entity.referenced", analiseColunaUtil.getId());
        }
        final ColunaLog analiseColunaLog = colunaLogRepository.findFirstByAnalise(analise);
        if (analiseColunaLog != null) {
            return WebUtils.getMessage("entity.referenced", analiseColunaLog.getId());
        }
        final PlanoAnalise analisePlanoAnalise = planoAnaliseRepository.findFirstByAnalise(analise);
        if (analisePlanoAnalise != null) {
            return WebUtils.getMessage("entity.referenced", analisePlanoAnalise.getId());
        }
        return null;
    }

}
