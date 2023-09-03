package br.com.lablims.spring_lablims.config;

import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

/**
 * Load Thymeleaf files from the file system during development, without any caching.
 */
@Configuration
@Profile("Prod")
public class ServerProdConfig {


}
