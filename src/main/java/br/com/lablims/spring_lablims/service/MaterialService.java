package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Lote;
import br.com.lablims.spring_lablims.domain.Material;
import br.com.lablims.spring_lablims.domain.MaterialTipo;
import br.com.lablims.spring_lablims.domain.MetodologiaVersao;
import br.com.lablims.spring_lablims.model.MaterialDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.LoteRepository;
import br.com.lablims.spring_lablims.repos.MaterialRepository;
import br.com.lablims.spring_lablims.repos.MaterialTipoRepository;
import br.com.lablims.spring_lablims.repos.MetodologiaVersaoRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final MaterialTipoRepository materialTipoRepository;
    private final MetodologiaVersaoRepository metodologiaVersaoRepository;
    private final LoteRepository loteRepository;

    public Material findById(Integer id){
        return materialRepository.findById(id).orElse(null);
    }

    public MaterialService(final MaterialRepository materialRepository,
            final MaterialTipoRepository materialTipoRepository,
            final MetodologiaVersaoRepository metodologiaVersaoRepository,
            final LoteRepository loteRepository) {
        this.materialRepository = materialRepository;
        this.materialTipoRepository = materialTipoRepository;
        this.metodologiaVersaoRepository = metodologiaVersaoRepository;
        this.loteRepository = loteRepository;
    }

    public SimplePage<MaterialDTO> findAll(final String filter, final Pageable pageable) {
        Page<Material> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = materialRepository.findAllById(integerFilter, pageable);
        } else {
            page = materialRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(material -> mapToDTO(material, new MaterialDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public MaterialDTO get(final Integer id) {
        return materialRepository.findById(id)
                .map(material -> mapToDTO(material, new MaterialDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final MaterialDTO materialDTO) {
        final Material material = new Material();
        mapToEntity(materialDTO, material);
        return materialRepository.save(material).getId();
    }

    public void update(final Integer id, final MaterialDTO materialDTO) {
        final Material material = materialRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(materialDTO, material);
        materialRepository.save(material);
    }

    public void delete(final Integer id) {
        final Material material = materialRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        metodologiaVersaoRepository.findAllByMaterial(material)
                .forEach(metodologiaVersao -> metodologiaVersao.getMaterial().remove(material));
        materialRepository.delete(material);
    }

    private MaterialDTO mapToDTO(final Material material, final MaterialDTO materialDTO) {
        materialDTO.setId(material.getId());
        materialDTO.setControleEspecial(material.getControleEspecial());
        materialDTO.setFiscalizado(material.getFiscalizado());
        materialDTO.setItem(material.getItem());
        materialDTO.setMaterial(material.getMaterial());
        materialDTO.setTipoMaterial(material.getTipoMaterial() == null ? null : material.getTipoMaterial().getId());
        materialDTO.setVersion(material.getVersion());
        return materialDTO;
    }

    private Material mapToEntity(final MaterialDTO materialDTO, final Material material) {
        material.setControleEspecial(materialDTO.getControleEspecial());
        material.setFiscalizado(materialDTO.getFiscalizado());
        material.setItem(materialDTO.getItem());
        material.setMaterial(materialDTO.getMaterial());
        final MaterialTipo tipoMaterial = materialDTO.getTipoMaterial() == null ? null : materialTipoRepository.findById(materialDTO.getTipoMaterial())
                .orElseThrow(() -> new NotFoundException("tipoMaterial not found"));
        material.setTipoMaterial(tipoMaterial);
        return material;
    }

    public String getReferencedWarning(final Integer id) {
        final Material material = materialRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final MetodologiaVersao materialMetodologiaVersao = metodologiaVersaoRepository.findFirstByMaterial(material);
        if (materialMetodologiaVersao != null) {
            return WebUtils.getMessage("material.metodologiaVersao.material.referenced", materialMetodologiaVersao.getId());
        }
        final Lote materialLote = loteRepository.findFirstByMaterial(material);
        if (materialLote != null) {
            return WebUtils.getMessage("material.lote.material.referenced", materialLote.getId());
        }
        return null;
    }

}
