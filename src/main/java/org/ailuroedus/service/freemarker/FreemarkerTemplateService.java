
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

package org.ailuroedus.service.freemarker;

import freemarker.template.Configuration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ailuroedus.service.TemplateService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreemarkerTemplateService implements TemplateService {
    private final Configuration configuration;

    @Override
    public void template(Path templatePath, Map<String, Object> context, Path output) throws IOException {
        configuration.setDirectoryForTemplateLoading(templatePath.toFile());
        Files.walkFileTree(templatePath, new FreemarkerTemplateVisitor(configuration, templatePath, context, output));
    }
}
