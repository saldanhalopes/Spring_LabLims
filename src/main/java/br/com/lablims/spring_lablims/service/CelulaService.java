package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Campanha;
import br.com.lablims.spring_lablims.domain.Celula;
import br.com.lablims.spring_lablims.domain.CelulaTipo;
import br.com.lablims.spring_lablims.domain.Equipamento;
import br.com.lablims.spring_lablims.domain.Usuario;
import br.com.lablims.spring_lablims.model.CelulaDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.CampanhaRepository;
import br.com.lablims.spring_lablims.repos.CelulaRepository;
import br.com.lablims.spring_lablims.repos.CelulaTipoRepository;
import br.com.lablims.spring_lablims.repos.EquipamentoRepository;
import br.com.lablims.spring_lablims.repos.UsuarioRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class CelulaService {

    private final CelulaRepository celulaRepository;
    private final EquipamentoRepository equipamentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CelulaTipoRepository celulaTipoRepository;
    private final CampanhaRepository campanhaRepository;

    public Celula findById(Integer id){
        return celulaRepository.findById(id).orElse(null);
    }

    public CelulaService(final CelulaRepository celulaRepository,
            final EquipamentoRepository equipamentoRepository,
            final UsuarioRepository usuarioRepository,
            final CelulaTipoRepository celulaTipoRepository,
            final CampanhaRepository campanhaRepository) {
        this.celulaRepository = celulaRepository;
        this.equipamentoRepository = equipamentoRepository;
        this.usuarioRepository = usuarioRepository;
        this.celulaTipoRepository = celulaTipoRepository;
        this.campanhaRepository = campanhaRepository;
    }

    public SimplePage<CelulaDTO> findAll(final String filter, final Pageable pageable) {
        Page<Celula> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = celulaRepository.findAllById(integerFilter, pageable);
        } else {
            page = celulaRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(celula -> mapToDTO(celula, new CelulaDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public CelulaDTO get(final Integer id) {
        return celulaRepository.findById(id)
                .map(celula -> mapToDTO(celula, new CelulaDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final CelulaDTO celulaDTO) {
        final Celula celula = new Celula();
        mapToEntity(celulaDTO, celula);
        return celulaRepository.save(celula).getId();
    }

    public void update(final Integer id, final CelulaDTO celulaDTO) {
        final Celula celula = celulaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(celulaDTO, celula);
        celulaRepository.save(celula);
    }

    public void delete(final Integer id) {
        celulaRepository.deleteById(id);
    }

    private CelulaDTO mapToDTO(final Celula celula, final CelulaDTO celulaDTO) {
        celulaDTO.setId(celula.getId());
        celulaDTO.setCelula(celula.getCelula());
        celulaDTO.setDescricao(celula.getDescricao());
        celulaDTO.setVersion(celula.getVersion());
        celulaDTO.setEquipamento(celula.getEquipamento().stream()
                .map(equipamento -> equipamento.getId())
                .toList());
        celulaDTO.setUsuario(celula.getUsuario().stream()
                .map(usuario -> usuario.getId())
                .toList());
        celulaDTO.setTipo(celula.getTipo() == null ? null : celula.getTipo().getId());
        return celulaDTO;
    }

    private Celula mapToEntity(final CelulaDTO celulaDTO, final Celula celula) {
        celula.setCelula(celulaDTO.getCelula());
        celula.setDescricao(celulaDTO.getDescricao());
        final List<Equipamento> equipamento = equipamentoRepository.findAllById(
                celulaDTO.getEquipamento() == null ? Collections.emptyList() : celulaDTO.getEquipamento());
        if (equipamento.size() != (celulaDTO.getEquipamento() == null ? 0 : celulaDTO.getEquipamento().size())) {
            throw new NotFoundException("one of equipamento not found");
        }
        celula.setEquipamento(equipamento.stream().collect(Collectors.toSet()));
        final List<Usuario> usuario = usuarioRepository.findAllById(
                celulaDTO.getUsuario() == null ? Collections.emptyList() : celulaDTO.getUsuario());
        if (usuario.size() != (celulaDTO.getUsuario() == null ? 0 : celulaDTO.getUsuario().size())) {
            throw new NotFoundException("one of usuario not found");
        }
        celula.setUsuario(usuario.stream().collect(Collectors.toSet()));
        final CelulaTipo tipo = celulaDTO.getTipo() == null ? null : celulaTipoRepository.findById(celulaDTO.getTipo())
                .orElseThrow(() -> new NotFoundException("tipo not found"));
        celula.setTipo(tipo);
        return celula;
    }

    public String getReferencedWarning(final Integer id) {
        final Celula celula = celulaRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Campanha celulaCampanha = campanhaRepository.findFirstByCelula(celula);
        if (celulaCampanha != null) {
            return WebUtils.getMessage("celula.campanha.celula.referenced", celulaCampanha.getId());
        }
        return null;
    }

}
