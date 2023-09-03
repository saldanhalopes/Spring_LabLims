package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.AtaTurno;
import br.com.lablims.spring_lablims.domain.Celula;
import br.com.lablims.spring_lablims.domain.ColunaLog;
import br.com.lablims.spring_lablims.domain.EquipamentoLog;
import br.com.lablims.spring_lablims.domain.Grupo;
import br.com.lablims.spring_lablims.domain.LoteStatus;
import br.com.lablims.spring_lablims.domain.SolucaoRegistro;
import br.com.lablims.spring_lablims.domain.Usuario;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.model.UsuarioDTO;
import br.com.lablims.spring_lablims.repos.AtaTurnoRepository;
import br.com.lablims.spring_lablims.repos.CelulaRepository;
import br.com.lablims.spring_lablims.repos.ColunaLogRepository;
import br.com.lablims.spring_lablims.repos.EquipamentoLogRepository;
import br.com.lablims.spring_lablims.repos.GrupoRepository;
import br.com.lablims.spring_lablims.repos.LoteStatusRepository;
import br.com.lablims.spring_lablims.repos.SolucaoRegistroRepository;
import br.com.lablims.spring_lablims.repos.UsuarioRepository;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import jakarta.transaction.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final GrupoRepository grupoRepository;
    private final PasswordEncoder passwordEncoder;
    private final CelulaRepository celulaRepository;
    private final ColunaLogRepository colunaLogRepository;
    private final AtaTurnoRepository ataTurnoRepository;
    private final EquipamentoLogRepository equipamentoLogRepository;
    private final LoteStatusRepository loteStatusRepository;
    private final SolucaoRegistroRepository solucaoRegistroRepository;

    public Usuario findById(Integer id){
        return usuarioRepository.findById(id).orElse(null);
    }

    public UsuarioService(final UsuarioRepository usuarioRepository,
                          final GrupoRepository grupoRepository, final PasswordEncoder passwordEncoder,
                          final CelulaRepository celulaRepository, final ColunaLogRepository colunaLogRepository,
                          final AtaTurnoRepository ataTurnoRepository,
                          final EquipamentoLogRepository equipamentoLogRepository,
                          final LoteStatusRepository loteStatusRepository,
                          final SolucaoRegistroRepository solucaoRegistroRepository) {
        this.usuarioRepository = usuarioRepository;
        this.grupoRepository = grupoRepository;
        this.passwordEncoder = passwordEncoder;
        this.celulaRepository = celulaRepository;
        this.colunaLogRepository = colunaLogRepository;
        this.ataTurnoRepository = ataTurnoRepository;
        this.equipamentoLogRepository = equipamentoLogRepository;
        this.loteStatusRepository = loteStatusRepository;
        this.solucaoRegistroRepository = solucaoRegistroRepository;
    }

    public SimplePage<UsuarioDTO> findAll(final String filter, final Pageable pageable) {
        Page<Usuario> page;
        if (filter != null) {
            Integer integerFilter = null;
            try {
                integerFilter = Integer.parseInt(filter);
            } catch (final NumberFormatException numberFormatException) {
                // keep null - no parseable input
            }
            page = usuarioRepository.findAllById(integerFilter, pageable);
        } else {
            page = usuarioRepository.findAll(pageable);
        }
        return new SimplePage<>(page.getContent()
                .stream()
                .map(usuario -> mapToDTO(usuario, new UsuarioDTO()))
                .toList(),
                page.getTotalElements(), pageable);
    }

    public UsuarioDTO get(final Integer id) {
        return usuarioRepository.findById(id)
                .map(usuario -> mapToDTO(usuario, new UsuarioDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsernameIgnoreCase(username);
    }

    public boolean validarUser(String usuario, String password) {
        try {
            final Usuario user = usuarioRepository.findByUsername(usuario).orElse(null);
            return passwordEncoder.matches(password, user.getPassword());
        } catch (final NotFoundException notFoundException) {
            return false;
        }
    }

    public Integer create(final UsuarioDTO usuarioDTO) {
        final Usuario usuario = new Usuario();
        mapToEntity(usuarioDTO, usuario);
        return usuarioRepository.save(usuario).getId();
    }

    public void update(final Integer id, final UsuarioDTO usuarioDTO) {
        final Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(usuarioDTO, usuario);
        usuarioRepository.save(usuario);
    }

    public void delete(final Integer id) {
        final Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        // remove many-to-many relations at owning side
        celulaRepository.findAllByUsuario(usuario)
                .forEach(celula -> celula.getUsuario().remove(usuario));
        usuarioRepository.delete(usuario);
    }

    private UsuarioDTO mapToDTO(final Usuario usuario, final UsuarioDTO usuarioDTO) {
        usuarioDTO.setId(usuario.getId());
        usuarioDTO.setCep(usuario.getCep());
        usuarioDTO.setChangePass(usuario.getChangePass());
        usuarioDTO.setCidade(usuario.getCidade());
        usuarioDTO.setCpf(usuario.getCpf());
        usuarioDTO.setDetalhes(usuario.getDetalhes());
        usuarioDTO.setEmail(usuario.getEmail());
        usuarioDTO.setEndereco(usuario.getEndereco());
        usuarioDTO.setEstado(usuario.getEstado());
        usuarioDTO.setFailedAccessCount(usuario.getFailedAccessCount());
        usuarioDTO.setLastChangePass(usuario.getLastChangePass());
        usuarioDTO.setLastLogin(usuario.getLastLogin());
        usuarioDTO.setLastLogout(usuario.getLastLogout());
        usuarioDTO.setNome(usuario.getNome());
        usuarioDTO.setPais(usuario.getPais());
        usuarioDTO.setSobrenome(usuario.getSobrenome());
        usuarioDTO.setTelefone(usuario.getTelefone());
        usuarioDTO.setUsername(usuario.getUsername());
        usuarioDTO.setVersion(usuario.getVersion());
        usuarioDTO.setGrupos(usuario.getGrupos().stream()
                .map(grupo -> grupo.getId())
                .toList());
        return usuarioDTO;
    }

    private Usuario mapToEntity(final UsuarioDTO usuarioDTO, final Usuario usuario) {
        usuario.setCep(usuarioDTO.getCep());
        usuario.setChangePass(usuarioDTO.getChangePass());
        usuario.setCidade(usuarioDTO.getCidade());
        usuario.setCpf(usuarioDTO.getCpf());
        usuario.setDetalhes(usuarioDTO.getDetalhes());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setEndereco(usuarioDTO.getEndereco());
        usuario.setEstado(usuarioDTO.getEstado());
        usuario.setFailedAccessCount(usuarioDTO.getFailedAccessCount());
        usuario.setLastChangePass(usuarioDTO.getLastChangePass());
        usuario.setLastLogin(usuarioDTO.getLastLogin());
        usuario.setLastLogout(usuarioDTO.getLastLogout());
        usuario.setNome(usuarioDTO.getNome());
        usuario.setPais(usuarioDTO.getPais());
        usuario.setPassword(passwordEncoder.encode(usuarioDTO.getPassword()));
        usuario.setSobrenome(usuarioDTO.getSobrenome());
        usuario.setTelefone(usuarioDTO.getTelefone());
        usuario.setUsername(usuarioDTO.getUsername());
        final List<Grupo> grupos = grupoRepository.findAllById(
                usuarioDTO.getGrupos() == null ? Collections.emptyList() : usuarioDTO.getGrupos());
        if (grupos.size() != (usuarioDTO.getGrupos() == null ? 0 : usuarioDTO.getGrupos().size())) {
            throw new NotFoundException("one of grupos not found");
        }
        usuario.setGrupos(grupos.stream().collect(Collectors.toSet()));
        return usuario;
    }

    public boolean emailExists(final String email) {
        return usuarioRepository.existsByEmailIgnoreCase(email);
    }

    public boolean usernameExists(final String username) {
        return usuarioRepository.existsByUsernameIgnoreCase(username);
    }

    public String getReferencedWarning(final Integer id) {
        final Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Celula usuarioCelula = celulaRepository.findFirstByUsuario(usuario);
        if (usuarioCelula != null) {
            return WebUtils.getMessage("usuario.celula.usuario.referenced", usuarioCelula.getId());
        }
        final ColunaLog usuarioInicioColunaLog = colunaLogRepository.findFirstByUsuarioInicio(usuario);
        if (usuarioInicioColunaLog != null) {
            return WebUtils.getMessage("usuario.colunaLog.usuarioInicio.referenced", usuarioInicioColunaLog.getId());
        }
        final ColunaLog usuarioFimColunaLog = colunaLogRepository.findFirstByUsuarioFim(usuario);
        if (usuarioFimColunaLog != null) {
            return WebUtils.getMessage("usuario.colunaLog.usuarioFim.referenced", usuarioFimColunaLog.getId());
        }
        final AtaTurno usuarioAtaTurno = ataTurnoRepository.findFirstByUsuario(usuario);
        if (usuarioAtaTurno != null) {
            return WebUtils.getMessage("usuario.ataTurno.usuario.referenced", usuarioAtaTurno.getId());
        }
        final EquipamentoLog usuarioInicioEquipamentoLog = equipamentoLogRepository.findFirstByUsuarioInicio(usuario);
        if (usuarioInicioEquipamentoLog != null) {
            return WebUtils.getMessage("usuario.equipamentoLog.usuarioInicio.referenced", usuarioInicioEquipamentoLog.getId());
        }
        final EquipamentoLog usuarioFimEquipamentoLog = equipamentoLogRepository.findFirstByUsuarioFim(usuario);
        if (usuarioFimEquipamentoLog != null) {
            return WebUtils.getMessage("usuario.equipamentoLog.usuarioFim.referenced", usuarioFimEquipamentoLog.getId());
        }
        final LoteStatus conferente1LoteStatus = loteStatusRepository.findFirstByConferente1(usuario);
        if (conferente1LoteStatus != null) {
            return WebUtils.getMessage("usuario.loteStatus.conferente1.referenced", conferente1LoteStatus.getId());
        }
        final LoteStatus conferente2LoteStatus = loteStatusRepository.findFirstByConferente2(usuario);
        if (conferente2LoteStatus != null) {
            return WebUtils.getMessage("usuario.loteStatus.conferente2.referenced", conferente2LoteStatus.getId());
        }
        final SolucaoRegistro criadorSolucaoRegistro = solucaoRegistroRepository.findFirstByCriador(usuario);
        if (criadorSolucaoRegistro != null) {
            return WebUtils.getMessage("usuario.solucaoRegistro.criador.referenced", criadorSolucaoRegistro.getId());
        }
        final SolucaoRegistro conferenteSolucaoRegistro = solucaoRegistroRepository.findFirstByConferente(usuario);
        if (conferenteSolucaoRegistro != null) {
            return WebUtils.getMessage("usuario.solucaoRegistro.conferente.referenced", conferenteSolucaoRegistro.getId());
        }
        return null;
    }

}
