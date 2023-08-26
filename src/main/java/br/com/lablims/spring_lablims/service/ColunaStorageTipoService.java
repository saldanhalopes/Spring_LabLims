package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.ColunaStorage;
import br.com.lablims.spring_lablims.domain.ColunaStorageTipo;
import br.com.lablims.spring_lablims.model.ColunaStorageTipoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.ColunaStorageRepository;
import br.com.lablims.spring_lablims.repos.ColunaStorageTipoRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class ColunaStorageTipoService {

    private final ColunaStorageTipoRepository colunaStorageTipoRepository;
    private final ColunaStorageRepository colunaStorageRepository;

    public ColunaStorageTipo findById(Integer id){
        return colunaStorageTipoRepository.findById(id).orElse(null);
    }

    public ColunaStorageTipoService(final ColunaStorageTipoRepository colunaStorageTipoRepository,
            final ColunaStorageRepository colunaStorageRepository) {
        this.colunaStorageTipoRepository = colunaStorageTipoRepository;
        this.colunaStorageRepository = colunaStorageRepository;
    }

    public SimplePage<ColunaStorageTipoDTO> findAll(final String filter, final Pageable pageable) {
        Page<ColunaStorageTipo> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = colunaStorageTipoRepository.findAllById(integerFilter, pageable);
        } else {
            page = colunaStorageTipoRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(colunaStorageTipo -> mapToDTO(colunaStorageTipo, new ColunaStorageTipoDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public ColunaStorageTipoDTO get(final Integer id) {
        return colunaStorageTipoRepository.findById(id)
                .map(colunaStorageTipo -> mapToDTO(colunaStorageTipo, new ColunaStorageTipoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final ColunaStorageTipoDTO colunaStorageTipoDTO) {
        final ColunaStorageTipo colunaStorageTipo = new ColunaStorageTipo();
        mapToEntity(colunaStorageTipoDTO, colunaStorageTipo);
        return colunaStorageTipoRepository.save(colunaStorageTipo).getId();
    }

    public void update(final Integer id, final ColunaStorageTipoDTO colunaStorageTipoDTO) {
        final ColunaStorageTipo colunaStorageTipo = colunaStorageTipoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(colunaStorageTipoDTO, colunaStorageTipo);
        colunaStorageTipoRepository.save(colunaStorageTipo);
    }

    public void delete(final Integer id) {
        colunaStorageTipoRepository.deleteById(id);
    }

    private ColunaStorageTipoDTO mapToDTO(final ColunaStorageTipo colunaStorageTipo,
            final ColunaStorageTipoDTO colunaStorageTipoDTO) {
        colunaStorageTipoDTO.setId(colunaStorageTipo.getId());
        colunaStorageTipoDTO.setTipo(colunaStorageTipo.getTipo());
        colunaStorageTipoDTO.setVersion(colunaStorageTipo.getVersion());
        return colunaStorageTipoDTO;
    }

    private ColunaStorageTipo mapToEntity(final ColunaStorageTipoDTO colunaStorageTipoDTO,
            final ColunaStorageTipo colunaStorageTipo) {
        colunaStorageTipo.setTipo(colunaStorageTipoDTO.getTipo());
        return colunaStorageTipo;
    }

    public String getReferencedWarning(final Integer id) {
        final ColunaStorageTipo colunaStorageTipo = colunaStorageTipoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final ColunaStorage tipoColunaStorage = colunaStorageRepository.findFirstByTipo(colunaStorageTipo);
        if (tipoColunaStorage != null) {
            return WebUtils.getMessage("colunaStorageTipo.colunaStorage.tipo.referenced", tipoColunaStorage.getId());
        }
        return null;
    }

}
