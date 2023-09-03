package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.ColunaStorage;
import br.com.lablims.spring_lablims.domain.ColunaStorageTipo;
import br.com.lablims.spring_lablims.domain.ColunaVaga;
import br.com.lablims.spring_lablims.domain.Setor;
import br.com.lablims.spring_lablims.model.ColunaStorageDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.ColunaStorageRepository;
import br.com.lablims.spring_lablims.repos.ColunaStorageTipoRepository;
import br.com.lablims.spring_lablims.repos.ColunaVagaRepository;
import br.com.lablims.spring_lablims.repos.SetorRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class ColunaStorageService {

    private final ColunaStorageRepository colunaStorageRepository;
    private final SetorRepository setorRepository;
    private final ColunaStorageTipoRepository colunaStorageTipoRepository;
    private final ColunaVagaRepository colunaVagaRepository;

    public ColunaStorage findById(Integer id){
        return colunaStorageRepository.findById(id).orElse(null);
    }

    public ColunaStorageService(final ColunaStorageRepository colunaStorageRepository,
            final SetorRepository setorRepository,
            final ColunaStorageTipoRepository colunaStorageTipoRepository,
            final ColunaVagaRepository colunaVagaRepository) {
        this.colunaStorageRepository = colunaStorageRepository;
        this.setorRepository = setorRepository;
        this.colunaStorageTipoRepository = colunaStorageTipoRepository;
        this.colunaVagaRepository = colunaVagaRepository;
    }

    public SimplePage<ColunaStorageDTO> findAll(final String filter, final Pageable pageable) {
        Page<ColunaStorage> page;
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
        final ColunaStorage colunaStorage = new ColunaStorage();
        mapToEntity(colunaStorageDTO, colunaStorage);
        return colunaStorageRepository.save(colunaStorage).getId();
    }

    public void update(final Integer id, final ColunaStorageDTO colunaStorageDTO) {
        final ColunaStorage colunaStorage = colunaStorageRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(colunaStorageDTO, colunaStorage);
        colunaStorageRepository.save(colunaStorage);
    }

    public void delete(final Integer id) {
        colunaStorageRepository.deleteById(id);
    }

    private ColunaStorageDTO mapToDTO(final ColunaStorage colunaStorage,
            final ColunaStorageDTO colunaStorageDTO) {
        colunaStorageDTO.setId(colunaStorage.getId());
        colunaStorageDTO.setCodigo(colunaStorage.getCodigo());
        colunaStorageDTO.setObs(colunaStorage.getObs());
        colunaStorageDTO.setSetor(colunaStorage.getSetor() == null ? null : colunaStorage.getSetor().getId());
        colunaStorageDTO.setTipo(colunaStorage.getTipo() == null ? null : colunaStorage.getTipo().getId());
        colunaStorageDTO.setVersion(colunaStorage.getVersion());
        return colunaStorageDTO;
    }

    private ColunaStorage mapToEntity(final ColunaStorageDTO colunaStorageDTO,
            final ColunaStorage colunaStorage) {
        colunaStorage.setCodigo(colunaStorageDTO.getCodigo());
        colunaStorage.setObs(colunaStorageDTO.getObs());
        final Setor setor = colunaStorageDTO.getSetor() == null ? null : setorRepository.findById(colunaStorageDTO.getSetor())
                .orElseThrow(() -> new NotFoundException("setor not found"));
        colunaStorage.setSetor(setor);
        final ColunaStorageTipo tipo = colunaStorageDTO.getTipo() == null ? null : colunaStorageTipoRepository.findById(colunaStorageDTO.getTipo())
                .orElseThrow(() -> new NotFoundException("tipo not found"));
        colunaStorage.setTipo(tipo);
        return colunaStorage;
    }

    public String getReferencedWarning(final Integer id) {
        final ColunaStorage colunaStorage = colunaStorageRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final ColunaVaga colunaStorageColunaVaga = colunaVagaRepository.findFirstByColunaStorage(colunaStorage);
        if (colunaStorageColunaVaga != null) {
            return WebUtils.getMessage("colunaStorage.colunaVaga.colunaStorage.referenced", colunaStorageColunaVaga.getId());
        }
        return null;
    }

}
