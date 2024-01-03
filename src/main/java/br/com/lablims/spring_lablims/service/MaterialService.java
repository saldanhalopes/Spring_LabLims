package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.AmostraTipoDTO;
import br.com.lablims.spring_lablims.model.MaterialDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.util.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final CategoriaRepository categoriaRepository;
    private final UnidadeMedidaRepository unidadeMedidaRepository;
    private final FornecedorRepository fornecedorRepository;
    private final FabricanteRepository fabricanteRepository;

    public Material findById(Integer id) {
        return materialRepository.findById(id).orElse(null);
    }

    public SimplePage<MaterialDTO> findAll(final String filter, final Pageable pageable) {
        Page<Material> page;
        if (filter != null) {
            page = materialRepository.findAllByKeyword(filter, pageable);
        } else {
            page = materialRepository.findAllOfMateriais(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(material -> mapToDTO(material, new MaterialDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public MaterialDTO get(final Integer id) {
        return materialRepository.findMaterialById(id)
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
        materialDTO.setMarca(material.getMarca());
        materialDTO.setModelo(material.getModelo());
        materialDTO.setEstoqueMin(material.getEstoqueMin());
        materialDTO.setEstoqueMax(material.getEstoqueMax());
        materialDTO.setCompraUnica(material.isCompraUnica());
        materialDTO.setObs(material.getObs());
        materialDTO.setCategoria(material.getCategoria() == null ? null : material.getCategoria().getId());
        materialDTO.setUnidade(material.getUnidade() == null ? null : material.getUnidade().getId());
        materialDTO.setFornecedor(material.getFornecedor() == null ? null : material.getFornecedor().getId());
        materialDTO.setFabricante(material.getFabricante() == null ? null : material.getFabricante().getId());
//        try {
            materialDTO.setCategoriaNome(material.getCategoria() == null ? null : material.getCategoria().getCategoria());
            materialDTO.setUnidadeNome(material.getUnidade() == null ? null : material.getUnidade().getUnidade());
            materialDTO.setFornecedorNome(material.getFornecedor() == null ? null : material.getFornecedor().getFornecedor());
            materialDTO.setFabricanteNome(material.getFabricante() == null ? null : material.getFabricante().getFabricante());
//        }catch (Exception e){
//
//        }
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
        material.setMarca(materialDTO.getMarca());
        material.setModelo(materialDTO.getModelo());
        material.setEstoqueMin(materialDTO.getEstoqueMin());
        material.setEstoqueMax(materialDTO.getEstoqueMax());
        material.setCompraUnica(materialDTO.isCompraUnica());
        material.setObs(materialDTO.getObs());
        final Categoria categoria = materialDTO.getCategoria() == null ? null : categoriaRepository.findById(materialDTO.getCategoria())
                .orElseThrow(() -> new NotFoundException("Categoria not found"));
        material.setCategoria(categoria);
        final UnidadeMedida unidade = materialDTO.getUnidade() == null ? null : unidadeMedidaRepository.findById(materialDTO.getUnidade())
                .orElseThrow(() -> new NotFoundException("UnidadeMedida not found"));
        material.setUnidade(unidade);
        final Fornecedor fornecedor = materialDTO.getFornecedor() == null ? null : fornecedorRepository.findById(materialDTO.getFornecedor())
                .orElseThrow(() -> new NotFoundException("Fornecedor not found"));
        material.setFornecedor(fornecedor);
        final Fabricante fabricante = materialDTO.getFabricante() == null ? null : fabricanteRepository.findById(materialDTO.getFabricante())
                .orElseThrow(() -> new NotFoundException("Fabricante not found"));
        material.setFabricante(fabricante);
        return material;
    }


}
