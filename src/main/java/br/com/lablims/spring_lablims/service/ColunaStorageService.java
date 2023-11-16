package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Storage;
import br.com.lablims.spring_lablims.domain.StorageTipo;
import br.com.lablims.spring_lablims.domain.StorageEndereco;
import br.com.lablims.spring_lablims.domain.Setor;
import br.com.lablims.spring_lablims.model.ColunaStorageDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.ColunaStorageRepository;
import br.com.lablims.spring_lablims.repos.ColunaStorageTipoRepository;
import br.com.lablims.spring_lablims.repos.StorageEnderecoRepository;
import br.com.lablims.spring_lablims.repos.SetorRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ColunaStorageService {

    private final ColunaStorageRepository colunaStorageRepository;
    private final SetorRepository setorRepository;
    private final ColunaStorageTipoRepository colunaStorageTipoRepository;
    private final StorageEnderecoRepository storageEnderecoRepository;

    public Storage findById(Integer id){
        return colunaStorageRepository.findById(id).orElse(null);
    }

    public SimplePage<ColunaStorageDTO> findAll(final String filter, final Pageable pageable) {
        Page<Storage> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = colunaStorageRepository.findAllById(integerFilter, pageable);
        } else {
            page = colunaStorageRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(colunaStorage -> mapToDTO(colunaStorage, new ColunaStorageDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public ColunaStorageDTO get(final Integer id) {
        return colunaStorageRepository.findById(id)
                .map(colunaStorage -> mapToDTO(colunaStorage, new ColunaStorageDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final ColunaStorageDTO colunaStorageDTO) {
        final Storage storage = new Storage();
        mapToEntity(colunaStorageDTO, storage);
        return colunaStorageRepository.save(storage).getId();
    }

    public void update(final Integer id, final ColunaStorageDTO colunaStorageDTO) {
        final Storage storage = colunaStorageRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(colunaStorageDTO, storage);
        colunaStorageRepository.save(storage);
    }

    public void delete(final Integer id) {
        colunaStorageRepository.deleteById(id);
    }

    private ColunaStorageDTO mapToDTO(final Storage storage,
            final ColunaStorageDTO colunaStorageDTO) {
        colunaStorageDTO.setId(storage.getId());
        colunaStorageDTO.setCodigo(storage.getCodigo());
        colunaStorageDTO.setObs(storage.getObs());
        colunaStorageDTO.setSetor(storage.getSetor() == null ? null : storage.getSetor().getId());
        colunaStorageDTO.setTipo(storage.getTipo() == null ? null : storage.getTipo().getId());
        colunaStorageDTO.setVersion(storage.getVersion());
        return colunaStorageDTO;
    }

    private Storage mapToEntity(final ColunaStorageDTO colunaStorageDTO,
                                final Storage storage) {
        storage.setCodigo(colunaStorageDTO.getCodigo());
        storage.setObs(colunaStorageDTO.getObs());
        final Setor setor = colunaStorageDTO.getSetor() == null ? null : setorRepository.findById(colunaStorageDTO.getSetor())
                .orElseThrow(() -> new NotFoundException("setor not found"));
        storage.setSetor(setor);
        final StorageTipo tipo = colunaStorageDTO.getTipo() == null ? null : colunaStorageTipoRepository.findById(colunaStorageDTO.getTipo())
                .orElseThrow(() -> new NotFoundException("tipo not found"));
        storage.setTipo(tipo);
        return storage;
    }

    public String getReferencedWarning(final Integer id) {
        final Storage storage = colunaStorageRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final StorageEndereco colunaStorageStorageEndereco = storageEnderecoRepository.findFirstByStorage(storage);
        if (colunaStorageStorageEndereco != null) {
            return WebUtils.getMessage("colunaStorage.colunaVaga.colunaStorage.referenced", colunaStorageStorageEndereco.getId());
        }
        return null;
    }

}
