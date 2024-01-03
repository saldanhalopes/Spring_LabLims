package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.AtaTurno;
import br.com.lablims.spring_lablims.domain.Turno;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.model.TurnoDTO;
import br.com.lablims.spring_lablims.repos.AtaTurnoRepository;
import br.com.lablims.spring_lablims.repos.TurnoRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final AtaTurnoRepository ataTurnoRepository;

    public Turno findById(Integer id){
        return turnoRepository.findById(id).orElse(null);
    }

    public SimplePage<TurnoDTO> findAll(final String filter, final Pageable pageable) {
        Page<Turno> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = turnoRepository.findAllById(integerFilter, pageable);
        } else {
            page = turnoRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(turno -> mapToDTO(turno, new TurnoDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public TurnoDTO get(final Integer id) {
        return turnoRepository.findById(id)
                .map(turno -> mapToDTO(turno, new TurnoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final TurnoDTO turnoDTO) {
        final Turno turno = new Turno();
        mapToEntity(turnoDTO, turno);
        return turnoRepository.save(turno).getId();
    }

    public void update(final Integer id, final TurnoDTO turnoDTO) {
        final Turno turno = turnoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(turnoDTO, turno);
        turnoRepository.save(turno);
    }

    public void delete(final Integer id) {
        turnoRepository.deleteById(id);
    }

    private TurnoDTO mapToDTO(final Turno turno, final TurnoDTO turnoDTO) {
        turnoDTO.setId(turno.getId());
        turnoDTO.setTurno(turno.getTurno());
        turnoDTO.setVersion(turno.getVersion());
        return turnoDTO;
    }

    private Turno mapToEntity(final TurnoDTO turnoDTO, final Turno turno) {
        turno.setTurno(turnoDTO.getTurno());
        return turno;
    }

    public String getReferencedWarning(final Integer id) {
        final Turno turno = turnoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final AtaTurno turnoAtaTurno = ataTurnoRepository.findFirstByTurno(turno);
        if (turnoAtaTurno != null) {
            return WebUtils.getMessage("entity.referenced", turnoAtaTurno.getId());
        }
        return null;
    }

}
