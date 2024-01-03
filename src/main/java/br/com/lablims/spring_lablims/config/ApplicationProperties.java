package br.com.lablims.spring_lablims.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
@Getter
public class ApplicationProperties {

    @Value("${app.version}")
    private String appVersion;

}
