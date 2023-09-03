package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Coluna;
import br.com.lablims.spring_lablims.domain.ColunaConfig;
import br.com.lablims.spring_lablims.domain.ColunaUtil;
import br.com.lablims.spring_lablims.domain.PlanoAnaliseColuna;
import br.com.lablims.spring_lablims.model.ColunaDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.ColunaConfigRepository;
import br.com.lablims.spring_lablims.repos.ColunaRepository;
import br.com.lablims.spring_lablims.repos.ColunaUtilRepository;
import br.com.lablims.spring_lablims.repos.PlanoAnaliseColunaRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class ColunaService {

    private final ColunaRepository colunaRepository;
    private final ColunaConfigRepository colunaConfigRepository;
    private final ColunaUtilRepository colunaUtilRepository;
    private final PlanoAnaliseColunaRepository planoAnaliseColunaRepository;

    public Coluna findById(Integer id){
        return colunaRepository.findById(id).orElse(null);
    }

    public ColunaService(final ColunaRepository colunaRepository,
            final ColunaConfigRepository colunaConfigRepository,
            final ColunaUtilRepository colunaUtilRepository,
            final PlanoAnaliseColunaRepository planoAnaliseColunaRepository) {
        this.colunaRepository = colunaRepository;
        this.colunaConfigRepository = colunaConfigRepository;
        this.colunaUtilRepository = colunaUtilRepository;
        this.planoAnaliseColunaRepository = planoAnaliseColunaRepository;
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
        final ColunaConfig tipoColuna = colunaDTO.getTipoColuna() == null ? null : colunaConfigRepository.findById(colunaDTO.getTipoColuna())
                .orElseThrow(() -> new NotFoundException("tipoColuna not found"));
        coluna.setTipoColuna(tipoColuna);
        final ColunaConfig fabricanteColuna = colunaDTO.getFabricanteColuna() == null ? null : colunaConfigRepository.findById(colunaDTO.getFabricanteColuna())
                .orElseThrow(() -> new NotFoundException("fabricanteColuna not found"));
        coluna.setFabricanteColuna(fabricanteColuna);
        final ColunaConfig marcaColuna = colunaDTO.getMarcaColuna() == null ? null : colunaConfigRepository.findById(colunaDTO.getMarcaColuna())
                .orElseThrow(() -> new NotFoundException("marcaColuna not found"));
        coluna.setMarcaColuna(marcaColuna);
        final ColunaConfig faseColuna = colunaDTO.getFaseColuna() == null ? null : colunaConfigRepository.findById(colunaDTO.getFaseColuna())
                .orElseThrow(() -> new NotFoundException("faseColuna not found"));
        coluna.setFaseColuna(faseColuna);
        final ColunaConfig tamanhoColuna = colunaDTO.getTamanhoColuna() == null ? null : colunaConfigRepository.findById(colunaDTO.getTamanhoColuna())
                .orElseThrow(() -> new NotFoundException("tamanhoColuna not found"));
        coluna.setTamanhoColuna(tamanhoColuna);
        final ColunaConfig diametroColuna = colunaDTO.getDiametroColuna() == null ? null : colunaConfigRepository.findById(colunaDTO.getDiametroColuna())
                .orElseThrow(() -> new NotFoundException("diametroColuna not found"));
        coluna.setDiametroColuna(diametroColuna);
        final ColunaConfig particulaColuna = colunaDTO.getParticulaColuna() == null ? null : colunaConfigRepository.findById(colunaDTO.getParticulaColuna())
                .orElseThrow(() -> new NotFoundException("particulaColuna not found"));
        coluna.setParticulaColuna(particulaColuna);
        return coluna;
    }

    public String getReferencedWarning(final Integer id) {
        final Coluna coluna = colunaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final ColunaUtil colunaColunaUtil = colunaUtilRepository.findFirstByColuna(coluna);
        if (colunaColunaUtil != null) {
            return WebUtils.getMessage("coluna.colunaUtil.coluna.referenced", colunaColunaUtil.getId());
        }
        final PlanoAnaliseColuna colunaPlanoAnaliseColuna = planoAnaliseColunaRepository.findFirstByColuna(coluna);
        if (colunaPlanoAnaliseColuna != null) {
            return WebUtils.getMessage("coluna.planoAnaliseColuna.coluna.referenced", colunaPlanoAnaliseColuna.getId());
        }
        return null;
    }

}
