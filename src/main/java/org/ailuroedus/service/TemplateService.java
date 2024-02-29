package org.ailuroedus.service;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.Map;

public interface TemplateService {
    void template(Path templatePath, Map<String, Object> context, Writer outputWriter) throws IOException;
}
