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
