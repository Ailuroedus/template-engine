package org.ailuroedus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FreemarkerConfig {
    @Bean
    public freemarker.template.Configuration configuration() {
        return new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_32);
    }
}
