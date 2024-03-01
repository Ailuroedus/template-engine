package org.ailuroedus.service;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class FreemarkerTemplateService implements TemplateService {
    private final Configuration configuration;

    @Override
    public void template(Path templatePath, Map<String, Object> context, Writer output) throws IOException {
        configuration.setDirectoryForTemplateLoading(templatePath.toFile());
        Files.walkFileTree(templatePath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                final var replacedPath = templatePath.relativize(file).toString();
                final var template = configuration.getTemplate(replacedPath);
                try {
                    template.process(context, output);
                } catch (TemplateException e) {
                    throw new TemplateProcessFailedException(replacedPath, e);
                }

                return FileVisitResult.CONTINUE;
            }
        });
    }
}
