package br.com.lablims.spring_lablims.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.envers.repository.config.EnableEnversRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EntityScan("br.com.lablims.spring_lablims.domain")
@EnableJpaRepositories("br.com.lablims.spring_lablims.repos")
@EnableTransactionManagement
@EnableAutoConfiguration
@EnableWebSecurity
@EnableEnversRepositories
@EnableMethodSecurity
public class DomainConfig {

}
