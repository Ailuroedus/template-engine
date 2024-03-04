package org.ailuroedus.util.template;

import java.nio.file.Path;

public final class TemplateUtil {
    public static boolean shouldTemplatePath(Path path) {
        return TemplateConstants.TEMPLATE_PATTERN.matcher(path.toString()).find();
    }
}
