package org.ailuroedus.service;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ailuroedus.constant.TemplateConstants;
import org.ailuroedus.util.FileUtil;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
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
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                final var replacedPath = templatePath.relativize(dir);
                final var outputPath = output.resolve(replacedPath);
                Files.createDirectories(outputPath);

                return FileVisitResult.CONTINUE;
            }

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
                log.info("Приступаем к постобработке директории '{}'", dir);
                try (final var outputPath = Files.walk(output.resolve(templatePath.relativize(dir)), 1)) {
                    outputPath.forEach(p -> {
                        try {
                            if (Files.isDirectory(p) && FileUtil.isEmptyDir(p)) {
                                log.info("Обнаружена пустая директория: {}", p);
                                Files.delete(p);
                            } else if (Files.isRegularFile(p)) {
                                if (Files.size(p) == 0) {
                                    log.info("Обнаружен пустой файл: {}", p);
                                    Files.delete(p);
                                } else {
                                    final var firstFileSymbols
                                            = FileUtil.readFirstSymbols(p, TemplateConstants.SERVICE_RECORD.length());
                                    if (TemplateConstants.SERVICE_RECORD.equals(firstFileSymbols)) {
                                        log.info("Обнаружен файл c служебной записью: {}", p);
                                        FileUtil.truncateFile(p);
                                    }
                                }
                            }
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
                }
                log.info("Постобработко директории '{}' завершена", dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
