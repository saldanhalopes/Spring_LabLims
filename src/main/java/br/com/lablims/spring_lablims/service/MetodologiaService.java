package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.CategoriaMetodologia;
import br.com.lablims.spring_lablims.domain.Metodologia;
import br.com.lablims.spring_lablims.domain.MetodologiaVesao;
import br.com.lablims.spring_lablims.model.MetodologiaDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.CategoriaMetodologiaRepository;
import br.com.lablims.spring_lablims.repos.MetodologiaRepository;
import br.com.lablims.spring_lablims.repos.MetodologiaVesaoRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class MetodologiaService {

    private final MetodologiaRepository metodologiaRepository;
    private final CategoriaMetodologiaRepository categoriaMetodologiaRepository;
    private final MetodologiaVesaoRepository metodologiaVesaoRepository;

    public Metodologia findById(Integer id){
        return metodologiaRepository.findById(id).orElse(null);
    }

    public MetodologiaService(final MetodologiaRepository metodologiaRepository,
            final CategoriaMetodologiaRepository categoriaMetodologiaRepository,
            final MetodologiaVesaoRepository metodologiaVesaoRepository) {
        this.metodologiaRepository = metodologiaRepository;
        this.categoriaMetodologiaRepository = categoriaMetodologiaRepository;
        this.metodologiaVesaoRepository = metodologiaVesaoRepository;
    }

    public SimplePage<MetodologiaDTO> findAll(final String filter, final Pageable pageable) {
        Page<Metodologia> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = metodologiaRepository.findAllById(integerFilter, pageable);
        } else {
            page = metodologiaRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(metodologia -> mapToDTO(metodologia, new MetodologiaDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public MetodologiaDTO get(final Integer id) {
        return metodologiaRepository.findById(id)
                .map(metodologia -> mapToDTO(metodologia, new MetodologiaDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final MetodologiaDTO metodologiaDTO) {
        final Metodologia metodologia = new Metodologia();
        mapToEntity(metodologiaDTO, metodologia);
        return metodologiaRepository.save(metodologia).getId();
    }

    public void update(final Integer id, final MetodologiaDTO metodologiaDTO) {
        final Metodologia metodologia = metodologiaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(metodologiaDTO, metodologia);
        metodologiaRepository.save(metodologia);
    }

    public void delete(final Integer id) {
        metodologiaRepository.deleteById(id);
    }

    private MetodologiaDTO mapToDTO(final Metodologia metodologia,
            final MetodologiaDTO metodologiaDTO) {
        metodologiaDTO.setId(metodologia.getId());
        metodologiaDTO.setCodigo(metodologia.getCodigo());
        metodologiaDTO.setMetodo(metodologia.getMetodo());
        metodologiaDTO.setObs(metodologia.getObs());
        metodologiaDTO.setCategoriaMetodologia(metodologia.getCategoriaMetodologia() == null ? null : metodologia.getCategoriaMetodologia().getId());
        metodologiaDTO.setVersion(metodologia.getVersion());
        return metodologiaDTO;
    }

    private Metodologia mapToEntity(final MetodologiaDTO metodologiaDTO,
            final Metodologia metodologia) {
        metodologia.setCodigo(metodologiaDTO.getCodigo());
        metodologia.setMetodo(metodologiaDTO.getMetodo());
        metodologia.setObs(metodologiaDTO.getObs());
        final CategoriaMetodologia categoriaMetodologia = metodologiaDTO.getCategoriaMetodologia() == null ? null : categoriaMetodologiaRepository.findById(metodologiaDTO.getCategoriaMetodologia())
                .orElseThrow(() -> new NotFoundException("categoriaMetodologia not found"));
        metodologia.setCategoriaMetodologia(categoriaMetodologia);
        return metodologia;
    }

    public String getReferencedWarning(final Integer id) {
        final Metodologia metodologia = metodologiaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final MetodologiaVesao metodologiaMetodologiaVesao = metodologiaVesaoRepository.findFirstByMetodologia(metodologia);
        if (metodologiaMetodologiaVesao != null) {
            return WebUtils.getMessage("metodologia.metodologiaVesao.metodologia.referenced", metodologiaMetodologiaVesao.getId());
        }
        return null;
    }

}
