package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.AnaliseProdutividade;
import br.com.lablims.spring_lablims.domain.AnaliseStatus;
import br.com.lablims.spring_lablims.model.AnaliseProdutividadeDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.AnaliseProdutividadeRepository;
import br.com.lablims.spring_lablims.repos.AnaliseStatusRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AnaliseProdutividadeService {

    private final AnaliseProdutividadeRepository analiseProdutividadeRepository;
    private final AnaliseStatusRepository analiseStatusRepository;

    public AnaliseProdutividade findById(Integer id){
        return analiseProdutividadeRepository.findById(id).orElse(null);
    }

    public SimplePage<AnaliseProdutividadeDTO> findAll(final String filter,
            final Pageable pageable) {
        Page<AnaliseProdutividade> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = analiseProdutividadeRepository.findAllById(integerFilter, pageable);
        } else {
            page = analiseProdutividadeRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(analiseProdutividade -> mapToDTO(analiseProdutividade, new AnaliseProdutividadeDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public AnaliseProdutividadeDTO get(final Integer id) {
        return analiseProdutividadeRepository.findById(id)
                .map(analiseProdutividade -> mapToDTO(analiseProdutividade, new AnaliseProdutividadeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final AnaliseProdutividadeDTO analiseProdutividadeDTO) {
        final AnaliseProdutividade analiseProdutividade = new AnaliseProdutividade();
        mapToEntity(analiseProdutividadeDTO, analiseProdutividade);
        return analiseProdutividadeRepository.save(analiseProdutividade).getId();
    }

    public void update(final Integer id, final AnaliseProdutividadeDTO analiseProdutividadeDTO) {
        final AnaliseProdutividade analiseProdutividade = analiseProdutividadeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(analiseProdutividadeDTO, analiseProdutividade);
        analiseProdutividadeRepository.save(analiseProdutividade);
    }

    public void delete(final Integer id) {
        analiseProdutividadeRepository.deleteById(id);
    }

    private AnaliseProdutividadeDTO mapToDTO(final AnaliseProdutividade analiseProdutividade,
            final AnaliseProdutividadeDTO analiseProdutividadeDTO) {
        analiseProdutividadeDTO.setId(analiseProdutividade.getId());
        analiseProdutividadeDTO.setAnaliseProdutividade(analiseProdutividade.getAnaliseProdutividade());
        analiseProdutividadeDTO.setSiglaProdutividade(analiseProdutividade.getSiglaProdutividade());
        analiseProdutividadeDTO.setDescricaoProdutividade(analiseProdutividade.getDescricaoProdutividade());
        analiseProdutividadeDTO.setCorProdutividade(analiseProdutividade.getCorProdutividade());
        analiseProdutividadeDTO.setVersion(analiseProdutividade.getVersion());
        return analiseProdutividadeDTO;
    }

    private AnaliseProdutividade mapToEntity(final AnaliseProdutividadeDTO analiseProdutividadeDTO,
            final AnaliseProdutividade analiseProdutividade) {
        analiseProdutividade.setAnaliseProdutividade(analiseProdutividadeDTO.getAnaliseProdutividade());
        analiseProdutividade.setSiglaProdutividade(analiseProdutividadeDTO.getSiglaProdutividade());
        analiseProdutividade.setDescricaoProdutividade(analiseProdutividadeDTO.getDescricaoProdutividade());
        analiseProdutividade.setCorProdutividade(analiseProdutividadeDTO.getCorProdutividade());
        return analiseProdutividade;
    }

    public String getReferencedWarning(final Integer id) {
        final AnaliseProdutividade analiseProdutividade = analiseProdutividadeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final AnaliseStatus analiseProdutividadeAnaliseStatus = analiseStatusRepository.findFirstByAnaliseProdutividade(analiseProdutividade);
        if (analiseProdutividadeAnaliseStatus != null) {
            return WebUtils.getMessage("analiseProdutividade.analiseStatus.analiseProdutividade.referenced", analiseProdutividadeAnaliseStatus.getId());
        }
        return null;
    }

}
