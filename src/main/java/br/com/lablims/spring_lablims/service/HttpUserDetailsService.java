package br.com.lablims.spring_lablims.service;

import br.com.lablims.spring_lablims.domain.Usuario;
import br.com.lablims.spring_lablims.model.HttpUserDetails;
import br.com.lablims.spring_lablims.repos.UsuarioRepository;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class HttpUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public HttpUserDetailsService(final UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public HttpUserDetails loadUserByUsername(final String username) {
        final Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username);
        if (usuario == null) {
            log.warn("user not found: {}", username);
            throw new UsernameNotFoundException("User " + username + " not found");
        }
        final List<SimpleGrantedAuthority> authorities = usuario.getGrupos() == null ? Collections.emptyList() : 
                usuario.getGrupos()
                .stream()
                .filter(grupoRef -> grupoRef.getRegra() != null)
                .map(grupoRef -> new SimpleGrantedAuthority(grupoRef.getRegra()))
                .toList();
        return new HttpUserDetails(usuario.getId(), username, usuario.getPassword(), authorities);
    }

}
