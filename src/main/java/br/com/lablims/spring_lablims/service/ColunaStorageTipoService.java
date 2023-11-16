package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Storage;
import br.com.lablims.spring_lablims.domain.StorageTipo;
import br.com.lablims.spring_lablims.model.ColunaStorageTipoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.ColunaStorageRepository;
import br.com.lablims.spring_lablims.repos.ColunaStorageTipoRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ColunaStorageTipoService {

    private final ColunaStorageTipoRepository colunaStorageTipoRepository;
    private final ColunaStorageRepository colunaStorageRepository;

    public StorageTipo findById(Integer id){
        return colunaStorageTipoRepository.findById(id).orElse(null);
    }

    public SimplePage<ColunaStorageTipoDTO> findAll(final String filter, final Pageable pageable) {
        Page<StorageTipo> page;
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
        final StorageTipo storageTipo = new StorageTipo();
        mapToEntity(colunaStorageTipoDTO, storageTipo);
        return colunaStorageTipoRepository.save(storageTipo).getId();
    }

    public void update(final Integer id, final ColunaStorageTipoDTO colunaStorageTipoDTO) {
        final StorageTipo storageTipo = colunaStorageTipoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(colunaStorageTipoDTO, storageTipo);
        colunaStorageTipoRepository.save(storageTipo);
    }

    public void delete(final Integer id) {
        colunaStorageTipoRepository.deleteById(id);
    }

    private ColunaStorageTipoDTO mapToDTO(final StorageTipo storageTipo,
            final ColunaStorageTipoDTO colunaStorageTipoDTO) {
        colunaStorageTipoDTO.setId(storageTipo.getId());
        colunaStorageTipoDTO.setTipo(storageTipo.getTipo());
        colunaStorageTipoDTO.setVersion(storageTipo.getVersion());
        return colunaStorageTipoDTO;
    }

    private StorageTipo mapToEntity(final ColunaStorageTipoDTO colunaStorageTipoDTO,
                                    final StorageTipo storageTipo) {
        storageTipo.setTipo(colunaStorageTipoDTO.getTipo());
        return storageTipo;
    }

    public String getReferencedWarning(final Integer id) {
        final StorageTipo storageTipo = colunaStorageTipoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Storage tipoStorage = colunaStorageRepository.findFirstByTipo(storageTipo);
        if (tipoStorage != null) {
            return WebUtils.getMessage("colunaStorageTipo.colunaStorage.tipo.referenced", tipoStorage.getId());
        }
        return null;
    }

}
