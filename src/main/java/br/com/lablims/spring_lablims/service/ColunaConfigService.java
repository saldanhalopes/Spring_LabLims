package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Coluna;
import br.com.lablims.spring_lablims.domain.ColunaConfig;
import br.com.lablims.spring_lablims.model.ColunaConfigDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.ColunaConfigRepository;
import br.com.lablims.spring_lablims.repos.ColunaRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ColunaConfigService {

    private final ColunaConfigRepository colunaConfigRepository;
    private final ColunaRepository colunaRepository;

    public ColunaConfig findById(Integer id){
        return colunaConfigRepository.findById(id).orElse(null);
    }

    public SimplePage<ColunaConfigDTO> findAll(final String filter, final Pageable pageable) {
        Page<ColunaConfig> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = colunaConfigRepository.findAllById(integerFilter, pageable);
        } else {
            page = colunaConfigRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(colunaConfig -> mapToDTO(colunaConfig, new ColunaConfigDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public ColunaConfigDTO get(final Integer id) {
        return colunaConfigRepository.findById(id)
                .map(colunaConfig -> mapToDTO(colunaConfig, new ColunaConfigDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final ColunaConfigDTO colunaConfigDTO) {
        final ColunaConfig colunaConfig = new ColunaConfig();
        mapToEntity(colunaConfigDTO, colunaConfig);
        return colunaConfigRepository.save(colunaConfig).getId();
    }

    public void update(final Integer id, final ColunaConfigDTO colunaConfigDTO) {
        final ColunaConfig colunaConfig = colunaConfigRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(colunaConfigDTO, colunaConfig);
        colunaConfigRepository.save(colunaConfig);
    }

    public void delete(final Integer id) {
        colunaConfigRepository.deleteById(id);
    }

    private ColunaConfigDTO mapToDTO(final ColunaConfig colunaConfig,
            final ColunaConfigDTO colunaConfigDTO) {
        colunaConfigDTO.setId(colunaConfig.getId());
        colunaConfigDTO.setTipo(colunaConfig.getTipo());
        colunaConfigDTO.setConfiguracao(colunaConfig.getConfiguracao());
        colunaConfigDTO.setDescricao(colunaConfig.getDescricao());
        colunaConfigDTO.setVersion(colunaConfig.getVersion());
        return colunaConfigDTO;
    }

    private ColunaConfig mapToEntity(final ColunaConfigDTO colunaConfigDTO,
            final ColunaConfig colunaConfig) {
        colunaConfig.setTipo(colunaConfigDTO.getTipo());
        colunaConfig.setConfiguracao(colunaConfigDTO.getConfiguracao());
        colunaConfig.setDescricao(colunaConfigDTO.getDescricao());
        return colunaConfig;
    }

    public String getReferencedWarning(final Integer id) {
        final ColunaConfig colunaConfig = colunaConfigRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Coluna tipoColunaColuna = colunaRepository.findFirstByTipoColuna(colunaConfig);
        if (tipoColunaColuna != null) {
            return WebUtils.getMessage("colunaConfig.coluna.tipoColuna.referenced", tipoColunaColuna.getId());
        }
        final Coluna fabricanteColunaColuna = colunaRepository.findFirstByFabricanteColuna(colunaConfig);
        if (fabricanteColunaColuna != null) {
            return WebUtils.getMessage("colunaConfig.coluna.fabricanteColuna.referenced", fabricanteColunaColuna.getId());
        }
        final Coluna marcaColunaColuna = colunaRepository.findFirstByMarcaColuna(colunaConfig);
        if (marcaColunaColuna != null) {
            return WebUtils.getMessage("colunaConfig.coluna.marcaColuna.referenced", marcaColunaColuna.getId());
        }
        final Coluna faseColunaColuna = colunaRepository.findFirstByFaseColuna(colunaConfig);
        if (faseColunaColuna != null) {
            return WebUtils.getMessage("colunaConfig.coluna.faseColuna.referenced", faseColunaColuna.getId());
        }
        final Coluna tamanhoColunaColuna = colunaRepository.findFirstByTamanhoColuna(colunaConfig);
        if (tamanhoColunaColuna != null) {
            return WebUtils.getMessage("colunaConfig.coluna.tamanhoColuna.referenced", tamanhoColunaColuna.getId());
        }
        final Coluna diametroColunaColuna = colunaRepository.findFirstByDiametroColuna(colunaConfig);
        if (diametroColunaColuna != null) {
            return WebUtils.getMessage("colunaConfig.coluna.diametroColuna.referenced", diametroColunaColuna.getId());
        }
        final Coluna particulaColunaColuna = colunaRepository.findFirstByParticulaColuna(colunaConfig);
        if (particulaColunaColuna != null) {
            return WebUtils.getMessage("colunaConfig.coluna.particulaColuna.referenced", particulaColunaColuna.getId());
        }
        return null;
    }

}
