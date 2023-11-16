package br.com.lablims.spring_lablims.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ApplicationProperties {

    @Value("${app.version}")
    private String appVersion;

}
