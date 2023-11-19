package br.com.lablims.spring_lablims.controller.auth.custom;

import br.com.lablims.spring_lablims.domain.Usuario;
import br.com.lablims.spring_lablims.enums.SegurancaTipo;
import br.com.lablims.spring_lablims.repos.SegurancaRepository;
import br.com.lablims.spring_lablims.repos.UsuarioRepository;
import com.amdelamar.jotp.OTP;
import com.amdelamar.jotp.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;


public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SegurancaRepository segurancaRepository;

    public CustomAuthenticationProvider(UserDetailsService userDetailsService,
                                        PasswordEncoder passwordEncoder) {
        super();
        this.setUserDetailsService(userDetailsService);
        this.setPasswordEncoder(passwordEncoder);
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
                                                  UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        super.additionalAuthenticationChecks(userDetails, authentication);
        Usuario usuario = usuarioRepository.findByUsername(authentication.getName()).orElse(null);
        if (usuario != null) {
            try {
                if (segurancaRepository.findBySegurancaTipo(SegurancaTipo.user2faCode).getValue().equalsIgnoreCase("1")) {
                    String serverGeneratedCode = OTP.create(usuario.getSecret(), OTP.timeInHex(System.currentTimeMillis()), 6, Type.TOTP);
                    CustomAuthenticationDetails userProvidedLoginDetails = (CustomAuthenticationDetails) authentication.getDetails();
                    if (!serverGeneratedCode.equals(userProvidedLoginDetails.getUser2FaCode())) {
                        throw new BadCredentialsException("Codigo invalido");
                    }
                }
            } catch (Exception e) {
                throw new AuthenticationServiceException("Falha ao gerar código 2FA do lado do servidor");
            }

        }
    }



}
