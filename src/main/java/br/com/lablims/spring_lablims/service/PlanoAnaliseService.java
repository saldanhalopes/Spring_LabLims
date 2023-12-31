package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.domain.MetodologiaVersao;
import br.com.lablims.spring_lablims.model.PlanoAnaliseDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PlanoAnaliseService {

    private final PlanoAnaliseRepository planoAnaliseRepository;
    private final MetodologiaVersaoRepository metodologiaVersaoRepository;
    private final AnaliseRepository analiseRepository;
    private final AnaliseTecnicaRepository analiseTecnicaRepository;
    private final SetorRepository setorRepository;
    private final PlanoAnaliseColunaRepository planoAnaliseColunaRepository;
    private final AmostraStatusRepository amostraStatusRepository;

    public PlanoAnalise findById(Integer id){
        return planoAnaliseRepository.findById(id).orElse(null);
    }

    public SimplePage<PlanoAnaliseDTO> findAll(final String filter, final Pageable pageable) {
        Page<PlanoAnalise> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = planoAnaliseRepository.findAllById(integerFilter, pageable);
        } else {
            page = planoAnaliseRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(planoAnalise -> mapToDTO(planoAnalise, new PlanoAnaliseDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public PlanoAnaliseDTO get(final Integer id) {
        return planoAnaliseRepository.findById(id)
                .map(planoAnalise -> mapToDTO(planoAnalise, new PlanoAnaliseDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final PlanoAnaliseDTO planoAnaliseDTO) {
        final PlanoAnalise planoAnalise = new PlanoAnalise();
        mapToEntity(planoAnaliseDTO, planoAnalise);
        return planoAnaliseRepository.save(planoAnalise).getId();
    }

    public void update(final Integer id, final PlanoAnaliseDTO planoAnaliseDTO) {
        final PlanoAnalise planoAnalise = planoAnaliseRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(planoAnaliseDTO, planoAnalise);
        planoAnaliseRepository.save(planoAnalise);
    }

    public void delete(final Integer id) {
        planoAnaliseRepository.deleteById(id);
    }

    private PlanoAnaliseDTO mapToDTO(final PlanoAnalise planoAnalise,
            final PlanoAnaliseDTO planoAnaliseDTO) {
        planoAnaliseDTO.setId(planoAnalise.getId());
        planoAnaliseDTO.setDescricao(planoAnalise.getDescricao());
        planoAnaliseDTO.setLeadTimeSetup(planoAnalise.getLeadTimeSetup());
        planoAnaliseDTO.setLeadTimeAnalise(planoAnalise.getLeadTimeAnalise());
        planoAnaliseDTO.setLeadTimeLimpeza(planoAnalise.getLeadTimeLimpeza());
        planoAnaliseDTO.setMetodologiaVersao(planoAnalise.getMetodologiaVersao() == null ? null : planoAnalise.getMetodologiaVersao().getId());
        planoAnaliseDTO.setAnalise(planoAnalise.getAnalise() == null ? null : planoAnalise.getAnalise().getId());
        planoAnaliseDTO.setAnaliseNome(planoAnalise.getAnalise() == null ? null : planoAnalise.getAnalise().getAnalise());
        planoAnaliseDTO.setAnaliseTipo(planoAnalise.getAnalise() == null ? null : planoAnalise.getAnalise().getAnaliseTipo().getAnaliseTipo());
        planoAnaliseDTO.setAnaliseTecnica(planoAnalise.getAnaliseTecnica() == null ? null : planoAnalise.getAnaliseTecnica().getId());
        planoAnaliseDTO.setAnaliseTecnicaNome(planoAnalise.getAnaliseTecnica() == null ? null : planoAnalise.getAnaliseTecnica().getAnaliseTecnica());
        planoAnaliseDTO.setSetor(planoAnalise.getSetor() == null ? null : planoAnalise.getSetor().getId());
        planoAnaliseDTO.setVersion(planoAnalise.getVersion());
        return planoAnaliseDTO;
    }

    private PlanoAnalise mapToEntity(final PlanoAnaliseDTO planoAnaliseDTO,
            final PlanoAnalise planoAnalise) {
        planoAnalise.setDescricao(planoAnaliseDTO.getDescricao());
        planoAnalise.setLeadTimeSetup(planoAnaliseDTO.getLeadTimeSetup());
        planoAnalise.setLeadTimeAnalise(planoAnaliseDTO.getLeadTimeAnalise());
        planoAnalise.setLeadTimeLimpeza(planoAnaliseDTO.getLeadTimeLimpeza());
        final MetodologiaVersao metodologiaVersao = planoAnaliseDTO.getMetodologiaVersao() == null ? null : metodologiaVersaoRepository.findById(planoAnaliseDTO.getMetodologiaVersao())
                .orElseThrow(() -> new NotFoundException("metodologiaVersao not found"));
        planoAnalise.setMetodologiaVersao(metodologiaVersao);
        final Analise analise = planoAnaliseDTO.getAnalise() == null ? null : analiseRepository.findById(planoAnaliseDTO.getAnalise())
                .orElseThrow(() -> new NotFoundException("analise not found"));
        planoAnalise.setAnalise(analise);
        final AnaliseTecnica analiseTecnica = planoAnaliseDTO.getAnaliseTecnica() == null ? null : analiseTecnicaRepository.findById(planoAnaliseDTO.getAnaliseTecnica())
                .orElseThrow(() -> new NotFoundException("analiseTipo not found"));
        planoAnalise.setAnaliseTecnica(analiseTecnica);
        final Setor setor = planoAnaliseDTO.getSetor() == null ? null : setorRepository.findById(planoAnaliseDTO.getSetor())
                .orElseThrow(() -> new NotFoundException("setor not found"));
        planoAnalise.setSetor(setor);
        return planoAnalise;
    }

    public String getReferencedWarning(final Integer id) {
        final PlanoAnalise planoAnalise = planoAnaliseRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final PlanoAnaliseColuna planoAnalisePlanoAnaliseColuna = planoAnaliseColunaRepository.findFirstByPlanoAnalise(planoAnalise);
        if (planoAnalisePlanoAnaliseColuna != null) {
            return WebUtils.getMessage("entity.referenced", planoAnalisePlanoAnaliseColuna.getId());
        }
        final AmostraStatus planoAnaliseAmostraStatus = amostraStatusRepository.findFirstByPlanoAnalise(planoAnalise);
        if (planoAnaliseAmostraStatus != null) {
            return WebUtils.getMessage("entity.referenced", planoAnaliseAmostraStatus.getId());
        }
        return null;
    }

}
