package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.MaterialDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.util.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final MaterialTipoRepository materialTipoRepository;
    private final UnidadeMedidaRepository unidadeMedidaRepository;
    private final FornecedorRepository fornecedorRepository;

    public Material findById(Integer id){
        return materialRepository.findById(id).orElse(null);
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

    public SimplePage<MaterialDTO> findAllOfMateriais(final Pageable pageable) {
        Page<Material> page;
        page = materialRepository.findAllOfMateriais(pageable);
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
        materialRepository.delete(material);
    }

    private MaterialDTO mapToDTO(final Material material, final MaterialDTO materialDTO) {
        materialDTO.setId(material.getId());
        materialDTO.setMaterial(material.getMaterial());
        materialDTO.setAtivo(material.isAtivo());
        materialDTO.setDescricao(material.getDescricao());
        materialDTO.setCodigo(material.getCodigo());
        materialDTO.setNumeroIdentificacao(material.getNumeroIdentificacao());
        materialDTO.setControlado(material.getControlado());
        materialDTO.setPartNumber(material.getPartNumber());
        materialDTO.setCasNumber(material.getCasNumber());
        materialDTO.setFabricante(material.getFabricante());
        materialDTO.setMarca(material.getMarca());
        materialDTO.setModelo(material.getModelo());
        materialDTO.setEstoqueMin(material.getEstoqueMin());
        materialDTO.setEstoqueMax(material.getEstoqueMax());
        materialDTO.setCompraUnica(material.isCompraUnica());
        materialDTO.setObs(material.getObs());
        materialDTO.setMaterialTipo(material.getMaterialTipo() == null ? null : material.getMaterialTipo().getId());
        materialDTO.setMaterialTipoNome(material.getMaterialTipo().getTipo() == null ? null : material.getMaterialTipo().getTipo());
        materialDTO.setUnidade(material.getUnidade() == null ? null : material.getUnidade().getId());
        materialDTO.setUnidadeNome(material.getUnidade().getUnidade() == null ? null : material.getUnidade().getUnidade());
        materialDTO.setFornecedor(material.getFornecedor() == null ? null : material.getFornecedor().getId());
        materialDTO.setFornecedorNome(material.getFornecedor().getFornecedor() == null ? null : material.getFornecedor().getFornecedor());
        materialDTO.setVersion(material.getVersion());
        return materialDTO;
    }



    private Material mapToEntity(final MaterialDTO materialDTO, final Material material) {
        material.setMaterial(materialDTO.getMaterial());
        material.setAtivo(materialDTO.isAtivo());
        material.setDescricao(materialDTO.getDescricao());
        material.setCodigo(materialDTO.getCodigo());
        material.setNumeroIdentificacao(materialDTO.getNumeroIdentificacao());
        material.setControlado(materialDTO.getControlado());
        material.setPartNumber(materialDTO.getPartNumber());
        material.setCasNumber(materialDTO.getCasNumber());
        material.setFabricante(materialDTO.getFabricante());
        material.setMarca(materialDTO.getMarca());
        material.setModelo(materialDTO.getModelo());
        material.setEstoqueMin(materialDTO.getEstoqueMin());
        material.setEstoqueMax(materialDTO.getEstoqueMax());
        material.setCompraUnica(materialDTO.isCompraUnica());
        material.setObs(materialDTO.getObs());
        final MaterialTipo materialTipo = materialDTO.getMaterialTipo() == null ? null : materialTipoRepository.findById(materialDTO.getMaterialTipo())
                .orElseThrow(() -> new NotFoundException("MaterialTipo not found"));
        material.setMaterialTipo(materialTipo);
        final UnidadeMedida unidade = materialDTO.getUnidade() == null ? null : unidadeMedidaRepository.findById(materialDTO.getUnidade())
                .orElseThrow(() -> new NotFoundException("UnidadeMedida not found"));
        material.setUnidade(unidade);
        final Fornecedor fornecedor = materialDTO.getFornecedor() == null ? null : fornecedorRepository.findById(materialDTO.getFornecedor())
                .orElseThrow(() -> new NotFoundException("Fornecedor not found"));
        material.setFornecedor(fornecedor);
        return material;
    }


}
