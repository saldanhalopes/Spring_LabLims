package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Grupo;
import br.com.lablims.spring_lablims.domain.Usuario;
import br.com.lablims.spring_lablims.model.GrupoDTO;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.repos.GrupoRepository;
import br.com.lablims.spring_lablims.repos.UsuarioRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class GrupoService {

    private final GrupoRepository grupoRepository;
    private final UsuarioRepository usuarioRepository;

    public Grupo findById(Integer id){
        return grupoRepository.findById(id).orElse(null);
    }

    public GrupoService(final GrupoRepository grupoRepository,
            final UsuarioRepository usuarioRepository) {
        this.grupoRepository = grupoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public SimplePage<GrupoDTO> findAll(final String filter, final Pageable pageable) {
        Page<Grupo> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = grupoRepository.findAllById(integerFilter, pageable);
        } else {
            page = grupoRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(grupo -> mapToDTO(grupo, new GrupoDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public GrupoDTO get(final Integer id) {
        return grupoRepository.findById(id)
                .map(grupo -> mapToDTO(grupo, new GrupoDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final GrupoDTO grupoDTO) {
        final Grupo grupo = new Grupo();
        mapToEntity(grupoDTO, grupo);
        return grupoRepository.save(grupo).getId();
    }

    public void update(final Integer id, final GrupoDTO grupoDTO) {
        final Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(grupoDTO, grupo);
        grupoRepository.save(grupo);
    }

    public void delete(final Integer id) {
        final Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        usuarioRepository.findAllByGrupos(grupo)
                .forEach(usuario -> usuario.getGrupos().remove(grupo));
        grupoRepository.delete(grupo);
    }

    private GrupoDTO mapToDTO(final Grupo grupo, final GrupoDTO grupoDTO) {
        grupoDTO.setId(grupo.getId());
        grupoDTO.setGrupo(grupo.getGrupo());
        grupoDTO.setTipo(grupo.getTipo());
        grupoDTO.setRegra(grupo.getRegra());
        grupoDTO.setVersion(grupo.getVersion());
        return grupoDTO;
    }

    private Grupo mapToEntity(final GrupoDTO grupoDTO, final Grupo grupo) {
        grupo.setGrupo(grupoDTO.getGrupo());
        grupo.setTipo(grupoDTO.getTipo());
        grupo.setRegra(grupoDTO.getRegra());
        return grupo;
    }

    public String getReferencedWarning(final Integer id) {
        final Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Usuario gruposUsuario = usuarioRepository.findFirstByGrupos(grupo);
        if (gruposUsuario != null) {
            return WebUtils.getMessage("grupo.usuario.grupos.referenced", gruposUsuario.getId());
        }
        return null;
    }

}
