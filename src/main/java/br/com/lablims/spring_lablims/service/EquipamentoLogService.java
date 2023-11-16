package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.EquipamentoLogDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.util.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class EquipamentoLogService {

    private final EquipamentoLogRepository equipamentoLogRepository;
    private final EquipamentoAtividadeRepository equipamentoAtividadeRepository;
    private final EquipamentoRepository equipamentoRepository;
    private final UsuarioRepository usuarioRepository;

    private final AmostraService amostraService;
    private final AmostraRepository amostraRepository;
    private final ArquivosRepository arquivosRepository;

    public EquipamentoLog findById(Integer id) {
        return equipamentoLogRepository.findById(id).orElse(null);
    }

    public EquipamentoLogService(final EquipamentoLogRepository equipamentoLogRepository,
                                 final EquipamentoAtividadeRepository equipamentoAtividadeRepository,
                                 final EquipamentoRepository equipamentoRepository,
                                 final UsuarioRepository usuarioRepository,
                                 AmostraService amostraService, AmostraRepository amostraRepository, final ArquivosRepository arquivosRepository) {
        this.equipamentoLogRepository = equipamentoLogRepository;
        this.equipamentoAtividadeRepository = equipamentoAtividadeRepository;
        this.equipamentoRepository = equipamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.amostraService = amostraService;
        this.amostraRepository = amostraRepository;
        this.arquivosRepository = arquivosRepository;
    }

    public SimplePage<EquipamentoLogDTO> findAll(final String filter, final Pageable pageable) {
        Page<EquipamentoLog> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = equipamentoLogRepository.findAllById(integerFilter, pageable);
        } else {
            page = equipamentoLogRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(equipamentoLog -> mapToDTO(equipamentoLog, new EquipamentoLogDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public SimplePage<EquipamentoLogDTO> findAllByEquipamento(Integer equip_Id, final Pageable pageable) {
        Page<EquipamentoLog> page = equipamentoLogRepository.findAllByEquipamento_Id(equip_Id, pageable);
        return new SimplePage<>(page.getContent()
                .stream()
                .map(equipamentoLog -> mapToDTOByEquipamentoLog(equipamentoLog, new EquipamentoLogDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public EquipamentoLogDTO get(final Integer id) {
        return equipamentoLogRepository.findById(id)
                .map(equipamentoLog -> mapToDTO(equipamentoLog, new EquipamentoLogDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final EquipamentoLogDTO equipamentoLogDTO) {
        final EquipamentoLog equipamentoLog = new EquipamentoLog();
        mapToEntity(equipamentoLogDTO, equipamentoLog);
        return equipamentoLogRepository.save(equipamentoLog).getId();
    }

    public void update(final Integer id, final EquipamentoLogDTO equipamentoLogDTO) {
        final EquipamentoLog equipamentoLog = equipamentoLogRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(equipamentoLogDTO, equipamentoLog);
        equipamentoLogRepository.save(equipamentoLog);
    }

    public void updateLog(final Integer id, final EquipamentoLogDTO equipamentoLogDTO) {
        final EquipamentoLog equipamentoLog = equipamentoLogRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntityUpdate(equipamentoLogDTO, equipamentoLog);
        equipamentoLogRepository.save(equipamentoLog);
    }

    public void updateAmostra(final Integer amostra_id, final Integer id) {
        Optional<Amostra> optional = amostraRepository.findAmostra(amostra_id);
        EquipamentoLog equipamentoLog = equipamentoLogRepository.findEquipamentoLogWithAmostras(id);
        if (optional.isPresent()) {
            if (!equipamentoLog.getAmostra().contains(optional.get())) {
                equipamentoLog.getAmostra().add(optional.get());
                equipamentoLogRepository.save(equipamentoLog);
            }
        }
    }

    public void deleteAmostra(final Integer amostra_id, final Integer id) {
        Optional<Amostra> optional = amostraRepository.findAmostra(amostra_id);
        EquipamentoLog equipamentoLog = equipamentoLogRepository.findEquipamentoLogWithAmostras(id);
        if (optional.isPresent()) {
            if (!equipamentoLog.getAmostra().contains(optional.get())) {
                equipamentoLog.getAmostra().remove(optional.get());
                equipamentoLogRepository.save(equipamentoLog);
            }
        }
    }

    public boolean amostraExists(final Integer amostra_id, final Integer id) {
        Optional<Amostra> optional = amostraRepository.findAmostra(amostra_id);
        return equipamentoLogRepository.findEquipamentoLogWithAmostras(id)
                .getAmostra()
                .contains(optional.get());
    }

    public List<Arquivos> findArquivosByEquipamentoLog(final Integer id) {
        final EquipamentoLog equipamentoLog = equipamentoLogRepository.findEquipamentoLogWithArquivos(id);
        return equipamentoLog.getArquivos()
                .stream()
                .toList();
    }

    public List<Amostra> findAmostrasByEquipamentoLog(final Integer id) {
        final EquipamentoLog equipamentoLog = equipamentoLogRepository.findEquipamentoLogWithAmostras(id);
        return equipamentoLog.getAmostra()
                .stream()
                .toList();
    }

    public void update(final EquipamentoLog equipamentoLog) {
        equipamentoLogRepository.save(equipamentoLog);
    }

    public EquipamentoLog findEquipamentoLogWithArquivos(final Integer id) {
        return equipamentoLogRepository.findEquipamentoLogWithArquivos(id);
    }

    public EquipamentoLog findEquipamentoLogWithAmostras(final Integer id) {
        return equipamentoLogRepository.findEquipamentoLogWithAmostras(id);
    }

    public void lock(final Integer id) {
        final EquipamentoLog equipamentoLog = equipamentoLogRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        equipamentoLog.setDataConfencia(LocalDateTime.now());
        equipamentoLogRepository.save(equipamentoLog);
    }

    public void unlock(final Integer id) {
        final EquipamentoLog equipamentoLog = equipamentoLogRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        equipamentoLog.setDataConfencia(null);
        equipamentoLogRepository.save(equipamentoLog);
    }

    public void delete(final Integer id) {
        equipamentoLogRepository.deleteById(id);
    }

    private EquipamentoLogDTO mapToDTO(final EquipamentoLog equipamentoLog,
                                       final EquipamentoLogDTO equipamentoLogDTO) {
        equipamentoLogDTO.setId(equipamentoLog.getId());
        equipamentoLogDTO.setDescricao(equipamentoLog.getDescricao());
        equipamentoLogDTO.setDataInicio(equipamentoLog.getDataInicio());
        equipamentoLogDTO.setDataFim(equipamentoLog.getDataFim());
        equipamentoLogDTO.setObs(equipamentoLog.getObs());
        equipamentoLogDTO.setDataConfencia(equipamentoLog.getDataConfencia());
        equipamentoLogDTO.setAtividade(equipamentoLog.getAtividade() == null ? null : equipamentoLog.getAtividade().getId());
        equipamentoLogDTO.setEquipamento(equipamentoLog.getEquipamento() == null ? null : equipamentoLog.getEquipamento().getId());
        equipamentoLogDTO.setUsuarioInicio(equipamentoLog.getUsuarioInicio() == null ? null : equipamentoLog.getUsuarioInicio().getId());
        equipamentoLogDTO.setUsuarioFim(equipamentoLog.getUsuarioFim() == null ? null : equipamentoLog.getUsuarioFim().getId());
        equipamentoLogDTO.setUsuarioConfencia(equipamentoLog.getUsuarioConfencia() == null ? null : equipamentoLog.getUsuarioConfencia().getId());
        equipamentoLogDTO.setVersion(equipamentoLog.getVersion());
        return equipamentoLogDTO;
    }

    private EquipamentoLogDTO mapToDTOByEquipamentoLog(final EquipamentoLog equipamentoLog,
                                                       final EquipamentoLogDTO equipamentoLogDTO) {
        equipamentoLogDTO.setId(equipamentoLog.getId());
        equipamentoLogDTO.setDescricao(equipamentoLog.getDescricao());
        equipamentoLogDTO.setDataInicio(equipamentoLog.getDataInicio());
        equipamentoLogDTO.setDataFim(equipamentoLog.getDataFim());
        equipamentoLogDTO.setObs(equipamentoLog.getObs());
        equipamentoLogDTO.setDataConfencia(equipamentoLog.getDataConfencia());
        equipamentoLogDTO.setAtividadeName(equipamentoLog.getAtividade() == null ? "" :
                equipamentoAtividadeRepository.findById(equipamentoLog.getAtividade().getId()).orElse(null).getAtividade());
        equipamentoLogDTO.setEquipamentoName(equipamentoLog.getEquipamento() == null ? "" :
                equipamentoRepository.findById(equipamentoLog.getEquipamento().getId()).orElse(null).getTag());
        equipamentoLogDTO.setUsuarioInicioName(equipamentoLog.getUsuarioInicio() == null ? "" :
                usuarioRepository.findById(equipamentoLog.getUsuarioInicio().getId()).orElse(null).getUsername());
        equipamentoLogDTO.setUsuarioFimName(equipamentoLog.getUsuarioFim() == null ? "" :
                usuarioRepository.findById(equipamentoLog.getUsuarioFim().getId()).orElse(null).getUsername());
        equipamentoLogDTO.setUsuarioConfenciaName(equipamentoLog.getUsuarioConfencia() == null ? "" :
                usuarioRepository.findById(equipamentoLog.getUsuarioConfencia().getId()).orElse(null).getUsername());
        equipamentoLogDTO.setVersion(equipamentoLog.getVersion());
        return equipamentoLogDTO;
    }

    private EquipamentoLog mapToEntity(final EquipamentoLogDTO equipamentoLogDTO,
                                       final EquipamentoLog equipamentoLog) {
        equipamentoLog.setDescricao(equipamentoLogDTO.getDescricao());
        equipamentoLog.setDataInicio(equipamentoLogDTO.getDataInicio());
        equipamentoLog.setDataFim(equipamentoLogDTO.getDataFim());
        equipamentoLog.setObs(equipamentoLogDTO.getObs());
        equipamentoLog.setDataConfencia(equipamentoLogDTO.getDataConfencia());
        final EquipamentoAtividade atividade = equipamentoLogDTO.getAtividade() == null ? null : equipamentoAtividadeRepository.findById(equipamentoLogDTO.getAtividade())
                .orElseThrow(() -> new NotFoundException("atividade not found"));
        equipamentoLog.setAtividade(atividade);
        final Equipamento equipamento = equipamentoLogDTO.getEquipamento() == null ? null : equipamentoRepository.findById(equipamentoLogDTO.getEquipamento())
                .orElseThrow(() -> new NotFoundException("equipamento not found"));
        equipamentoLog.setEquipamento(equipamento);
        final Usuario usuarioInicio = equipamentoLogDTO.getUsuarioInicio() == null ? null : usuarioRepository.findById(equipamentoLogDTO.getUsuarioInicio())
                .orElseThrow(() -> new NotFoundException("usuarioInicio not found"));
        equipamentoLog.setUsuarioInicio(usuarioInicio);
        final Usuario usuarioFim = equipamentoLogDTO.getUsuarioFim() == null ? null : usuarioRepository.findById(equipamentoLogDTO.getUsuarioFim())
                .orElseThrow(() -> new NotFoundException("usuarioFim not found"));
        equipamentoLog.setUsuarioFim(usuarioFim);
        final Usuario usuarioConferencia = equipamentoLogDTO.getUsuarioConfencia() == null ? null : usuarioRepository.findById(equipamentoLogDTO.getUsuarioConfencia())
                .orElseThrow(() -> new NotFoundException("usuarioFim not found"));
        equipamentoLog.setUsuarioConfencia(usuarioConferencia);
        return equipamentoLog;
    }

    private EquipamentoLog mapToEntityUpdate(final EquipamentoLogDTO equipamentoLogDTO,
                                             final EquipamentoLog equipamentoLog) {
        equipamentoLog.setDescricao(equipamentoLogDTO.getDescricao());
        equipamentoLog.setObs(equipamentoLogDTO.getObs());
        final EquipamentoAtividade atividade = equipamentoLogDTO.getAtividade() == null ? null : equipamentoAtividadeRepository.findById(equipamentoLogDTO.getAtividade())
                .orElseThrow(() -> new NotFoundException("atividade not found"));
        equipamentoLog.setAtividade(atividade);
        return equipamentoLog;
    }

}
