package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Arquivos;
import br.com.lablims.spring_lablims.domain.AtaTurno;
import br.com.lablims.spring_lablims.domain.Celula;
import br.com.lablims.spring_lablims.domain.ColunaLog;
import br.com.lablims.spring_lablims.domain.Equipamento;
import br.com.lablims.spring_lablims.domain.EquipamentoLog;
import br.com.lablims.spring_lablims.domain.EquipamentoTipo;
import br.com.lablims.spring_lablims.domain.EscalaMedida;
import br.com.lablims.spring_lablims.domain.Setor;
import br.com.lablims.spring_lablims.model.EquipamentoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.ArquivosRepository;
import br.com.lablims.spring_lablims.repos.AtaTurnoRepository;
import br.com.lablims.spring_lablims.repos.CelulaRepository;
import br.com.lablims.spring_lablims.repos.ColunaLogRepository;
import br.com.lablims.spring_lablims.repos.EquipamentoLogRepository;
import br.com.lablims.spring_lablims.repos.EquipamentoRepository;
import br.com.lablims.spring_lablims.repos.EquipamentoTipoRepository;
import br.com.lablims.spring_lablims.repos.EscalaMedidaRepository;
import br.com.lablims.spring_lablims.repos.SetorRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
@Transactional
public class EquipamentoService {

    private final EquipamentoRepository equipamentoRepository;
    private final EquipamentoTipoRepository equipamentoTipoRepository;
    private final SetorRepository setorRepository;
    private final EscalaMedidaRepository escalaMedidaRepository;
    private final CelulaRepository celulaRepository;
    private final AtaTurnoRepository ataTurnoRepository;
    private final ColunaLogRepository colunaLogRepository;
    private final EquipamentoLogRepository equipamentoLogRepository;

    public Equipamento findById(Integer id) {
        return equipamentoRepository.findById(id).orElse(null);
    }

    public EquipamentoService(final EquipamentoRepository equipamentoRepository,
                              final EquipamentoTipoRepository equipamentoTipoRepository,
                              final SetorRepository setorRepository,
                              final EscalaMedidaRepository escalaMedidaRepository,
                              final CelulaRepository celulaRepository,
                              final AtaTurnoRepository ataTurnoRepository,
                              final ColunaLogRepository colunaLogRepository,
                              final EquipamentoLogRepository equipamentoLogRepository) {
        this.equipamentoRepository = equipamentoRepository;
        this.equipamentoTipoRepository = equipamentoTipoRepository;
        this.setorRepository = setorRepository;
        this.escalaMedidaRepository = escalaMedidaRepository;
        this.celulaRepository = celulaRepository;
        this.ataTurnoRepository = ataTurnoRepository;
        this.colunaLogRepository = colunaLogRepository;
        this.equipamentoLogRepository = equipamentoLogRepository;
    }

