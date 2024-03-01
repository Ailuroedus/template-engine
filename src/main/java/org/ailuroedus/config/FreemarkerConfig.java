package org.ailuroedus.config;

import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateMethodModelEx;
import org.ailuroedus.constant.TemplateConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FreemarkerConfig {
    @Bean
    public freemarker.template.Configuration configuration() {
        final var config = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_32);
        config.setSharedVariable(TemplateConstants.CREATE_EMPTY_IF_METHOD, createEmptyIf());
        return config;
    }

    @Bean
    public TemplateMethodModelEx createEmptyIf() {
        return args -> {
            if (args.size() == 1 && args.getFirst() instanceof TemplateBooleanModel conditionModel) {
                return conditionModel.getAsBoolean() ? TemplateConstants.SERVICE_RECORD : "";
            }

            return "";
        };
    }
}
