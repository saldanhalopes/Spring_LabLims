package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.SolucaoParemetro;
import br.com.lablims.spring_lablims.domain.SolucaoRegistro;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.model.SolucaoParemetroDTO;
import br.com.lablims.spring_lablims.repos.SolucaoParemetroRepository;
import br.com.lablims.spring_lablims.repos.SolucaoRegistroRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SolucaoParemetroService {

    private final SolucaoParemetroRepository solucaoParemetroRepository;
    private final SolucaoRegistroRepository solucaoRegistroRepository;

    public SolucaoParemetro findById(Integer id){
        return solucaoParemetroRepository.findById(id).orElse(null);
    }

    public SimplePage<SolucaoParemetroDTO> findAll(final String filter, final Pageable pageable) {
        Page<SolucaoParemetro> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = solucaoParemetroRepository.findAllById(integerFilter, pageable);
        } else {
            page = solucaoParemetroRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(solucaoParemetro -> mapToDTO(solucaoParemetro, new SolucaoParemetroDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public SolucaoParemetroDTO get(final Integer id) {
        return solucaoParemetroRepository.findById(id)
                .map(solucaoParemetro -> mapToDTO(solucaoParemetro, new SolucaoParemetroDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final SolucaoParemetroDTO solucaoParemetroDTO) {
        final SolucaoParemetro solucaoParemetro = new SolucaoParemetro();
        mapToEntity(solucaoParemetroDTO, solucaoParemetro);
        return solucaoParemetroRepository.save(solucaoParemetro).getId();
    }

    public void update(final Integer id, final SolucaoParemetroDTO solucaoParemetroDTO) {
        final SolucaoParemetro solucaoParemetro = solucaoParemetroRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(solucaoParemetroDTO, solucaoParemetro);
        solucaoParemetroRepository.save(solucaoParemetro);
    }

    public void delete(final Integer id) {
        solucaoParemetroRepository.deleteById(id);
    }

    private SolucaoParemetroDTO mapToDTO(final SolucaoParemetro solucaoParemetro,
            final SolucaoParemetroDTO solucaoParemetroDTO) {
        solucaoParemetroDTO.setId(solucaoParemetro.getId());
        solucaoParemetroDTO.setParemetro(solucaoParemetro.getParemetro());
        solucaoParemetroDTO.setValor(solucaoParemetro.getValor());
        solucaoParemetroDTO.setSolucaoRegistro(solucaoParemetro.getSolucaoRegistro() == null ? null : solucaoParemetro.getSolucaoRegistro().getId());
        solucaoParemetroDTO.setVersion(solucaoParemetro.getVersion());
        return solucaoParemetroDTO;
    }

    private SolucaoParemetro mapToEntity(final SolucaoParemetroDTO solucaoParemetroDTO,
            final SolucaoParemetro solucaoParemetro) {
        solucaoParemetro.setParemetro(solucaoParemetroDTO.getParemetro());
        solucaoParemetro.setValor(solucaoParemetroDTO.getValor());
        final SolucaoRegistro solucaoRegistro = solucaoParemetroDTO.getSolucaoRegistro() == null ? null : solucaoRegistroRepository.findById(solucaoParemetroDTO.getSolucaoRegistro())
                .orElseThrow(() -> new NotFoundException("solucaoRegistro not found"));
        solucaoParemetro.setSolucaoRegistro(solucaoRegistro);
        return solucaoParemetro;
    }

}
