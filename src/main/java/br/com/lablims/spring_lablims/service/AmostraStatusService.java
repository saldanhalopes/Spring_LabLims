package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.domain.AmostraStatus;
import br.com.lablims.spring_lablims.model.AmostraStatusDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.AnaliseStatusRepository;
import br.com.lablims.spring_lablims.repos.AmostraRepository;
import br.com.lablims.spring_lablims.repos.AmostraStatusRepository;
import br.com.lablims.spring_lablims.repos.PlanoAnaliseRepository;
import br.com.lablims.spring_lablims.repos.UsuarioRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class AmostraStatusService {

    private final AmostraStatusRepository amostraStatusRepository;
    private final AmostraRepository amostraRepository;
    private final PlanoAnaliseRepository planoAnaliseRepository;
    private final AnaliseStatusRepository analiseStatusRepository;
    private final UsuarioRepository usuarioRepository;

    public AmostraStatus findById(Integer id){
        return amostraStatusRepository.findById(id).orElse(null);
    }

    public AmostraStatusService(final AmostraStatusRepository amostraStatusRepository,
                                final AmostraRepository amostraRepository,
                                final PlanoAnaliseRepository planoAnaliseRepository,
                                final AnaliseStatusRepository analiseStatusRepository,
                                final UsuarioRepository usuarioRepository) {
        this.amostraStatusRepository = amostraStatusRepository;
        this.amostraRepository = amostraRepository;
        this.planoAnaliseRepository = planoAnaliseRepository;
        this.analiseStatusRepository = analiseStatusRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public SimplePage<AmostraStatusDTO> findAll(final String filter, final Pageable pageable) {
        Page<AmostraStatus> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = amostraStatusRepository.findAllById(integerFilter, pageable);
        } else {
            page = amostraStatusRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(amostraStatus -> mapToDTO(amostraStatus, new AmostraStatusDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public AmostraStatusDTO get(final Integer id) {
        return amostraStatusRepository.findById(id)
                .map(amostraStatus -> mapToDTO(amostraStatus, new AmostraStatusDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final AmostraStatusDTO amostraStatusDTO) {
        final AmostraStatus amostraStatus = new AmostraStatus();
        mapToEntity(amostraStatusDTO, amostraStatus);
        return amostraStatusRepository.save(amostraStatus).getId();
    }

    public void update(final Integer id, final AmostraStatusDTO amostraStatusDTO) {
        final AmostraStatus amostraStatus = amostraStatusRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(amostraStatusDTO, amostraStatus);
        amostraStatusRepository.save(amostraStatus);
    }

    public void delete(final Integer id) {
        amostraStatusRepository.deleteById(id);
    }

    private AmostraStatusDTO mapToDTO(final AmostraStatus amostraStatus, final AmostraStatusDTO amostraStatusDTO) {
        amostraStatusDTO.setId(amostraStatus.getId());
        amostraStatusDTO.setDataStatus(amostraStatus.getDataStatus());
        amostraStatusDTO.setDataInicioAnalise(amostraStatus.getDataInicioAnalise());
        amostraStatusDTO.setDataFimAnalise(amostraStatus.getDataFimAnalise());
        amostraStatusDTO.setDataPrevisaoLiberacao(amostraStatus.getDataPrevisaoLiberacao());
        amostraStatusDTO.setDataProgramado(amostraStatus.getDataProgramado());
        amostraStatusDTO.setDataConferencia1(amostraStatus.getDataConferencia1());
        amostraStatusDTO.setDataConferencia2(amostraStatus.getDataConferencia2());
        amostraStatusDTO.setObs(amostraStatus.getObs());
        amostraStatusDTO.setAmostra(amostraStatus.getAmostra() == null ? null : amostraStatus.getAmostra().getId());
        amostraStatusDTO.setPlanoAnalise(amostraStatus.getPlanoAnalise() == null ? null : amostraStatus.getPlanoAnalise().getId());
        amostraStatusDTO.setAnaliseStatus(amostraStatus.getAnaliseStatus() == null ? null : amostraStatus.getAnaliseStatus().getId());
        amostraStatusDTO.setConferente1(amostraStatus.getConferente1() == null ? null : amostraStatus.getConferente1().getId());
        amostraStatusDTO.setConferente2(amostraStatus.getConferente2() == null ? null : amostraStatus.getConferente2().getId());
        amostraStatusDTO.setVersion(amostraStatus.getVersion());
        return amostraStatusDTO;
    }

    private AmostraStatus mapToEntity(final AmostraStatusDTO amostraStatusDTO, final AmostraStatus amostraStatus) {
        amostraStatus.setDataStatus(amostraStatusDTO.getDataStatus());
        amostraStatus.setDataInicioAnalise(amostraStatusDTO.getDataInicioAnalise());
        amostraStatus.setDataFimAnalise(amostraStatusDTO.getDataFimAnalise());
        amostraStatus.setDataPrevisaoLiberacao(amostraStatusDTO.getDataPrevisaoLiberacao());
        amostraStatus.setDataProgramado(amostraStatusDTO.getDataProgramado());
        amostraStatus.setDataConferencia1(amostraStatusDTO.getDataConferencia1());
        amostraStatus.setDataConferencia2(amostraStatusDTO.getDataConferencia2());
        amostraStatus.setObs(amostraStatusDTO.getObs());
        final Amostra amostra = amostraStatusDTO.getAmostra() == null ? null : amostraRepository.findById(amostraStatusDTO.getAmostra())
                .orElseThrow(() -> new NotFoundException("amostra not found"));
        amostraStatus.setAmostra(amostra);
        final PlanoAnalise planoAnalise = amostraStatusDTO.getPlanoAnalise() == null ? null : planoAnaliseRepository.findById(amostraStatusDTO.getPlanoAnalise())
                .orElseThrow(() -> new NotFoundException("planoAnalise not found"));
        amostraStatus.setPlanoAnalise(planoAnalise);
        final AnaliseStatus analiseStatus = amostraStatusDTO.getAnaliseStatus() == null ? null : analiseStatusRepository.findById(amostraStatusDTO.getAnaliseStatus())
                .orElseThrow(() -> new NotFoundException("analiseStatus not found"));
        amostraStatus.setAnaliseStatus(analiseStatus);
        final Usuario conferente1 = amostraStatusDTO.getConferente1() == null ? null : usuarioRepository.findById(amostraStatusDTO.getConferente1())
                .orElseThrow(() -> new NotFoundException("conferente1 not found"));
        amostraStatus.setConferente1(conferente1);
        final Usuario conferente2 = amostraStatusDTO.getConferente2() == null ? null : usuarioRepository.findById(amostraStatusDTO.getConferente2())
                .orElseThrow(() -> new NotFoundException("conferente2 not found"));
        amostraStatus.setConferente2(conferente2);
        return amostraStatus;
    }

}
