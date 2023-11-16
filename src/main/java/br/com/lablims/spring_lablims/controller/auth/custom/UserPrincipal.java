package br.com.lablims.spring_lablims.controller.auth.custom;

import br.com.lablims.spring_lablims.domain.Usuario;
import br.com.lablims.spring_lablims.enums.SegurancaTipo;
import br.com.lablims.spring_lablims.repos.SegurancaRepository;
import br.com.lablims.spring_lablims.repos.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private Usuario usuario;

    private final UsuarioRepository usuarioRepository;

    private final SegurancaRepository segurancaRepository;

    public UserPrincipal(Usuario usuario, UsuarioRepository usuarioRepository, SegurancaRepository segurancaRepository){
        this.usuario = usuario;
        this.usuarioRepository = usuarioRepository;
        this.segurancaRepository = segurancaRepository;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final Usuario user = usuarioRepository.findByUsernameWithGrupo(usuario.getUsername());
        final List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getGrupo().getRegra()));
        return authorities;
    }

    @Override
    public String getPassword() {return usuario.getPassword();}

    @Override
    public String getUsername() {return usuario.getUsername();}

    @Override
    public boolean isAccountNonExpired() {return usuario.isAtivo();}

    @Override
    public boolean isAccountNonLocked() {
        if(segurancaRepository.findBySegurancaTipo(SegurancaTipo.user2faCode).getValue().equalsIgnoreCase("1")){
            return usuario.isToken();
        }else{
            return true;
        }

    }

    @Override
    public boolean isCredentialsNonExpired() {
        return usuario.isAtivo();
    }

    @Override
    public boolean isEnabled() {
        return usuario.isAtivo();
    }
}
