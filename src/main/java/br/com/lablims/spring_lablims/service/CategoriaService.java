package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Categoria;
import br.com.lablims.spring_lablims.domain.Material;
import br.com.lablims.spring_lablims.model.CategoriaDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.MaterialRepository;
import br.com.lablims.spring_lablims.repos.CategoriaRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final MaterialRepository materialRepository;

    public Categoria findById(Integer id){
        return categoriaRepository.findById(id).orElse(null);
    }

    public SimplePage<CategoriaDTO> findAll(final String filter, final Pageable pageable) {
        Page<Categoria> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = categoriaRepository.findAllById(integerFilter, pageable);
        } else {
            page = categoriaRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(categoria -> mapToDTO(categoria, new CategoriaDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public CategoriaDTO get(final Integer id) {
        return categoriaRepository.findById(id)
                .map(categoria -> mapToDTO(categoria, new CategoriaDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final CategoriaDTO categoriaDTO) {
        final Categoria categoria = new Categoria();
        mapToEntity(categoriaDTO, categoria);
        return categoriaRepository.save(categoria).getId();
    }

    public void update(final Integer id, final CategoriaDTO categoriaDTO) {
        final Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(categoriaDTO, categoria);
        categoriaRepository.save(categoria);
    }

    public void delete(final Integer id) {
        categoriaRepository.deleteById(id);
    }

    private CategoriaDTO mapToDTO(final Categoria categoria,
                                  final CategoriaDTO categoriaDTO) {
        categoriaDTO.setId(categoria.getId());
        categoriaDTO.setDescricao(categoria.getDescricao());
        categoriaDTO.setCategoria(categoria.getCategoria());
        categoriaDTO.setVersion(categoria.getVersion());
        return categoriaDTO;
    }

    private Categoria mapToEntity(final CategoriaDTO categoriaDTO,
                                  final Categoria categoria) {
        categoria.setDescricao(categoriaDTO.getDescricao());
        categoria.setCategoria(categoriaDTO.getCategoria());
        return categoria;
    }

    public String getReferencedWarning(final Integer id) {
        final Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Material categoriaMaterial = materialRepository.findFirstByCategoria(categoria);
        if (categoriaMaterial != null) {
            return WebUtils.getMessage("entity.referenced", categoriaMaterial.getId());
        }
        return null;
    }

}
