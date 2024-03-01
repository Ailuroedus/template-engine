package org.ailuroedus.service;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ailuroedus.constant.TemplateConstants;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
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
                try (final var outputPath = Files.walk(output.resolve(templatePath.relativize(dir)))) {
                    log.info("Приступаем к постобработке файла {}", outputPath);
                    outputPath.filter(Files::isRegularFile).forEach(p -> {
                        try {
                            if (Files.size(p) == 0) {
                                Files.delete(p);
                            } else {
                                var shouldCleanFile = false;

                                try (final var outputPathReader = FileChannel.open(p, StandardOpenOption.READ)) {
                                    final var buffer = ByteBuffer.allocate(TemplateConstants.SERVICE_RECORD.length());
                                    outputPathReader.read(buffer);
                                    if (TemplateConstants.SERVICE_RECORD
                                            .equals(new String(buffer.array(), Charset.defaultCharset()))) {
                                        shouldCleanFile = true;
                                    }
                                }

                                if (shouldCleanFile) {
                                    Files.write(p, new byte[0], StandardOpenOption.TRUNCATE_EXISTING);
                                }
                            }
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
                }

                return FileVisitResult.CONTINUE;
            }
        });
    }
}
