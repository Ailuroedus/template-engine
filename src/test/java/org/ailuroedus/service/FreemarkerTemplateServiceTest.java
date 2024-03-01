package org.ailuroedus.service;

import java.io.IOException;
import org.junit.jupiter.api.Test;

//@SpringBootTest
public class FreemarkerTemplateServiceTest {
    //    @Autowired
    private FreemarkerTemplateService templateService;

//    @Value("classpath:templates/simple")
//    private Resource simpleTemplate;

    @Test
    public void happyPathTemplateTest() throws IOException {
        final var expectedOutputName = "Misha";

//        try (final var output = new StringWriter()) {
////            templateService.template(simpleTemplate.getFile().toPath(), Map.of("name", expectedOutputName), output);
////
////            final var actualOutput = output.toString();
////            assertNotNull(actualOutput);
////            assertFalse(actualOutput.isEmpty());
////            assertTrue(actualOutput.contains(expectedOutputName));
//        }
    }
}
