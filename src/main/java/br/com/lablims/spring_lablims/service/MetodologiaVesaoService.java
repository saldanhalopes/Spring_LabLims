package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Arquivos;
import br.com.lablims.spring_lablims.domain.ColunaUtil;
import br.com.lablims.spring_lablims.domain.Material;
import br.com.lablims.spring_lablims.domain.Metodologia;
import br.com.lablims.spring_lablims.domain.MetodologiaStatus;
import br.com.lablims.spring_lablims.domain.MetodologiaVesao;
import br.com.lablims.spring_lablims.domain.PlanoAnalise;
import br.com.lablims.spring_lablims.model.MetodologiaVesaoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.ArquivosRepository;
import br.com.lablims.spring_lablims.repos.ColunaUtilRepository;
import br.com.lablims.spring_lablims.repos.MaterialRepository;
import br.com.lablims.spring_lablims.repos.MetodologiaRepository;
import br.com.lablims.spring_lablims.repos.MetodologiaStatusRepository;
import br.com.lablims.spring_lablims.repos.MetodologiaVesaoRepository;
import br.com.lablims.spring_lablims.repos.PlanoAnaliseRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class MetodologiaVesaoService {

    private final MetodologiaVesaoRepository metodologiaVesaoRepository;
    private final MetodologiaRepository metodologiaRepository;
    private final ArquivosRepository arquivosRepository;
    private final MaterialRepository materialRepository;
    private final MetodologiaStatusRepository metodologiaStatusRepository;
    private final ColunaUtilRepository colunaUtilRepository;
    private final PlanoAnaliseRepository planoAnaliseRepository;

    public MetodologiaVesao findById(Integer id){
        return metodologiaVesaoRepository.findById(id).orElse(null);
    }

    public MetodologiaVesaoService(final MetodologiaVesaoRepository metodologiaVesaoRepository,
            final MetodologiaRepository metodologiaRepository,
            final ArquivosRepository arquivosRepository,
            final MaterialRepository materialRepository,
            final MetodologiaStatusRepository metodologiaStatusRepository,
            final ColunaUtilRepository colunaUtilRepository,
            final PlanoAnaliseRepository planoAnaliseRepository) {
        this.metodologiaVesaoRepository = metodologiaVesaoRepository;
        this.metodologiaRepository = metodologiaRepository;
        this.arquivosRepository = arquivosRepository;
        this.materialRepository = materialRepository;
        this.metodologiaStatusRepository = metodologiaStatusRepository;
        this.colunaUtilRepository = colunaUtilRepository;
        this.planoAnaliseRepository = planoAnaliseRepository;
    }

    public SimplePage<MetodologiaVesaoDTO> findAll(final String filter, final Pageable pageable) {
        Page<MetodologiaVesao> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = metodologiaVesaoRepository.findAllById(integerFilter, pageable);
        } else {
            page = metodologiaVesaoRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(metodologiaVesao -> mapToDTO(metodologiaVesao, new MetodologiaVesaoDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public MetodologiaVesaoDTO get(final Integer id) {
        return metodologiaVesaoRepository.findById(id)
                .map(metodologiaVesao -> mapToDTO(metodologiaVesao, new MetodologiaVesaoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final MetodologiaVesaoDTO metodologiaVesaoDTO) {
        final MetodologiaVesao metodologiaVesao = new MetodologiaVesao();
        mapToEntity(metodologiaVesaoDTO, metodologiaVesao);
        return metodologiaVesaoRepository.save(metodologiaVesao).getId();
    }

    public void update(final Integer id, final MetodologiaVesaoDTO metodologiaVesaoDTO) {
        final MetodologiaVesao metodologiaVesao = metodologiaVesaoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(metodologiaVesaoDTO, metodologiaVesao);
        metodologiaVesaoRepository.save(metodologiaVesao);
    }

    public void delete(final Integer id) {
        metodologiaVesaoRepository.deleteById(id);
    }

    private MetodologiaVesaoDTO mapToDTO(final MetodologiaVesao metodologiaVesao,
            final MetodologiaVesaoDTO metodologiaVesaoDTO) {
        metodologiaVesaoDTO.setId(metodologiaVesao.getId());
        metodologiaVesaoDTO.setDataRevisao(metodologiaVesao.getDataRevisao());
        metodologiaVesaoDTO.setDataProximaRevisao(metodologiaVesao.getDataProximaRevisao());
        metodologiaVesaoDTO.setObs(metodologiaVesao.getObs());
        metodologiaVesaoDTO.setMetodologia(metodologiaVesao.getMetodologia() == null ? null : metodologiaVesao.getMetodologia().getId());
        metodologiaVesaoDTO.setAnexo(metodologiaVesao.getAnexo() == null ? null : metodologiaVesao.getAnexo().getId());
        metodologiaVesaoDTO.setVersion(metodologiaVesao.getVersion());
        metodologiaVesaoDTO.setMaterial(metodologiaVesao.getMaterial().stream()
                .map(material -> material.getId())
                .toList());
        metodologiaVesaoDTO.setStatus(metodologiaVesao.getStatus() == null ? null : metodologiaVesao.getStatus().getId());
        return metodologiaVesaoDTO;
    }

    private MetodologiaVesao mapToEntity(final MetodologiaVesaoDTO metodologiaVesaoDTO,
            final MetodologiaVesao metodologiaVesao) {
        metodologiaVesao.setDataRevisao(metodologiaVesaoDTO.getDataRevisao());
        metodologiaVesao.setDataProximaRevisao(metodologiaVesaoDTO.getDataProximaRevisao());
        metodologiaVesao.setObs(metodologiaVesaoDTO.getObs());
        final Metodologia metodologia = metodologiaVesaoDTO.getMetodologia() == null ? null : metodologiaRepository.findById(metodologiaVesaoDTO.getMetodologia())
                .orElseThrow(() -> new NotFoundException("metodologia not found"));
        metodologiaVesao.setMetodologia(metodologia);
        final Arquivos anexo = metodologiaVesaoDTO.getAnexo() == null ? null : arquivosRepository.findById(metodologiaVesaoDTO.getAnexo())
                .orElseThrow(() -> new NotFoundException("anexo not found"));
        metodologiaVesao.setAnexo(anexo);
        final List<Material> material = materialRepository.findAllById(
                metodologiaVesaoDTO.getMaterial() == null ? Collections.emptyList() : metodologiaVesaoDTO.getMaterial());
        if (material.size() != (metodologiaVesaoDTO.getMaterial() == null ? 0 : metodologiaVesaoDTO.getMaterial().size())) {
            throw new NotFoundException("one of material not found");
        }
        metodologiaVesao.setMaterial(material.stream().collect(Collectors.toSet()));
        final MetodologiaStatus status = metodologiaVesaoDTO.getStatus() == null ? null : metodologiaStatusRepository.findById(metodologiaVesaoDTO.getStatus())
                .orElseThrow(() -> new NotFoundException("status not found"));
        metodologiaVesao.setStatus(status);
        return metodologiaVesao;
    }

    public String getReferencedWarning(final Integer id) {
        final MetodologiaVesao metodologiaVesao = metodologiaVesaoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final ColunaUtil metodologiaVersaoColunaUtil = colunaUtilRepository.findFirstByMetodologiaVersao(metodologiaVesao);
        if (metodologiaVersaoColunaUtil != null) {
            return WebUtils.getMessage("metodologiaVesao.colunaUtil.metodologiaVersao.referenced", metodologiaVersaoColunaUtil.getId());
        }
        final PlanoAnalise metodologiaVersaoPlanoAnalise = planoAnaliseRepository.findFirstByMetodologiaVersao(metodologiaVesao);
        if (metodologiaVersaoPlanoAnalise != null) {
            return WebUtils.getMessage("metodologiaVesao.planoAnalise.metodologiaVersao.referenced", metodologiaVersaoPlanoAnalise.getId());
        }
        return null;
    }

}
