package br.com.lablims.spring_lablims.config;

import java.io.File;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;


/**
 * Load Thymeleaf files from the file system during development, without any caching.
 */
@Configuration
@Profile("Local")
public class LocalDevConfig {

    @SneakyThrows
    public LocalDevConfig(final TemplateEngine templateEngine) {
        File sourceRoot = new ClassPathResource("application.properties").getFile().getParentFile();
        while (sourceRoot.listFiles((dir, name) -> name.equals("mvnw")).length != 1) {
            sourceRoot = sourceRoot.getParentFile();
        }
        final FileTemplateResolver fileTemplateResolver = new FileTemplateResolver();
        fileTemplateResolver.setPrefix(sourceRoot.getPath() + "/src/main/resources/templates/");
        System.out.println(sourceRoot.getPath() + "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
        fileTemplateResolver.setSuffix(".html");
        fileTemplateResolver.setCacheable(false);
        fileTemplateResolver.setCharacterEncoding("UTF-8");
        fileTemplateResolver.setCheckExistence(true);
        templateEngine.setTemplateResolver(fileTemplateResolver);
    }

}
