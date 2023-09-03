package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Analise;
import br.com.lablims.spring_lablims.domain.ColunaLog;
import br.com.lablims.spring_lablims.domain.ColunaUtil;
import br.com.lablims.spring_lablims.domain.PlanoAnalise;
import br.com.lablims.spring_lablims.model.AnaliseDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.AnaliseRepository;
import br.com.lablims.spring_lablims.repos.ColunaLogRepository;
import br.com.lablims.spring_lablims.repos.ColunaUtilRepository;
import br.com.lablims.spring_lablims.repos.PlanoAnaliseRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class AnaliseService {

    private final AnaliseRepository analiseRepository;
    private final ColunaUtilRepository colunaUtilRepository;
    private final ColunaLogRepository colunaLogRepository;
    private final PlanoAnaliseRepository planoAnaliseRepository;

    public Analise findById(Integer id){
        return analiseRepository.findById(id).orElse(null);
    }

    public AnaliseService(final AnaliseRepository analiseRepository,
            final ColunaUtilRepository colunaUtilRepository,
            final ColunaLogRepository colunaLogRepository,
            final PlanoAnaliseRepository planoAnaliseRepository) {
        this.analiseRepository = analiseRepository;
        this.colunaUtilRepository = colunaUtilRepository;
        this.colunaLogRepository = colunaLogRepository;
        this.planoAnaliseRepository = planoAnaliseRepository;
    }

    public SimplePage<AnaliseDTO> findAll(final String filter, final Pageable pageable) {
        Page<Analise> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = analiseRepository.findAllById(integerFilter, pageable);
        } else {
            page = analiseRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(analise -> mapToDTO(analise, new AnaliseDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public AnaliseDTO get(final Integer id) {
        return analiseRepository.findById(id)
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
        analiseDTO.setVersion(analise.getVersion());
        return analiseDTO;
    }

    private Analise mapToEntity(final AnaliseDTO analiseDTO, final Analise analise) {
        analise.setAnalise(analiseDTO.getAnalise());
        analise.setDescricaoAnalise(analiseDTO.getDescricaoAnalise());
        analise.setSiglaAnalise(analiseDTO.getSiglaAnalise());
        return analise;
    }

    public String getReferencedWarning(final Integer id) {
        final Analise analise = analiseRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final ColunaUtil analiseColunaUtil = colunaUtilRepository.findFirstByAnalise(analise);
        if (analiseColunaUtil != null) {
            return WebUtils.getMessage("analise.colunaUtil.analise.referenced", analiseColunaUtil.getId());
        }
        final ColunaLog analiseColunaLog = colunaLogRepository.findFirstByAnalise(analise);
        if (analiseColunaLog != null) {
            return WebUtils.getMessage("analise.colunaLog.analise.referenced", analiseColunaLog.getId());
        }
        final PlanoAnalise analisePlanoAnalise = planoAnaliseRepository.findFirstByAnalise(analise);
        if (analisePlanoAnalise != null) {
            return WebUtils.getMessage("analise.planoAnalise.analise.referenced", analisePlanoAnalise.getId());
        }
        return null;
    }

}
