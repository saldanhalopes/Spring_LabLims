package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.EquipamentoLogDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.util.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class EquipamentoLogService {

    private final EquipamentoLogRepository equipamentoLogRepository;
    private final EquipamentoAtividadeRepository equipamentoAtividadeRepository;
    private final EquipamentoRepository equipamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AmostraRepository amostraRepository;

    private final ArquivosRepository arquivosRepository;

    public EquipamentoLog findById(Integer id) {
        return equipamentoLogRepository.findById(id).orElse(null);
    }

    public EquipamentoLogService(final EquipamentoLogRepository equipamentoLogRepository,
                                 final EquipamentoAtividadeRepository equipamentoAtividadeRepository,
                                 final EquipamentoRepository equipamentoRepository,
                                 final UsuarioRepository usuarioRepository,
                                 final AmostraRepository amostraRepository,
                                 final ArquivosRepository arquivosRepository) {
        this.equipamentoLogRepository = equipamentoLogRepository;
        this.equipamentoAtividadeRepository = equipamentoAtividadeRepository;
        this.equipamentoRepository = equipamentoRepository;
        this.usuarioRepository = usuarioRepository;
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
        equipamentoLogDTO.setAtividade(equipamentoLog.getAtividade() == null ? null : equipamentoLog.getAtividade().getId());
        equipamentoLogDTO.setEquipamento(equipamentoLog.getEquipamento() == null ? null : equipamentoLog.getEquipamento().getId());
        equipamentoLogDTO.setUsuarioInicio(equipamentoLog.getUsuarioInicio() == null ? null : equipamentoLog.getUsuarioInicio().getId());
        equipamentoLogDTO.setUsuarioFim(equipamentoLog.getUsuarioFim() == null ? null : equipamentoLog.getUsuarioFim().getId());
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
        equipamentoLogDTO.setAtividadeName(equipamentoLog.getAtividade() == null ? "" :
                equipamentoAtividadeRepository.findById(equipamentoLog.getAtividade().getId()).orElse(null).getAtividade());
        equipamentoLogDTO.setEquipamentoName(equipamentoLog.getEquipamento() == null ? "" :
                equipamentoRepository.findById(equipamentoLog.getEquipamento().getId()).orElse(null).getTag());
        equipamentoLogDTO.setUsuarioInicioName(equipamentoLog.getUsuarioInicio() == null ? "" :
                usuarioRepository.findById(equipamentoLog.getUsuarioInicio().getId()).orElse(null).getUsername());
        equipamentoLogDTO.setUsuarioFimName(equipamentoLog.getUsuarioFim() == null ? "" :
                usuarioRepository.findById(equipamentoLog.getUsuarioFim().getId()).orElse(null).getUsername());
        equipamentoLogDTO.setVersion(equipamentoLog.getVersion());
        return equipamentoLogDTO;
    }

    private EquipamentoLog mapToEntity(final EquipamentoLogDTO equipamentoLogDTO,
                                       final EquipamentoLog equipamentoLog) {
        equipamentoLog.setDescricao(equipamentoLogDTO.getDescricao());
        equipamentoLog.setDataInicio(equipamentoLogDTO.getDataInicio());
        equipamentoLog.setDataFim(equipamentoLogDTO.getDataFim());
        equipamentoLog.setObs(equipamentoLogDTO.getObs());
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
