package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Storage;
import br.com.lablims.spring_lablims.domain.ColunaUtil;
import br.com.lablims.spring_lablims.domain.StorageEndereco;
import br.com.lablims.spring_lablims.model.StorageEnderecoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.ColunaStorageRepository;
import br.com.lablims.spring_lablims.repos.ColunaUtilRepository;
import br.com.lablims.spring_lablims.repos.StorageEnderecoRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class StorageEnderecoService {

    private final StorageEnderecoRepository colunaVagaRepository;
    private final ColunaStorageRepository colunaStorageRepository;
    private final ColunaUtilRepository colunaUtilRepository;

    public StorageEndereco findById(Integer id){
        return colunaVagaRepository.findById(id).orElse(null);
    }

    public SimplePage<StorageEnderecoDTO> findAll(final String filter, final Pageable pageable) {
        Page<StorageEndereco> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = colunaVagaRepository.findAllById(integerFilter, pageable);
        } else {
            page = colunaVagaRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(colunaVaga -> mapToDTO(colunaVaga, new StorageEnderecoDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public StorageEnderecoDTO get(final Integer id) {
        return colunaVagaRepository.findById(id)
                .map(colunaVaga -> mapToDTO(colunaVaga, new StorageEnderecoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final StorageEnderecoDTO colunaVagaDTO) {
        final StorageEndereco storageEndereco = new StorageEndereco();
        mapToEntity(colunaVagaDTO, storageEndereco);
        return colunaVagaRepository.save(storageEndereco).getId();
    }

    public void update(final Integer id, final StorageEnderecoDTO colunaVagaDTO) {
        final StorageEndereco storageEndereco = colunaVagaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(colunaVagaDTO, storageEndereco);
        colunaVagaRepository.save(storageEndereco);
    }

    public void delete(final Integer id) {
        colunaVagaRepository.deleteById(id);
    }

    private StorageEnderecoDTO mapToDTO(final StorageEndereco storageEndereco, final StorageEnderecoDTO colunaVagaDTO) {
        colunaVagaDTO.setId(storageEndereco.getId());
        colunaVagaDTO.setEndereco(storageEndereco.getEndereco());
        colunaVagaDTO.setObs(storageEndereco.getObs());
        colunaVagaDTO.setColunaStorage(storageEndereco.getStorage() == null ? null : storageEndereco.getStorage().getId());
        colunaVagaDTO.setVersion(storageEndereco.getVersion());
        return colunaVagaDTO;
    }

    private StorageEndereco mapToEntity(final StorageEnderecoDTO colunaVagaDTO, final StorageEndereco storageEndereco) {
        storageEndereco.setEndereco(colunaVagaDTO.getEndereco());
        storageEndereco.setObs(colunaVagaDTO.getObs());
        final Storage storage = colunaVagaDTO.getColunaStorage() == null ? null : colunaStorageRepository.findById(colunaVagaDTO.getColunaStorage())
                .orElseThrow(() -> new NotFoundException("storage not found"));
        storageEndereco.setStorage(storage);
        return storageEndereco;
    }

    public String getReferencedWarning(final Integer id) {
        final StorageEndereco storageEndereco = colunaVagaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final ColunaUtil colunaVagaColunaUtil = colunaUtilRepository.findFirstByStorageEndereco(storageEndereco);
        if (colunaVagaColunaUtil != null) {
            return WebUtils.getMessage("colunaVaga.colunaUtil.colunaVaga.referenced", colunaVagaColunaUtil.getId());
        }
        return null;
    }

}
