package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.CampanhaDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.CampanhaRepository;
import br.com.lablims.spring_lablims.repos.CelulaRepository;
import br.com.lablims.spring_lablims.repos.ColunaLogRepository;
import br.com.lablims.spring_lablims.repos.AmostraRepository;
import br.com.lablims.spring_lablims.repos.SetorRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CampanhaService {

    private final CampanhaRepository campanhaRepository;
    private final SetorRepository setorRepository;
    private final CelulaRepository celulaRepository;
    private final AmostraRepository amostraRepository;
    private final ColunaLogRepository colunaLogRepository;

    public Campanha findById(Integer id){
        return campanhaRepository.findById(id).orElse(null);
    }

    public SimplePage<CampanhaDTO> findAll(final String filter, final Pageable pageable) {
        Page<Campanha> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = campanhaRepository.findAllById(integerFilter, pageable);
        } else {
            page = campanhaRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(campanha -> mapToDTO(campanha, new CampanhaDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public CampanhaDTO get(final Integer id) {
        return campanhaRepository.findById(id)
                .map(campanha -> mapToDTO(campanha, new CampanhaDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final CampanhaDTO campanhaDTO) {
        final Campanha campanha = new Campanha();
        mapToEntity(campanhaDTO, campanha);
        return campanhaRepository.save(campanha).getId();
    }

    public void update(final Integer id, final CampanhaDTO campanhaDTO) {
        final Campanha campanha = campanhaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(campanhaDTO, campanha);
        campanhaRepository.save(campanha);
    }

    public void delete(final Integer id) {
        campanhaRepository.deleteById(id);
    }

    private CampanhaDTO mapToDTO(final Campanha campanha, final CampanhaDTO campanhaDTO) {
        campanhaDTO.setId(campanha.getId());
        campanhaDTO.setPrevisaoInicio(campanha.getPrevisaoInicio());
        campanhaDTO.setPrevisaoFim(campanha.getPrevisaoFim());
        campanhaDTO.setDataInicio(campanha.getDataInicio());
        campanhaDTO.setDataFim(campanha.getDataFim());
        campanhaDTO.setStatus(campanha.getStatus());
        campanhaDTO.setObs(campanha.getObs());
        campanhaDTO.setOrdem(campanha.getOrdem());
        campanhaDTO.setPrioridade(campanha.getPrioridade());
        campanhaDTO.setSetor(campanha.getSetor() == null ? null : campanha.getSetor().getId());
        campanhaDTO.setCelula(campanha.getCelula() == null ? null : campanha.getCelula().getId());
        campanhaDTO.setVersion(campanha.getVersion());
        campanhaDTO.setAmostras(campanha.getAmostras().stream()
                .map(amostra -> amostra.getId())
                .toList());
        return campanhaDTO;
    }

    private Campanha mapToEntity(final CampanhaDTO campanhaDTO, final Campanha campanha) {
        campanha.setPrevisaoInicio(campanhaDTO.getPrevisaoInicio());
        campanha.setPrevisaoFim(campanhaDTO.getPrevisaoFim());
        campanha.setDataInicio(campanhaDTO.getDataInicio());
        campanha.setDataFim(campanhaDTO.getDataFim());
        campanha.setStatus(campanhaDTO.getStatus());
        campanha.setObs(campanhaDTO.getObs());
        campanha.setOrdem(campanhaDTO.getOrdem());
        campanha.setPrioridade(campanhaDTO.getPrioridade());
        final Setor setor = campanhaDTO.getSetor() == null ? null : setorRepository.findById(campanhaDTO.getSetor())
                .orElseThrow(() -> new NotFoundException("setor not found"));
        campanha.setSetor(setor);
        final Celula celula = campanhaDTO.getCelula() == null ? null : celulaRepository.findById(campanhaDTO.getCelula())
                .orElseThrow(() -> new NotFoundException("celula not found"));
        campanha.setCelula(celula);
        final List<Amostra> amostras = amostraRepository.findAllById(
                campanhaDTO.getAmostras() == null ? Collections.emptyList() : campanhaDTO.getAmostras());
        if (amostras.size() != (campanhaDTO.getAmostras() == null ? 0 : campanhaDTO.getAmostras().size())) {
            throw new NotFoundException("one of amostras not found");
        }
        campanha.setAmostras(amostras.stream().collect(Collectors.toSet()));
        return campanha;
    }

    public String getReferencedWarning(final Integer id) {
        final Campanha campanha = campanhaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final ColunaLog campanhaColunaLog = colunaLogRepository.findFirstByCampanha(campanha);
        if (campanhaColunaLog != null) {
            return WebUtils.getMessage("entity.referenced", campanhaColunaLog.getId());
        }
        return null;
    }

}
