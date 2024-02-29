package org.ailuroedus.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class FreemarkerTemplateServiceTest {
    @Autowired
    private FreemarkerTemplateService templateService;

    @Value("classpath:templates/simple")
    private Resource simpleTemplate;

    @Test
    public void happyPathTemplateTest() throws IOException {
        final var expectedOutputName = "Misha";

        try (final var output = new StringWriter()) {
            templateService.template(simpleTemplate.getFile().toPath(), Map.of("name", expectedOutputName), output);

            final var actualOutput = output.toString();
            assertNotNull(actualOutput);
            assertFalse(actualOutput.isEmpty());
            assertTrue(actualOutput.contains(expectedOutputName));
        }
    }
}
