package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.CategoriaMetodologia;
import br.com.lablims.spring_lablims.domain.Metodologia;
import br.com.lablims.spring_lablims.model.CategoriaMetodologiaDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.CategoriaMetodologiaRepository;
import br.com.lablims.spring_lablims.repos.MetodologiaRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CategoriaMetodologiaService {

    private final CategoriaMetodologiaRepository categoriaMetodologiaRepository;
    private final MetodologiaRepository metodologiaRepository;

    public CategoriaMetodologia findById(Integer id){
        return categoriaMetodologiaRepository.findById(id).orElse(null);
    }

    public SimplePage<CategoriaMetodologiaDTO> findAll(final String filter,
            final Pageable pageable) {
        Page<CategoriaMetodologia> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = categoriaMetodologiaRepository.findAllById(integerFilter, pageable);
        } else {
            page = categoriaMetodologiaRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(categoriaMetodologia -> mapToDTO(categoriaMetodologia, new CategoriaMetodologiaDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public CategoriaMetodologiaDTO get(final Integer id) {
        return categoriaMetodologiaRepository.findById(id)
                .map(categoriaMetodologia -> mapToDTO(categoriaMetodologia, new CategoriaMetodologiaDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final CategoriaMetodologiaDTO categoriaMetodologiaDTO) {
        final CategoriaMetodologia categoriaMetodologia = new CategoriaMetodologia();
        mapToEntity(categoriaMetodologiaDTO, categoriaMetodologia);
        return categoriaMetodologiaRepository.save(categoriaMetodologia).getId();
    }

    public void update(final Integer id, final CategoriaMetodologiaDTO categoriaMetodologiaDTO) {
        final CategoriaMetodologia categoriaMetodologia = categoriaMetodologiaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(categoriaMetodologiaDTO, categoriaMetodologia);
        categoriaMetodologiaRepository.save(categoriaMetodologia);
    }

    public void delete(final Integer id) {
        categoriaMetodologiaRepository.deleteById(id);
    }

    private CategoriaMetodologiaDTO mapToDTO(final CategoriaMetodologia categoriaMetodologia,
            final CategoriaMetodologiaDTO categoriaMetodologiaDTO) {
        categoriaMetodologiaDTO.setId(categoriaMetodologia.getId());
        categoriaMetodologiaDTO.setCategoria(categoriaMetodologia.getCategoria());
        categoriaMetodologiaDTO.setVersion(categoriaMetodologia.getVersion());
        return categoriaMetodologiaDTO;
    }

    private CategoriaMetodologia mapToEntity(final CategoriaMetodologiaDTO categoriaMetodologiaDTO,
            final CategoriaMetodologia categoriaMetodologia) {
        categoriaMetodologia.setCategoria(categoriaMetodologiaDTO.getCategoria());
        return categoriaMetodologia;
    }

    public String getReferencedWarning(final Integer id) {
        final CategoriaMetodologia categoriaMetodologia = categoriaMetodologiaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Metodologia categoriaMetodologiaMetodologia = metodologiaRepository.findFirstByCategoriaMetodologia(categoriaMetodologia);
        if (categoriaMetodologiaMetodologia != null) {
            return WebUtils.getMessage("entity.referenced", categoriaMetodologiaMetodologia.getId());
        }
        return null;
    }

}
