package br.com.lablims.spring_lablims.config;

import br.com.lablims.spring_lablims.domain.Grupo;
import br.com.lablims.spring_lablims.model.RegistrationRequest;
import br.com.lablims.spring_lablims.repos.GrupoRepository;
import br.com.lablims.spring_lablims.service.RegistrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class RoleLoader implements ApplicationRunner {

    private final GrupoRepository grupoRepository;

    private final RegistrationService registrationService;

    public RoleLoader(final GrupoRepository grupoRepository, final RegistrationService registrationService) {
        this.grupoRepository = grupoRepository;
        this.registrationService = registrationService;
    }

    @Override
    public void run(final ApplicationArguments args) {
        if (grupoRepository.count() != 0) {
            return;
        }
        log.info("initializing roles");
        final Grupo adminGrupo = new Grupo();
        adminGrupo.setRegra("ADMIN");
        adminGrupo.setGrupo("ADMIN");
        adminGrupo.setTipo("ADMINISTRADOR");
        grupoRepository.save(adminGrupo);
        final RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setEmail("admin@admin.com");
        registrationRequest.setNome("Administrador");
        registrationRequest.setSobrenome("Lablims");
        registrationRequest.setUsername("admin");
        registrationRequest.setPassword("admin");
        registrationService.register(registrationRequest);
    }

}