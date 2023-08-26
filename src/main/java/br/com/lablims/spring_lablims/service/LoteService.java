package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.AmostraTipo;
import br.com.lablims.spring_lablims.domain.Campanha;
import br.com.lablims.spring_lablims.domain.Lote;
import br.com.lablims.spring_lablims.domain.LoteStatus;
import br.com.lablims.spring_lablims.domain.Material;
import br.com.lablims.spring_lablims.model.LoteDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.AmostraTipoRepository;
import br.com.lablims.spring_lablims.repos.CampanhaRepository;
import br.com.lablims.spring_lablims.repos.LoteRepository;
import br.com.lablims.spring_lablims.repos.LoteStatusRepository;
import br.com.lablims.spring_lablims.repos.MaterialRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class LoteService {

    private final LoteRepository loteRepository;
    private final MaterialRepository materialRepository;
    private final AmostraTipoRepository amostraTipoRepository;
    private final CampanhaRepository campanhaRepository;
    private final LoteStatusRepository loteStatusRepository;

    public Lote findById(Integer id){
        return loteRepository.findById(id).orElse(null);
    }

    public LoteService(final LoteRepository loteRepository,
            final MaterialRepository materialRepository,
            final AmostraTipoRepository amostraTipoRepository,
            final CampanhaRepository campanhaRepository,
            final LoteStatusRepository loteStatusRepository) {
        this.loteRepository = loteRepository;
        this.materialRepository = materialRepository;
        this.amostraTipoRepository = amostraTipoRepository;
        this.campanhaRepository = campanhaRepository;
        this.loteStatusRepository = loteStatusRepository;
    }

    public SimplePage<LoteDTO> findAll(final String filter, final Pageable pageable) {
        Page<Lote> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = loteRepository.findAllById(integerFilter, pageable);
        } else {
            page = loteRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(lote -> mapToDTO(lote, new LoteDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public LoteDTO get(final Integer id) {
        return loteRepository.findById(id)
                .map(lote -> mapToDTO(lote, new LoteDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final LoteDTO loteDTO) {
        final Lote lote = new Lote();
        mapToEntity(loteDTO, lote);
        return loteRepository.save(lote).getId();
    }

    public void update(final Integer id, final LoteDTO loteDTO) {
        final Lote lote = loteRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(loteDTO, lote);
        loteRepository.save(lote);
    }

    public void delete(final Integer id) {
        final Lote lote = loteRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        campanhaRepository.findAllByLotes(lote)
                .forEach(campanha -> campanha.getLotes().remove(lote));
        loteRepository.delete(lote);
    }

    private LoteDTO mapToDTO(final Lote lote, final LoteDTO loteDTO) {
        loteDTO.setId(lote.getId());
        loteDTO.setLote(lote.getLote());
        loteDTO.setQtdEstoque(lote.getQtdEstoque());
        loteDTO.setDataStatus(lote.getDataStatus());
        loteDTO.setDataEntrada(lote.getDataEntrada());
        loteDTO.setDataInicioAnalise(lote.getDataInicioAnalise());
        loteDTO.setDataLiberacao(lote.getDataLiberacao());
        loteDTO.setDataEnvioGarantia(lote.getDataEnvioGarantia());
        loteDTO.setDataNecessidade(lote.getDataNecessidade());
        loteDTO.setDataValidade(lote.getDataValidade());
        loteDTO.setDataImpressao(lote.getDataImpressao());
        loteDTO.setNumeroDocumento(lote.getNumeroDocumento());
        loteDTO.setComplemento(lote.getComplemento());
        loteDTO.setObs(lote.getObs());
        loteDTO.setMaterial(lote.getMaterial() == null ? null : lote.getMaterial().getId());
        loteDTO.setAmostraTipo(lote.getAmostraTipo() == null ? null : lote.getAmostraTipo().getId());
        loteDTO.setVersion(lote.getVersion());
        return loteDTO;
    }

    private Lote mapToEntity(final LoteDTO loteDTO, final Lote lote) {
        lote.setLote(loteDTO.getLote());
        lote.setQtdEstoque(loteDTO.getQtdEstoque());
        lote.setDataStatus(loteDTO.getDataStatus());
        lote.setDataEntrada(loteDTO.getDataEntrada());
        lote.setDataInicioAnalise(loteDTO.getDataInicioAnalise());
        lote.setDataLiberacao(loteDTO.getDataLiberacao());
        lote.setDataEnvioGarantia(loteDTO.getDataEnvioGarantia());
        lote.setDataNecessidade(loteDTO.getDataNecessidade());
        lote.setDataValidade(loteDTO.getDataValidade());
        lote.setDataImpressao(loteDTO.getDataImpressao());
        lote.setNumeroDocumento(loteDTO.getNumeroDocumento());
        lote.setComplemento(loteDTO.getComplemento());
        lote.setObs(loteDTO.getObs());
        final Material material = loteDTO.getMaterial() == null ? null : materialRepository.findById(loteDTO.getMaterial())
                .orElseThrow(() -> new NotFoundException("material not found"));
        lote.setMaterial(material);
        final AmostraTipo amostraTipo = loteDTO.getAmostraTipo() == null ? null : amostraTipoRepository.findById(loteDTO.getAmostraTipo())
                .orElseThrow(() -> new NotFoundException("amostraTipo not found"));
        lote.setAmostraTipo(amostraTipo);
        return lote;
    }

    public String getReferencedWarning(final Integer id) {
        final Lote lote = loteRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final LoteStatus loteLoteStatus = loteStatusRepository.findFirstByLote(lote);
        if (loteLoteStatus != null) {
            return WebUtils.getMessage("lote.loteStatus.lote.referenced", loteLoteStatus.getId());
        }
        final Campanha lotesCampanha = campanhaRepository.findFirstByLotes(lote);
        if (lotesCampanha != null) {
            return WebUtils.getMessage("lote.campanha.lotes.referenced", lotesCampanha.getId());
        }
        return null;
    }

}
