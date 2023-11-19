package br.com.lablims.spring_lablims.config;

import br.com.lablims.spring_lablims.domain.Grupo;
import br.com.lablims.spring_lablims.domain.Seguranca;
import br.com.lablims.spring_lablims.domain.Sistema;
import br.com.lablims.spring_lablims.domain.Usuario;
import br.com.lablims.spring_lablims.enums.SegurancaTipo;
import br.com.lablims.spring_lablims.repos.GrupoRepository;
import br.com.lablims.spring_lablims.repos.SegurancaRepository;
import br.com.lablims.spring_lablims.repos.SistemaRepository;
import br.com.lablims.spring_lablims.service.RegistrationService;
import br.com.lablims.spring_lablims.util.UserRoles;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;


@Component
public class ApplicationLoader implements ApplicationRunner {

    private final GrupoRepository grupoRepository;

    private final SistemaRepository sistemaRepository;

    private final SegurancaRepository segurancaRepository;

    private final RegistrationService registrationService;

    public ApplicationLoader(final GrupoRepository grupoRepository,
                             final SistemaRepository sistemaRepository,
                             SegurancaRepository segurancaRepository,
                             final RegistrationService registrationService) {
        this.grupoRepository = grupoRepository;
        this.sistemaRepository = sistemaRepository;
        this.segurancaRepository = segurancaRepository;
        this.registrationService = registrationService;
    }


    @Override
    public void run(final ApplicationArguments args) throws IOException {
        if (sistemaRepository.count() == 0) {
            final Sistema sistema = new Sistema();
            sistema.setSistemaNome("Lablims");
            sistema.setSistemaCriador("Rafael Saldanha Lopes");
            sistema.setBuilderVersao(1.0);
            sistema.setDetalhes("Sistema de Gerenciamento de Laboraório");
            sistemaRepository.save(sistema);
            final Seguranca seguranca = new Seguranca();
            seguranca.setSegurancaTipo(SegurancaTipo.user2faCode);
            seguranca.setValue("false");
            segurancaRepository.save(seguranca);
        }
        if (grupoRepository.count() == 0) {
            final Grupo adminGrupo = new Grupo();
            adminGrupo.setRegra(UserRoles.ADMIN);
            adminGrupo.setGrupo("ADMIN");
            adminGrupo.setTipo("ADMINISTRADOR");
            grupoRepository.save(adminGrupo);
            final Usuario usuario = new Usuario();
            usuario.setEmail("admin@admin.com");
            usuario.setNome("Administrador");
            usuario.setSobrenome("Lablims");
            usuario.setUsername("admin");
            usuario.setPassword("admin");
            usuario.setAtivo(true);
            usuario.setToken(true);
            usuario.setGrupo(adminGrupo);
            registrationService.register(usuario);
        }

    }

}
