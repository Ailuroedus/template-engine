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
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ailuroedus.service.TemplateProcessFailedException;
import org.ailuroedus.util.file.FileUtil;
import org.ailuroedus.util.template.TemplateConstants;
import org.ailuroedus.util.template.TemplateUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class FreemarkerTemplateVisitor extends SimpleFileVisitor<Path> {
    private final Configuration configuration;
    private final Path templatePath;
    private final Map<String, Object> context;
    private final Path output;

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        final var outputPath = output.resolve(templatePath.relativize(dir));
        final var finalPath = templatePathIfNecessary(outputPath, context);
        Files.createDirectories(finalPath);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        final var replacedPath = templatePath.relativize(file);
        final var outputPath = output.resolve(replacedPath);
        final var template = configuration.getTemplate(replacedPath.toString());
        final var finalPath = templatePathIfNecessary(outputPath, context);

        try (final var outputWriter = new FileWriter(finalPath.toFile())) {
            template(template, context, outputWriter);
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        log.info("Приступаем к постобработке директории '{}'", dir);
        final var outputPath = output.resolve(templatePath.relativize(dir));
        final var finalPath = templatePathIfNecessary(outputPath, context);

        try (final var outputWalker = Files.walk(finalPath, 1)) {
            outputWalker.forEach(p -> {
                try {
                    if (Files.isDirectory(p) && FileUtil.isEmptyDir(p)) {
                        log.info("Обнаружена пустая директория: {}", p);
                        Files.deleteIfExists(p);
                    } else if (Files.isRegularFile(p)) {
                        if (Files.size(p) == 0) {
                            log.info("Обнаружен пустой файл: {}", p);
                            Files.deleteIfExists(p);
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

        log.info("Постобработка директории '{}' завершена", dir);
        return FileVisitResult.CONTINUE;
    }


    protected Path templatePathIfNecessary(Path path, Map<String, Object> context) throws IOException {
        if (TemplateUtil.shouldTemplatePath(path)) {
            final var pathTemplate
                    = new Template(path.getFileName().toString(), path.toString(), configuration);
            try (final var out = new StringWriter()) {
                template(pathTemplate, context, out);
                return Paths.get(out.toString());
            }
        }

        return path;
    }

    protected void template(Template template, Map<String, Object> context, Writer writer) {
        try {
            template.process(context, writer);
        } catch (TemplateException e) {
            throw new TemplateProcessFailedException(template.getName(), e);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
