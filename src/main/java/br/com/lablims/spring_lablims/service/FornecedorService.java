package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Fornecedor;
import br.com.lablims.spring_lablims.model.FornecedorDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.FornecedorRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;

    public Fornecedor findById(Integer id) {
        return fornecedorRepository.findById(id).orElse(null);
    }


    public SimplePage<FornecedorDTO> findAll(final String filter, final Pageable pageable) {
        Page<Fornecedor> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = fornecedorRepository.findAllById(integerFilter, pageable);
        } else {
            page = fornecedorRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(Fornecedor -> mapToDTO(Fornecedor, new FornecedorDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public FornecedorDTO get(final Integer id) {
        return fornecedorRepository.findById(id)
                .map(fornecedor -> mapToDTO(fornecedor, new FornecedorDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final FornecedorDTO fornecedorDTO) {
        final Fornecedor fornecedor = new Fornecedor();
        mapToEntity(fornecedorDTO, fornecedor);
        return fornecedorRepository.save(fornecedor).getId();
    }

    public void update(final Integer id, final FornecedorDTO fornecedorDTO) {
        final Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(fornecedorDTO, fornecedor);
        fornecedorRepository.save(fornecedor);
    }

    public void delete(final Integer id) {
        fornecedorRepository.deleteById(id);
    }

    private FornecedorDTO mapToDTO(final Fornecedor fornecedor,
                                   final FornecedorDTO fornecedorDTO) {
        fornecedorDTO.setId(fornecedor.getId());
        fornecedorDTO.setFornecedor(fornecedor.getFornecedor());
        fornecedorDTO.setDescricao(fornecedor.getDescricao());
        fornecedorDTO.setCnpj(fornecedor.getCnpj());
        fornecedorDTO.setObs(fornecedor.getObs());
        fornecedorDTO.setVersion(fornecedor.getVersion());
        return fornecedorDTO;
    }

    private Fornecedor mapToEntity(final FornecedorDTO fornecedorDTO,
                                   final Fornecedor fornecedor) {
        fornecedor.setFornecedor(fornecedorDTO.getFornecedor());
        fornecedor.setDescricao(fornecedorDTO.getDescricao());
        fornecedor.setCnpj(fornecedorDTO.getCnpj());
        fornecedor.setObs(fornecedorDTO.getObs());

        return fornecedor;
    }

}
