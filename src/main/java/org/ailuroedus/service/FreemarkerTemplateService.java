package org.ailuroedus.service;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ailuroedus.constant.TemplateConstants;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FreemarkerTemplateService implements TemplateService {
    private final Configuration configuration;

    @Override
    public void template(Path templatePath, Map<String, Object> context, Path output) throws IOException {
        configuration.setDirectoryForTemplateLoading(templatePath.toFile());
        Files.walkFileTree(templatePath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                final var replacedPath = templatePath.relativize(file).toString();
                final var template = configuration.getTemplate(replacedPath);
                final var outputPath = output.resolve(replacedPath).toString();
                try (final var outputWriter = new FileWriter(outputPath)) {
                    template.process(context, outputWriter);
                } catch (TemplateException e) {
                    throw new TemplateProcessFailedException(replacedPath, e);
                }

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                final var outputFiles = output.resolve(templatePath.relativize(dir)).toFile().listFiles();
                if (outputFiles == null) {
                    return FileVisitResult.CONTINUE;
                }
                for (final var file : outputFiles) {
                    if (file.length() == 0) {
                        final var isDeleted = file.delete();
                        if (!isDeleted) {
                            // TODO Подумать как правильно обработать удаление файла
                            log.warn("Не удалось удалить файл {}", file);
                        }
                    } else {
                        var isLeaveEmpty = false;
                        try (final var bufferedReader = new BufferedReader(new FileReader(file))) {
                            if (TemplateConstants.SERVICE_RECORD.equals(bufferedReader.readLine())) {
                                isLeaveEmpty = true;
                            }
                        }
                        if (isLeaveEmpty) {
                            try (var ignored = Files.newBufferedWriter(file.toPath(), StandardOpenOption.TRUNCATE_EXISTING)) {
                            }
                        }
                    }
                }

                return FileVisitResult.CONTINUE;
            }
        });
    }
}
