package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.AnaliseStatus;
import br.com.lablims.spring_lablims.domain.Lote;
import br.com.lablims.spring_lablims.domain.LoteStatus;
import br.com.lablims.spring_lablims.domain.PlanoAnalise;
import br.com.lablims.spring_lablims.domain.Usuario;
import br.com.lablims.spring_lablims.model.LoteStatusDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.AnaliseStatusRepository;
import br.com.lablims.spring_lablims.repos.LoteRepository;
import br.com.lablims.spring_lablims.repos.LoteStatusRepository;
import br.com.lablims.spring_lablims.repos.PlanoAnaliseRepository;
import br.com.lablims.spring_lablims.repos.UsuarioRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class LoteStatusService {

    private final LoteStatusRepository loteStatusRepository;
    private final LoteRepository loteRepository;
    private final PlanoAnaliseRepository planoAnaliseRepository;
    private final AnaliseStatusRepository analiseStatusRepository;
    private final UsuarioRepository usuarioRepository;

    public LoteStatus findById(Integer id){
        return loteStatusRepository.findById(id).orElse(null);
    }

    public LoteStatusService(final LoteStatusRepository loteStatusRepository,
            final LoteRepository loteRepository,
            final PlanoAnaliseRepository planoAnaliseRepository,
            final AnaliseStatusRepository analiseStatusRepository,
            final UsuarioRepository usuarioRepository) {
        this.loteStatusRepository = loteStatusRepository;
        this.loteRepository = loteRepository;
        this.planoAnaliseRepository = planoAnaliseRepository;
        this.analiseStatusRepository = analiseStatusRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public SimplePage<LoteStatusDTO> findAll(final String filter, final Pageable pageable) {
        Page<LoteStatus> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = loteStatusRepository.findAllById(integerFilter, pageable);
        } else {
            page = loteStatusRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(loteStatus -> mapToDTO(loteStatus, new LoteStatusDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public LoteStatusDTO get(final Integer id) {
        return loteStatusRepository.findById(id)
                .map(loteStatus -> mapToDTO(loteStatus, new LoteStatusDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final LoteStatusDTO loteStatusDTO) {
        final LoteStatus loteStatus = new LoteStatus();
        mapToEntity(loteStatusDTO, loteStatus);
        return loteStatusRepository.save(loteStatus).getId();
    }

    public void update(final Integer id, final LoteStatusDTO loteStatusDTO) {
        final LoteStatus loteStatus = loteStatusRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(loteStatusDTO, loteStatus);
        loteStatusRepository.save(loteStatus);
    }

    public void delete(final Integer id) {
        loteStatusRepository.deleteById(id);
    }

    private LoteStatusDTO mapToDTO(final LoteStatus loteStatus, final LoteStatusDTO loteStatusDTO) {
        loteStatusDTO.setId(loteStatus.getId());
        loteStatusDTO.setDataStatus(loteStatus.getDataStatus());
        loteStatusDTO.setDataPrevisao(loteStatus.getDataPrevisao());
        loteStatusDTO.setDataProgramado(loteStatus.getDataProgramado());
        loteStatusDTO.setDataConferencia1(loteStatus.getDataConferencia1());
        loteStatusDTO.setDataConferencia2(loteStatus.getDataConferencia2());
        loteStatusDTO.setObs(loteStatus.getObs());
        loteStatusDTO.setLote(loteStatus.getLote() == null ? null : loteStatus.getLote().getId());
        loteStatusDTO.setPlanoAnalise(loteStatus.getPlanoAnalise() == null ? null : loteStatus.getPlanoAnalise().getId());
        loteStatusDTO.setAnaliseStatus(loteStatus.getAnaliseStatus() == null ? null : loteStatus.getAnaliseStatus().getId());
        loteStatusDTO.setConferente1(loteStatus.getConferente1() == null ? null : loteStatus.getConferente1().getId());
        loteStatusDTO.setConferente2(loteStatus.getConferente2() == null ? null : loteStatus.getConferente2().getId());
        loteStatusDTO.setVersion(loteStatus.getVersion());
        return loteStatusDTO;
    }

    private LoteStatus mapToEntity(final LoteStatusDTO loteStatusDTO, final LoteStatus loteStatus) {
        loteStatus.setDataStatus(loteStatusDTO.getDataStatus());
        loteStatus.setDataPrevisao(loteStatusDTO.getDataPrevisao());
        loteStatus.setDataProgramado(loteStatusDTO.getDataProgramado());
        loteStatus.setDataConferencia1(loteStatusDTO.getDataConferencia1());
        loteStatus.setDataConferencia2(loteStatusDTO.getDataConferencia2());
        loteStatus.setObs(loteStatusDTO.getObs());
        final Lote lote = loteStatusDTO.getLote() == null ? null : loteRepository.findById(loteStatusDTO.getLote())
                .orElseThrow(() -> new NotFoundException("lote not found"));
        loteStatus.setLote(lote);
        final PlanoAnalise planoAnalise = loteStatusDTO.getPlanoAnalise() == null ? null : planoAnaliseRepository.findById(loteStatusDTO.getPlanoAnalise())
                .orElseThrow(() -> new NotFoundException("planoAnalise not found"));
        loteStatus.setPlanoAnalise(planoAnalise);
        final AnaliseStatus analiseStatus = loteStatusDTO.getAnaliseStatus() == null ? null : analiseStatusRepository.findById(loteStatusDTO.getAnaliseStatus())
                .orElseThrow(() -> new NotFoundException("analiseStatus not found"));
        loteStatus.setAnaliseStatus(analiseStatus);
        final Usuario conferente1 = loteStatusDTO.getConferente1() == null ? null : usuarioRepository.findById(loteStatusDTO.getConferente1())
                .orElseThrow(() -> new NotFoundException("conferente1 not found"));
        loteStatus.setConferente1(conferente1);
        final Usuario conferente2 = loteStatusDTO.getConferente2() == null ? null : usuarioRepository.findById(loteStatusDTO.getConferente2())
                .orElseThrow(() -> new NotFoundException("conferente2 not found"));
        loteStatus.setConferente2(conferente2);
        return loteStatus;
    }

}
