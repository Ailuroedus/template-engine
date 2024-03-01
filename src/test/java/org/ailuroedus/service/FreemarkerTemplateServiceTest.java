package org.ailuroedus.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@SpringBootTest
public class FreemarkerTemplateServiceTest {
    @Autowired
    private FreemarkerTemplateService templateService;

    @Value("classpath:templates/simple")
    private Resource simpleTemplate;

    @Test
    public void happyPathTemplateTest(@TempDir File tempDir) throws IOException {
        final var expectedOutputName = "Misha";

        templateService.template(
                simpleTemplate.getFile().toPath(),
                Map.of("name", expectedOutputName,
                        "changelogEnabled", false),
                tempDir.toPath()
        );
    }
}
