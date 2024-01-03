package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Atributo;
import br.com.lablims.spring_lablims.domain.Categoria;
import br.com.lablims.spring_lablims.domain.Coluna;
import br.com.lablims.spring_lablims.domain.Turno;
import br.com.lablims.spring_lablims.model.AtributoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.AtributoRepository;
import br.com.lablims.spring_lablims.repos.CategoriaRepository;
import br.com.lablims.spring_lablims.repos.ColunaRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AtributoService {

    private final AtributoRepository atributoRepository;
    private final ColunaRepository colunaRepository;

    private final CategoriaRepository categoriaRepository;

    public Atributo findById(Integer id){
        return atributoRepository.findById(id).orElse(null);
    }

    public SimplePage<AtributoDTO> findAll(final String filter, final Pageable pageable) {
        Page<Atributo> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = atributoRepository.findAllById(integerFilter, pageable);
        } else {
            page = atributoRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(atributo -> mapToDTO(atributo, new AtributoDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public SimplePage<AtributoDTO> findAllOfAtributos(final Pageable pageable) {
        Page<Atributo> page = atributoRepository.findAllOfAtributos(pageable);
        return new SimplePage<>(page.getContent()
                .stream()
                .map(atributo -> mapToDTO(atributo, new AtributoDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public AtributoDTO get(final Integer id) {
        return atributoRepository.findById(id)
                .map(atributo -> mapToDTO(atributo, new AtributoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final AtributoDTO atributoDTO) {
        final Atributo atributo = new Atributo();
        mapToEntity(atributoDTO, atributo);
        return atributoRepository.save(atributo).getId();
    }

    public void update(final Integer id, final AtributoDTO atributoDTO) {
        final Atributo atributo = atributoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(atributoDTO, atributo);
        atributoRepository.save(atributo);
    }

    public void delete(final Integer id) {
        atributoRepository.deleteById(id);
    }

    private AtributoDTO mapToDTO(final Atributo atributo,
                                 final AtributoDTO atributoDTO) {
        atributoDTO.setId(atributo.getId());
        atributoDTO.setAtributo(atributo.getAtributo());
        atributoDTO.setValor(atributo.getValor());
        atributoDTO.setCategoria(atributo.getCategoria() == null ? null : atributo.getCategoria().getId());
        atributoDTO.setCategoriaNome(atributo.getCategoria().getCategoria() == null ? null : atributo.getCategoria().getCategoria());
        atributoDTO.setDescricao(atributo.getDescricao());
        atributoDTO.setVersion(atributo.getVersion());
        return atributoDTO;
    }

    private Atributo mapToEntity(final AtributoDTO atributoDTO,
                                 final Atributo atributo) {
        atributo.setAtributo(atributoDTO.getAtributo());
        atributo.setValor(atributoDTO.getValor());
        atributo.setDescricao(atributoDTO.getDescricao());
        final Categoria categoria = atributoDTO.getCategoria() == null ? null : categoriaRepository.findById(atributoDTO.getCategoria())
                .orElseThrow(() -> new NotFoundException("Categoria not found"));
        atributo.setCategoria(categoria);
        return atributo;
    }

    public String getReferencedWarning(final Integer id) {
        final Atributo atributo = atributoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Coluna tipoColunaColuna = colunaRepository.findFirstByTipoColuna(atributo);
        if (tipoColunaColuna != null) {
            return WebUtils.getMessage("entity.referenced", tipoColunaColuna.getId());
        }
        final Coluna fabricanteColunaColuna = colunaRepository.findFirstByFabricanteColuna(atributo);
        if (fabricanteColunaColuna != null) {
            return WebUtils.getMessage("entity.referenced", fabricanteColunaColuna.getId());
        }
        final Coluna marcaColunaColuna = colunaRepository.findFirstByMarcaColuna(atributo);
        if (marcaColunaColuna != null) {
            return WebUtils.getMessage("entity.referenced", marcaColunaColuna.getId());
        }
        final Coluna faseColunaColuna = colunaRepository.findFirstByFaseColuna(atributo);
        if (faseColunaColuna != null) {
            return WebUtils.getMessage("entity.referenced", faseColunaColuna.getId());
        }
        final Coluna tamanhoColunaColuna = colunaRepository.findFirstByTamanhoColuna(atributo);
        if (tamanhoColunaColuna != null) {
            return WebUtils.getMessage("entity.referenced", tamanhoColunaColuna.getId());
        }
        final Coluna diametroColunaColuna = colunaRepository.findFirstByDiametroColuna(atributo);
        if (diametroColunaColuna != null) {
            return WebUtils.getMessage("entity.referenced", diametroColunaColuna.getId());
        }
        final Coluna particulaColunaColuna = colunaRepository.findFirstByParticulaColuna(atributo);
        if (particulaColunaColuna != null) {
            return WebUtils.getMessage("entity.referenced", particulaColunaColuna.getId());
        }
        return null;
    }

}
