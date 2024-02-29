package org.ailuroedus.service;

public class TemplateProcessFailedException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "Шаблонизация файла '%s' провалена";

    public TemplateProcessFailedException(String templateName, Exception cause) {
        super(MESSAGE_TEMPLATE.formatted(templateName), cause);
    }
}
