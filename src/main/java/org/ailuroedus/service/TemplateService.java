package org.ailuroedus.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public interface TemplateService {
    void template(Path templatePath, Map<String, Object> context, Path output) throws IOException;
}
