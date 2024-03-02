package org.ailuroedus.util;

import org.ailuroedus.constant.TemplateConstants;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class FileUtil {
    public static String readFirstSymbols(Path file, int length) {
        try (final var outputPathReader = FileChannel.open(file, StandardOpenOption.READ)) {
            final var buffer = ByteBuffer.allocate(TemplateConstants.SERVICE_RECORD.length());
            outputPathReader.read(buffer);
            return new String(buffer.array(), Charset.defaultCharset());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static boolean isEmptyDir(Path dir) {
        try (var entries = Files.list(dir)) {
            return entries.findAny().isEmpty();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void truncateFile(Path file) {
        try {
            Files.write(file, new byte[0], StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
