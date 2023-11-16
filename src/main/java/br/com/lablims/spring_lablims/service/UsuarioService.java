package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.controller.auth.custom.UserPrincipal;
import br.com.lablims.spring_lablims.domain.*;
import br.com.lablims.spring_lablims.model.SimplePage;
import br.com.lablims.spring_lablims.model.UsuarioDTO;
import br.com.lablims.spring_lablims.repos.*;
import br.com.lablims.spring_lablims.util.NotFoundException;
import br.com.lablims.spring_lablims.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final CelulaRepository celulaRepository;
    private final ColunaLogRepository colunaLogRepository;
    private final AtaTurnoRepository ataTurnoRepository;
    private final EquipamentoLogRepository equipamentoLogRepository;
    private final AmostraStatusRepository amostraStatusRepository;
    private final SolucaoRegistroRepository solucaoRegistroRepository;
    private final SegurancaRepository segurancaRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        if (usuario == null) {
            throw new UsernameNotFoundException("user " + username + " not found");
        }
        return new UserPrincipal(usuario, usuarioRepository, segurancaRepository);
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

    public Usuario findById(Integer id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario findBySecret(String secret) {
        return usuarioRepository.findBySecret(secret).orElse(null);
    }

    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsernameIgnoreCase(username);
    }

    public boolean validarUser(String usuario, String password) {
        try {
            final Usuario user = usuarioRepository.findByUsernameWithGrupo(usuario);
            return passwordEncoder.matches(password, user.getPassword());
        } catch (final NotFoundException notFoundException) {
            return false;
        }
    }

    public void alterarSenha(String usuario, String password) {
        Usuario user = usuarioRepository.findByUsername(usuario).orElse(null);
        user.setChangePass(false);
        user.setPassword(passwordEncoder.encode(password));
        usuarioRepository.save(user);
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
        usuarioDTO.setCidade(usuario.getCidade());
        usuarioDTO.setCpf(usuario.getCpf());
        usuarioDTO.setDetalhes(usuario.getDetalhes());
        usuarioDTO.setEndereco(usuario.getEndereco());
        usuarioDTO.setEstado(usuario.getEstado());
        usuarioDTO.setNome(usuario.getNome());
        usuarioDTO.setPais(usuario.getPais());
        usuarioDTO.setSobrenome(usuario.getSobrenome());
        usuarioDTO.setTelefone(usuario.getTelefone());
        usuarioDTO.setVersion(usuario.getVersion());
        usuarioDTO.setSecret(usuario.getSecret());
        usuarioDTO.setAtivo(usuario.isAtivo());
        return usuarioDTO;
    }

    private Usuario mapToEntity(final UsuarioDTO usuarioDTO, final Usuario usuario) {
        usuario.setCep(usuarioDTO.getCep());
        usuario.setCidade(usuarioDTO.getCidade());
        usuario.setCpf(usuarioDTO.getCpf());
        usuario.setDetalhes(usuarioDTO.getDetalhes());
        usuario.setEndereco(usuarioDTO.getEndereco());
        usuario.setEstado(usuarioDTO.getEstado());
        usuario.setNome(usuarioDTO.getNome());
        usuario.setPais(usuarioDTO.getPais());
        usuario.setSobrenome(usuarioDTO.getSobrenome());
        usuario.setTelefone(usuarioDTO.getTelefone());
        usuario.setSecret(usuarioDTO.getSecret());
        usuario.setAtivo(usuarioDTO.isAtivo());
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
        final AmostraStatus conferente1AmostraStatus = amostraStatusRepository.findFirstByConferente1(usuario);
        if (conferente1AmostraStatus != null) {
            return WebUtils.getMessage("usuario.amostraStatus.conferente1.referenced", conferente1AmostraStatus.getId());
        }
        final AmostraStatus conferente2AmostraStatus = amostraStatusRepository.findFirstByConferente2(usuario);
        if (conferente2AmostraStatus != null) {
            return WebUtils.getMessage("usuario.amostraStatus.conferente2.referenced", conferente2AmostraStatus.getId());
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
