package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.CategoriaMetodologia;
import br.com.lablims.spring_lablims.domain.Metodologia;
import br.com.lablims.spring_lablims.domain.MetodologiaVersao;
import br.com.lablims.spring_lablims.model.MetodologiaDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.CategoriaMetodologiaRepository;
import br.com.lablims.spring_lablims.repos.MetodologiaRepository;
import br.com.lablims.spring_lablims.repos.MetodologiaVersaoRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MetodologiaService {

    private final MetodologiaRepository metodologiaRepository;
    private final CategoriaMetodologiaRepository categoriaMetodologiaRepository;
    private final MetodologiaVersaoRepository metodologiaVersaoRepository;

    public Metodologia findById(Integer id){
        return metodologiaRepository.findById(id).orElse(null);
    }

    public SimplePage<MetodologiaDTO> findAll(final String filter, final Pageable pageable) {
        Page<Metodologia> page;
        if (filter != null) {
            page = metodologiaRepository.findAllByKeyword(filter, pageable);
        } else {
            page = metodologiaRepository.findAllOfMetodologia(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(metodologia -> mapToDTO(metodologia, new MetodologiaDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public MetodologiaDTO get(final Integer id) {
        return metodologiaRepository.findMetodologiaById(id)
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
        metodologiaDTO.setCategoriaMetodologiaNome(metodologia.getCategoriaMetodologia() == null ? null : metodologia.getCategoriaMetodologia().getCategoria());
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
        final MetodologiaVersao metodologiaMetodologiaVersao = metodologiaVersaoRepository.findFirstByMetodologia(metodologia);
        if (metodologiaMetodologiaVersao != null) {
            return WebUtils.getMessage("entity.referenced", metodologiaMetodologiaVersao.getId());
        }
        return null;
    }

}
