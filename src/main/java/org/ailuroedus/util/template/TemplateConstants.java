package org.ailuroedus.util.template;

import java.util.regex.Pattern;

public final class TemplateConstants {
    public static final String SERVICE_RECORD = "___!AILUROEDUS!___";
    public static final String CREATE_EMPTY_IF_METHOD = "createEmptyIf";
    public static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\$\\{.*}");
}
