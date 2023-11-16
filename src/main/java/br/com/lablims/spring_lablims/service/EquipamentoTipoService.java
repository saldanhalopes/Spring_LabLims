package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Equipamento;
import br.com.lablims.spring_lablims.domain.EquipamentoTipo;
import br.com.lablims.spring_lablims.model.EquipamentoTipoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.EquipamentoRepository;
import br.com.lablims.spring_lablims.repos.EquipamentoTipoRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EquipamentoTipoService {

    private final EquipamentoTipoRepository equipamentoTipoRepository;
    private final EquipamentoRepository equipamentoRepository;

    public EquipamentoTipo findById(Integer id){
        return equipamentoTipoRepository.findById(id).orElse(null);
    }

    public SimplePage<EquipamentoTipoDTO> findAll(final String filter, final Pageable pageable) {
        Page<EquipamentoTipo> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = equipamentoTipoRepository.findAllById(integerFilter, pageable);
        } else {
            page = equipamentoTipoRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(equipamentoTipo -> mapToDTO(equipamentoTipo, new EquipamentoTipoDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public EquipamentoTipoDTO get(final Integer id) {
        return equipamentoTipoRepository.findById(id)
                .map(equipamentoTipo -> mapToDTO(equipamentoTipo, new EquipamentoTipoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final EquipamentoTipoDTO equipamentoTipoDTO) {
        final EquipamentoTipo equipamentoTipo = new EquipamentoTipo();
        mapToEntity(equipamentoTipoDTO, equipamentoTipo);
        return equipamentoTipoRepository.save(equipamentoTipo).getId();
    }

    public void update(final Integer id, final EquipamentoTipoDTO equipamentoTipoDTO) {
        final EquipamentoTipo equipamentoTipo = equipamentoTipoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(equipamentoTipoDTO, equipamentoTipo);
        equipamentoTipoRepository.save(equipamentoTipo);
    }

    public void delete(final Integer id) {
        equipamentoTipoRepository.deleteById(id);
    }

    private EquipamentoTipoDTO mapToDTO(final EquipamentoTipo equipamentoTipo,
            final EquipamentoTipoDTO equipamentoTipoDTO) {
        equipamentoTipoDTO.setId(equipamentoTipo.getId());
        equipamentoTipoDTO.setTipo(equipamentoTipo.getTipo());
        equipamentoTipoDTO.setVersion(equipamentoTipo.getVersion());
        return equipamentoTipoDTO;
    }

    private EquipamentoTipo mapToEntity(final EquipamentoTipoDTO equipamentoTipoDTO,
            final EquipamentoTipo equipamentoTipo) {
        equipamentoTipo.setTipo(equipamentoTipoDTO.getTipo());
        return equipamentoTipo;
    }

    public String getReferencedWarning(final Integer id) {
        final EquipamentoTipo equipamentoTipo = equipamentoTipoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Equipamento tipoEquipamento = equipamentoRepository.findFirstByTipo(equipamentoTipo);
        if (tipoEquipamento != null) {
            return WebUtils.getMessage("equipamentoTipo.equipamento.tipo.referenced", tipoEquipamento.getId());
        }
        return null;
    }

}
