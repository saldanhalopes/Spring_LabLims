package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Storage;
import br.com.lablims.spring_lablims.domain.ColunaUtil;
import br.com.lablims.spring_lablims.domain.StorageEndereco;
import br.com.lablims.spring_lablims.model.StorageEnderecoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.StorageRepository;
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

    private final StorageEnderecoRepository storageEnderecoRepository;
    private final StorageRepository storageRepository;
    private final ColunaUtilRepository colunaUtilRepository;

    public StorageEndereco findById(Integer id){
        return storageEnderecoRepository.findById(id).orElse(null);
    }

    public SimplePage<StorageEnderecoDTO> findAll(final Pageable pageable) {
        Page<StorageEndereco> page = storageEnderecoRepository.findAllOfStorageEndereco(pageable);
        return new SimplePage<>(page.getContent()
                .stream()
                .map(colunaVaga -> mapToDTO(colunaVaga, new StorageEnderecoDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public StorageEnderecoDTO get(final Integer id) {
        return storageEnderecoRepository.findStorageEnderecoById(id)
                .map(colunaVaga -> mapToDTO(colunaVaga, new StorageEnderecoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final StorageEnderecoDTO colunaVagaDTO) {
        final StorageEndereco storageEndereco = new StorageEndereco();
        mapToEntity(colunaVagaDTO, storageEndereco);
        return storageEnderecoRepository.save(storageEndereco).getId();
    }

    public void update(final Integer id, final StorageEnderecoDTO colunaVagaDTO) {
        final StorageEndereco storageEndereco = storageEnderecoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(colunaVagaDTO, storageEndereco);
        storageEnderecoRepository.save(storageEndereco);
    }

    public void delete(final Integer id) {
        storageEnderecoRepository.deleteById(id);
    }

    private StorageEnderecoDTO mapToDTO(final StorageEndereco storageEndereco, final StorageEnderecoDTO colunaVagaDTO) {
        colunaVagaDTO.setId(storageEndereco.getId());
        colunaVagaDTO.setEndereco(storageEndereco.getEndereco());
        colunaVagaDTO.setObs(storageEndereco.getObs());
        colunaVagaDTO.setStorage(storageEndereco.getStorage() == null ? null : storageEndereco.getStorage().getId());
        colunaVagaDTO.setStorageNome(storageEndereco.getStorage().getStorage() == null ? null : storageEndereco.getStorage().getStorage());
        colunaVagaDTO.setVersion(storageEndereco.getVersion());
        return colunaVagaDTO;
    }

    private StorageEndereco mapToEntity(final StorageEnderecoDTO colunaVagaDTO, final StorageEndereco storageEndereco) {
        storageEndereco.setEndereco(colunaVagaDTO.getEndereco());
        storageEndereco.setObs(colunaVagaDTO.getObs());
        final Storage storage = colunaVagaDTO.getStorage() == null ? null : storageRepository.findById(colunaVagaDTO.getStorage())
                .orElseThrow(() -> new NotFoundException("storage not found"));
        storageEndereco.setStorage(storage);
        return storageEndereco;
    }

    public String getReferencedWarning(final Integer id) {
        final StorageEndereco storageEndereco = storageEnderecoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final ColunaUtil colunaVagaColunaUtil = colunaUtilRepository.findFirstByStorageEndereco(storageEndereco);
        if (colunaVagaColunaUtil != null) {
            return WebUtils.getMessage("entity.referenced", colunaVagaColunaUtil.getId());
        }
        return null;
    }

}
