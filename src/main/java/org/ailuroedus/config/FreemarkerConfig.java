/*
 * Copyright [2024] [Ailuroedus team]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ailuroedus.config;

import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateMethodModelEx;
import org.ailuroedus.util.template.TemplateConstants;
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
