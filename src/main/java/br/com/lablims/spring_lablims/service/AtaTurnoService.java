package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.AtaTurno;
import br.com.lablims.spring_lablims.domain.Equipamento;
import br.com.lablims.spring_lablims.domain.Setor;
import br.com.lablims.spring_lablims.domain.Turno;
import br.com.lablims.spring_lablims.domain.Usuario;
import br.com.lablims.spring_lablims.model.AtaTurnoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.AtaTurnoRepository;
import br.com.lablims.spring_lablims.repos.EquipamentoRepository;
import br.com.lablims.spring_lablims.repos.SetorRepository;
import br.com.lablims.spring_lablims.repos.TurnoRepository;
import br.com.lablims.spring_lablims.repos.UsuarioRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class AtaTurnoService {

    private final AtaTurnoRepository ataTurnoRepository;
    private final TurnoRepository turnoRepository;
    private final SetorRepository setorRepository;
    private final UsuarioRepository usuarioRepository;
    private final EquipamentoRepository equipamentoRepository;

    public AtaTurno findById(Integer id){
        return ataTurnoRepository.findById(id).orElse(null);
    }

    public AtaTurnoService(final AtaTurnoRepository ataTurnoRepository,
            final TurnoRepository turnoRepository, final SetorRepository setorRepository,
            final UsuarioRepository usuarioRepository,
            final EquipamentoRepository equipamentoRepository) {
        this.ataTurnoRepository = ataTurnoRepository;
        this.turnoRepository = turnoRepository;
        this.setorRepository = setorRepository;
        this.usuarioRepository = usuarioRepository;
        this.equipamentoRepository = equipamentoRepository;
    }

    public SimplePage<AtaTurnoDTO> findAll(final String filter, final Pageable pageable) {
        Page<AtaTurno> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = ataTurnoRepository.findAllById(integerFilter, pageable);
        } else {
            page = ataTurnoRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(ataTurno -> mapToDTO(ataTurno, new AtaTurnoDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public AtaTurnoDTO get(final Integer id) {
        return ataTurnoRepository.findById(id)
                .map(ataTurno -> mapToDTO(ataTurno, new AtaTurnoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final AtaTurnoDTO ataTurnoDTO) {
        final AtaTurno ataTurno = new AtaTurno();
        mapToEntity(ataTurnoDTO, ataTurno);
        return ataTurnoRepository.save(ataTurno).getId();
    }

    public void update(final Integer id, final AtaTurnoDTO ataTurnoDTO) {
        final AtaTurno ataTurno = ataTurnoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(ataTurnoDTO, ataTurno);
        ataTurnoRepository.save(ataTurno);
    }

    public void delete(final Integer id) {
        ataTurnoRepository.deleteById(id);
    }

    private AtaTurnoDTO mapToDTO(final AtaTurno ataTurno, final AtaTurnoDTO ataTurnoDTO) {
        ataTurnoDTO.setId(ataTurno.getId());
        ataTurnoDTO.setPassagem(ataTurno.getPassagem());
        ataTurnoDTO.setAgrupador(ataTurno.getAgrupador());
        ataTurnoDTO.setTurno(ataTurno.getTurno() == null ? null : ataTurno.getTurno().getId());
        ataTurnoDTO.setSetor(ataTurno.getSetor() == null ? null : ataTurno.getSetor().getId());
        ataTurnoDTO.setUsuario(ataTurno.getUsuario() == null ? null : ataTurno.getUsuario().getId());
        ataTurnoDTO.setVersion(ataTurno.getVersion());
        ataTurnoDTO.setEquipamentos(ataTurno.getEquipamentos().stream()
                .map(equipamento -> equipamento.getId())
                .toList());
        return ataTurnoDTO;
    }

    private AtaTurno mapToEntity(final AtaTurnoDTO ataTurnoDTO, final AtaTurno ataTurno) {
        ataTurno.setPassagem(ataTurnoDTO.getPassagem());
        ataTurno.setAgrupador(ataTurnoDTO.getAgrupador());
        final Turno turno = ataTurnoDTO.getTurno() == null ? null : turnoRepository.findById(ataTurnoDTO.getTurno())
                .orElseThrow(() -> new NotFoundException("turno not found"));
        ataTurno.setTurno(turno);
        final Setor setor = ataTurnoDTO.getSetor() == null ? null : setorRepository.findById(ataTurnoDTO.getSetor())
                .orElseThrow(() -> new NotFoundException("setor not found"));
        ataTurno.setSetor(setor);
        final Usuario usuario = ataTurnoDTO.getUsuario() == null ? null : usuarioRepository.findById(ataTurnoDTO.getUsuario())
                .orElseThrow(() -> new NotFoundException("usuario not found"));
        ataTurno.setUsuario(usuario);
        final List<Equipamento> equipamentos = equipamentoRepository.findAllById(
                ataTurnoDTO.getEquipamentos() == null ? Collections.emptyList() : ataTurnoDTO.getEquipamentos());
        if (equipamentos.size() != (ataTurnoDTO.getEquipamentos() == null ? 0 : ataTurnoDTO.getEquipamentos().size())) {
            throw new NotFoundException("one of equipamentos not found");
        }
        ataTurno.setEquipamentos(equipamentos.stream().collect(Collectors.toSet()));
        return ataTurno;
    }

}
