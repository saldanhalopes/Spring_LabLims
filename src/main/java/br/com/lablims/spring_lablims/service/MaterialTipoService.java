package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Material;
import br.com.lablims.spring_lablims.domain.MaterialTipo;
import br.com.lablims.spring_lablims.model.MaterialTipoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.MaterialRepository;
import br.com.lablims.spring_lablims.repos.MaterialTipoRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class MaterialTipoService {

    private final MaterialTipoRepository materialTipoRepository;
    private final MaterialRepository materialRepository;

    public MaterialTipo findById(Integer id){
        return materialTipoRepository.findById(id).orElse(null);
    }

    public MaterialTipoService(final MaterialTipoRepository materialTipoRepository,
            final MaterialRepository materialRepository) {
        this.materialTipoRepository = materialTipoRepository;
        this.materialRepository = materialRepository;
    }

    public SimplePage<MaterialTipoDTO> findAll(final String filter, final Pageable pageable) {
        Page<MaterialTipo> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = materialTipoRepository.findAllById(integerFilter, pageable);
        } else {
            page = materialTipoRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(materialTipo -> mapToDTO(materialTipo, new MaterialTipoDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public MaterialTipoDTO get(final Integer id) {
        return materialTipoRepository.findById(id)
                .map(materialTipo -> mapToDTO(materialTipo, new MaterialTipoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final MaterialTipoDTO materialTipoDTO) {
        final MaterialTipo materialTipo = new MaterialTipo();
        mapToEntity(materialTipoDTO, materialTipo);
        return materialTipoRepository.save(materialTipo).getId();
    }

    public void update(final Integer id, final MaterialTipoDTO materialTipoDTO) {
        final MaterialTipo materialTipo = materialTipoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(materialTipoDTO, materialTipo);
        materialTipoRepository.save(materialTipo);
    }

    public void delete(final Integer id) {
        materialTipoRepository.deleteById(id);
    }

    private MaterialTipoDTO mapToDTO(final MaterialTipo materialTipo,
            final MaterialTipoDTO materialTipoDTO) {
        materialTipoDTO.setId(materialTipo.getId());
        materialTipoDTO.setSigla(materialTipo.getSigla());
        materialTipoDTO.setTipo(materialTipo.getTipo());
        materialTipoDTO.setVersion(materialTipo.getVersion());
        return materialTipoDTO;
    }

    private MaterialTipo mapToEntity(final MaterialTipoDTO materialTipoDTO,
            final MaterialTipo materialTipo) {
        materialTipo.setSigla(materialTipoDTO.getSigla());
        materialTipo.setTipo(materialTipoDTO.getTipo());
        return materialTipo;
    }

    public String getReferencedWarning(final Integer id) {
        final MaterialTipo materialTipo = materialTipoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Material tipoMaterialMaterial = materialRepository.findFirstByTipoMaterial(materialTipo);
        if (tipoMaterialMaterial != null) {
            return WebUtils.getMessage("materialTipo.material.tipoMaterial.referenced", tipoMaterialMaterial.getId());
        }
        return null;
    }

}
