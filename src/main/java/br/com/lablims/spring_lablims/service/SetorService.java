package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.domain.Storage;
import br.com.lablims.spring_lablims.model.SetorDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.AtaTurnoRepository;
import br.com.lablims.spring_lablims.repos.CampanhaRepository;
import br.com.lablims.spring_lablims.repos.StorageRepository;
import br.com.lablims.spring_lablims.repos.ColunaUtilRepository;
import br.com.lablims.spring_lablims.repos.DepartamentoRepository;
import br.com.lablims.spring_lablims.repos.EquipamentoRepository;
import br.com.lablims.spring_lablims.repos.PlanoAnaliseRepository;
import br.com.lablims.spring_lablims.repos.SetorRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SetorService {

    private final SetorRepository setorRepository;
    private final DepartamentoRepository departamentoRepository;
    private final StorageRepository storageRepository;
    private final ColunaUtilRepository colunaUtilRepository;
    private final EquipamentoRepository equipamentoRepository;
    private final CampanhaRepository campanhaRepository;
    private final AtaTurnoRepository ataTurnoRepository;
    private final PlanoAnaliseRepository planoAnaliseRepository;

    public Setor findById(Integer id){
        return setorRepository.findById(id).orElse(null);
    }

    public SimplePage<SetorDTO> findAll(final String filter, final Pageable pageable) {
        Page<Setor> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = setorRepository.findAllById(integerFilter, pageable);
        } else {
            page = setorRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(setor -> mapToDTO(setor, new SetorDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public SetorDTO get(final Integer id) {
        return setorRepository.findById(id)
                .map(setor -> mapToDTO(setor, new SetorDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final SetorDTO setorDTO) {
        final Setor setor = new Setor();
        mapToEntity(setorDTO, setor);
        return setorRepository.save(setor).getId();
    }

    public void update(final Integer id, final SetorDTO setorDTO) {
        final Setor setor = setorRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(setorDTO, setor);
        setorRepository.save(setor);
    }

    public void delete(final Integer id) {
        setorRepository.deleteById(id);
    }

    private SetorDTO mapToDTO(final Setor setor, final SetorDTO setorDTO) {
        setorDTO.setId(setor.getId());
        setorDTO.setSetor(setor.getSetor());
        setorDTO.setSiglaSetor(setor.getSiglaSetor());
        setorDTO.setDescricao(setor.getDescricao());
        setorDTO.setTipo(setor.getTipo());
        setorDTO.setDepartamento(setor.getDepartamento() == null ? null : setor.getDepartamento().getId());
        setorDTO.setVersion(setor.getVersion());
        return setorDTO;
    }

    private Setor mapToEntity(final SetorDTO setorDTO, final Setor setor) {
        setor.setSetor(setorDTO.getSetor());
        setor.setSiglaSetor(setorDTO.getSiglaSetor());
        setor.setDescricao(setorDTO.getDescricao());
        setor.setTipo(setorDTO.getTipo());
        final Departamento departamento = setorDTO.getDepartamento() == null ? null : departamentoRepository.findById(setorDTO.getDepartamento())
                .orElseThrow(() -> new NotFoundException("departamento not found"));
        setor.setDepartamento(departamento);
        return setor;
    }

    public String getReferencedWarning(final Integer id) {
        final Setor setor = setorRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Storage setorStorage = storageRepository.findFirstBySetor(setor);
        if (setorStorage != null) {
            return WebUtils.getMessage("entity.referenced", setorStorage.getId());
        }
        final ColunaUtil setorColunaUtil = colunaUtilRepository.findFirstBySetor(setor);
        if (setorColunaUtil != null) {
            return WebUtils.getMessage("entity.referenced", setorColunaUtil.getId());
        }
        final Equipamento setorEquipamento = equipamentoRepository.findFirstBySetor(setor);
        if (setorEquipamento != null) {
            return WebUtils.getMessage("entity.referenced", setorEquipamento.getId());
        }
        final Campanha setorCampanha = campanhaRepository.findFirstBySetor(setor);
        if (setorCampanha != null) {
            return WebUtils.getMessage("entity.referenced", setorCampanha.getId());
        }
        final AtaTurno setorAtaTurno = ataTurnoRepository.findFirstBySetor(setor);
        if (setorAtaTurno != null) {
            return WebUtils.getMessage("entity.referenced", setorAtaTurno.getId());
        }
        final PlanoAnalise setorPlanoAnalise = planoAnaliseRepository.findFirstBySetor(setor);
        if (setorPlanoAnalise != null) {
            return WebUtils.getMessage("entity.referenced", setorPlanoAnalise.getId());
        }
        return null;
    }

}