    public SimplePage<EquipamentoDTO> findAll(final String filter, final Pageable pageable) {
        Page<Equipamento> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = equipamentoRepository.findAllById(integerFilter, pageable);
        } else {
            page = equipamentoRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(equipamento -> mapToDTO(equipamento, new EquipamentoDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public EquipamentoDTO get(final Integer id) {
        return equipamentoRepository.findById(id)
                .map(equipamento -> mapToDTO(equipamento, new EquipamentoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer save(final Equipamento equipamento) {
        return equipamentoRepository.save(equipamento).getId();
    }

    public Integer create(final EquipamentoDTO equipamentoDTO) {
        final Equipamento equipamento = new Equipamento();
        mapToEntity(equipamentoDTO, equipamento);
        return equipamentoRepository.save(equipamento).getId();
    }

    public void update(final Integer id, final EquipamentoDTO equipamentoDTO) {
        final Equipamento equipamento = equipamentoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(equipamentoDTO, equipamento);
        equipamentoRepository.save(equipamento);
    }

    public void delete(final Integer id) {
        final Equipamento equipamento = equipamentoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        celulaRepository.findAllByEquipamento(equipamento)
                .forEach(celula -> celula.getEquipamento().remove(equipamento));
        ataTurnoRepository.findAllByEquipamentos(equipamento)
                .forEach(ataTurno -> ataTurno.getEquipamentos().remove(equipamento));
        equipamentoRepository.delete(equipamento);
    }

    private EquipamentoDTO mapToDTO(final Equipamento equipamento,
                                    final EquipamentoDTO equipamentoDTO) {
        equipamentoDTO.setId(equipamento.getId());
        equipamentoDTO.setDescricao(equipamento.getDescricao());
        equipamentoDTO.setTag(equipamento.getTag());
        equipamentoDTO.setFabricante(equipamento.getFabricante());
        equipamentoDTO.setMarca(equipamento.getMarca());
        equipamentoDTO.setModelo(equipamento.getModelo());
        equipamentoDTO.setUltimaCalibracao(equipamento.getUltimaCalibracao());
        equipamentoDTO.setProximaCalibracao(equipamento.getProximaCalibracao());
        equipamentoDTO.setAtivo(equipamento.getAtivo());
        equipamentoDTO.setObs(equipamento.getObs());
        equipamentoDTO.setImagem(equipamento.getImagem());
        equipamentoDTO.setCertificado(equipamento.getCertificado());
        equipamentoDTO.setManual(equipamento.getManual());
        equipamentoDTO.setProcedimento(equipamento.getProcedimento());
        equipamentoDTO.setSerialNumber(equipamento.getSerialNumber());
        equipamentoDTO.setTipo(equipamento.getTipo() == null ? null : equipamento.getTipo().getId());
        equipamentoDTO.setSetor(equipamento.getSetor() == null ? null : equipamento.getSetor().getId());
        equipamentoDTO.setEscala(equipamento.getEscala() == null ? null : equipamento.getEscala().getId());
        equipamentoDTO.setVersion(equipamento.getVersion());
        return equipamentoDTO;
    }

    private Equipamento mapToEntity(final EquipamentoDTO equipamentoDTO,
                                    final Equipamento equipamento) {
        equipamento.setDescricao(equipamentoDTO.getDescricao());
        equipamento.setTag(equipamentoDTO.getTag());
        equipamento.setFabricante(equipamentoDTO.getFabricante());
        equipamento.setMarca(equipamentoDTO.getMarca());
        equipamento.setModelo(equipamentoDTO.getModelo());
        equipamento.setUltimaCalibracao(equipamentoDTO.getUltimaCalibracao());
        equipamento.setProximaCalibracao(equipamentoDTO.getProximaCalibracao());
        equipamento.setAtivo(equipamentoDTO.getAtivo());
        equipamento.setObs(equipamentoDTO.getObs());
        equipamento.setImagem(equipamentoDTO.getImagem());
        equipamento.setCertificado(equipamentoDTO.getCertificado());
        equipamento.setManual(equipamentoDTO.getManual());
        equipamento.setProcedimento(equipamentoDTO.getProcedimento());
        equipamento.setSerialNumber(equipamentoDTO.getSerialNumber());
        final EquipamentoTipo tipo = equipamentoDTO.getTipo() == null ? null : equipamentoTipoRepository.findById(equipamentoDTO.getTipo())
                .orElseThrow(() -> new NotFoundException("tipo not found"));
        equipamento.setTipo(tipo);
        final Setor setor = equipamentoDTO.getSetor() == null ? null : setorRepository.findById(equipamentoDTO.getSetor())
                .orElseThrow(() -> new NotFoundException("setor not found"));
        equipamento.setSetor(setor);
        final EscalaMedida escala = equipamentoDTO.getEscala() == null ? null : escalaMedidaRepository.findById(equipamentoDTO.getEscala())
                .orElseThrow(() -> new NotFoundException("escala not found"));
        equipamento.setEscala(escala);
        return equipamento;
    }

    public String getReferencedWarning(final Integer id) {
        final Equipamento equipamento = equipamentoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Celula equipamentoCelula = celulaRepository.findFirstByEquipamento(equipamento);
        if (equipamentoCelula != null) {
            return WebUtils.getMessage("equipamento.celula.equipamento.referenced", equipamentoCelula.getId());
        }
        final ColunaLog equipamentoColunaLog = colunaLogRepository.findFirstByEquipamento(equipamento);
        if (equipamentoColunaLog != null) {
            return WebUtils.getMessage("equipamento.colunaLog.equipamento.referenced", equipamentoColunaLog.getId());
        }
        final AtaTurno equipamentosAtaTurno = ataTurnoRepository.findFirstByEquipamentos(equipamento);
        if (equipamentosAtaTurno != null) {
            return WebUtils.getMessage("equipamento.ataTurno.equipamentos.referenced", equipamentosAtaTurno.getId());
        }
        final EquipamentoLog equipamentoEquipamentoLog = equipamentoLogRepository.findFirstByEquipamento(equipamento);
        if (equipamentoEquipamentoLog != null) {
            return WebUtils.getMessage("equipamento.equipamentoLog.equipamento.referenced", equipamentoEquipamentoLog.getId());
        }
        return null;
    }

}
