package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Atributo;
import br.com.lablims.spring_lablims.domain.Coluna;
import br.com.lablims.spring_lablims.domain.ColunaUtil;
import br.com.lablims.spring_lablims.domain.PlanoAnaliseColuna;
import br.com.lablims.spring_lablims.model.ColunaDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.AtributoRepository;
import br.com.lablims.spring_lablims.repos.ColunaRepository;
import br.com.lablims.spring_lablims.repos.ColunaUtilRepository;
import br.com.lablims.spring_lablims.repos.PlanoAnaliseColunaRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ColunaService {

    private final ColunaRepository colunaRepository;
    private final AtributoRepository atributoRepository;
    private final ColunaUtilRepository colunaUtilRepository;
    private final PlanoAnaliseColunaRepository planoAnaliseColunaRepository;

    public Coluna findById(Integer id){
        return colunaRepository.findById(id).orElse(null);
    }

    public SimplePage<ColunaDTO> findAll(final String filter, final Pageable pageable) {
        Page<Coluna> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = colunaRepository.findAllById(integerFilter, pageable);
        } else {
            page = colunaRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(coluna -> mapToDTO(coluna, new ColunaDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public ColunaDTO get(final Integer id) {
        return colunaRepository.findById(id)
                .map(coluna -> mapToDTO(coluna, new ColunaDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final ColunaDTO colunaDTO) {
        final Coluna coluna = new Coluna();
        mapToEntity(colunaDTO, coluna);
        return colunaRepository.save(coluna).getId();
    }

    public void update(final Integer id, final ColunaDTO colunaDTO) {
        final Coluna coluna = colunaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(colunaDTO, coluna);
        colunaRepository.save(coluna);
    }

    public void delete(final Integer id) {
        colunaRepository.deleteById(id);
    }

    private ColunaDTO mapToDTO(final Coluna coluna, final ColunaDTO colunaDTO) {
        colunaDTO.setId(coluna.getId());
        colunaDTO.setCodigo(coluna.getCodigo());
        colunaDTO.setPartNumber(coluna.getPartNumber());
        colunaDTO.setObs(coluna.getObs());
        colunaDTO.setTipoColuna(coluna.getTipoColuna() == null ? null : coluna.getTipoColuna().getId());
        colunaDTO.setFabricanteColuna(coluna.getFabricanteColuna() == null ? null : coluna.getFabricanteColuna().getId());
        colunaDTO.setMarcaColuna(coluna.getMarcaColuna() == null ? null : coluna.getMarcaColuna().getId());
        colunaDTO.setFaseColuna(coluna.getFaseColuna() == null ? null : coluna.getFaseColuna().getId());
        colunaDTO.setTamanhoColuna(coluna.getTamanhoColuna() == null ? null : coluna.getTamanhoColuna().getId());
        colunaDTO.setDiametroColuna(coluna.getDiametroColuna() == null ? null : coluna.getDiametroColuna().getId());
        colunaDTO.setParticulaColuna(coluna.getParticulaColuna() == null ? null : coluna.getParticulaColuna().getId());
        colunaDTO.setVersion(coluna.getVersion());
        return colunaDTO;
    }

    private Coluna mapToEntity(final ColunaDTO colunaDTO, final Coluna coluna) {
        coluna.setCodigo(colunaDTO.getCodigo());
        coluna.setPartNumber(colunaDTO.getPartNumber());
        coluna.setObs(colunaDTO.getObs());
        final Atributo tipoColuna = colunaDTO.getTipoColuna() == null ? null : atributoRepository.findById(colunaDTO.getTipoColuna())
                .orElseThrow(() -> new NotFoundException("tipoColuna not found"));
        coluna.setTipoColuna(tipoColuna);
        final Atributo fabricanteColuna = colunaDTO.getFabricanteColuna() == null ? null : atributoRepository.findById(colunaDTO.getFabricanteColuna())
                .orElseThrow(() -> new NotFoundException("fabricanteColuna not found"));
        coluna.setFabricanteColuna(fabricanteColuna);
        final Atributo marcaColuna = colunaDTO.getMarcaColuna() == null ? null : atributoRepository.findById(colunaDTO.getMarcaColuna())
                .orElseThrow(() -> new NotFoundException("marcaColuna not found"));
        coluna.setMarcaColuna(marcaColuna);
        final Atributo faseColuna = colunaDTO.getFaseColuna() == null ? null : atributoRepository.findById(colunaDTO.getFaseColuna())
                .orElseThrow(() -> new NotFoundException("faseColuna not found"));
        coluna.setFaseColuna(faseColuna);
        final Atributo tamanhoColuna = colunaDTO.getTamanhoColuna() == null ? null : atributoRepository.findById(colunaDTO.getTamanhoColuna())
                .orElseThrow(() -> new NotFoundException("tamanhoColuna not found"));
        coluna.setTamanhoColuna(tamanhoColuna);
        final Atributo diametroColuna = colunaDTO.getDiametroColuna() == null ? null : atributoRepository.findById(colunaDTO.getDiametroColuna())
                .orElseThrow(() -> new NotFoundException("diametroColuna not found"));
        coluna.setDiametroColuna(diametroColuna);
        final Atributo particulaColuna = colunaDTO.getParticulaColuna() == null ? null : atributoRepository.findById(colunaDTO.getParticulaColuna())
                .orElseThrow(() -> new NotFoundException("particulaColuna not found"));
        coluna.setParticulaColuna(particulaColuna);
        return coluna;
    }

    public String getReferencedWarning(final Integer id) {
        final Coluna coluna = colunaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final ColunaUtil colunaColunaUtil = colunaUtilRepository.findFirstByColuna(coluna);
        if (colunaColunaUtil != null) {
            return WebUtils.getMessage("entity.referenced", colunaColunaUtil.getId());
        }
        final PlanoAnaliseColuna colunaPlanoAnaliseColuna = planoAnaliseColunaRepository.findFirstByColuna(coluna);
        if (colunaPlanoAnaliseColuna != null) {
            return WebUtils.getMessage("entity.referenced", colunaPlanoAnaliseColuna.getId());
        }
        return null;
    }

}
