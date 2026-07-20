package generate.templates.recursive.java;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/** Declarative semantic runtime type to Java STG template bindings. */
public final class JavaSemanticTemplateBindings
{
    private static final String CONCRETE_RESOURCE =
        "/templates/java/semantic-bindings.properties";
    private static final String RUNTIME_RESOURCE =
        "/templates/java/semantic-runtime-bindings.properties";

    private final Map<String, String> templateNames;

    private JavaSemanticTemplateBindings(Map<String, String> templateNames)
    {
        this.templateNames = Collections.unmodifiableMap(
            new LinkedHashMap<>(templateNames));
    }

    public static JavaSemanticTemplateBindings loadDefault()
    {
        Map<String, String> bindings = new LinkedHashMap<>();
        loadResource(bindings, CONCRETE_RESOURCE);
        loadResource(bindings, RUNTIME_RESOURCE);
        return new JavaSemanticTemplateBindings(bindings);
    }

    private static void loadResource(Map<String, String> bindings, String resource)
    {
        Properties properties = new Properties();
        try (InputStream input = JavaSemanticTemplateBindings.class
            .getResourceAsStream(resource))
        {
            if (input == null)
            {
                throw new IllegalStateException("Missing template binding resource: " + resource);
            }
            properties.load(input);
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Cannot load template bindings: " + resource, e);
        }

        for (String semanticType : properties.stringPropertyNames())
        {
            String templateName = properties.getProperty(semanticType).trim();
            if (templateName.isEmpty())
            {
                throw new IllegalStateException(
                    "Empty template binding for semantic type: " + semanticType);
            }
            if (bindings.putIfAbsent(semanticType, templateName) != null)
            {
                throw new IllegalStateException(
                    "Duplicate template binding across manifests: " + semanticType);
            }
        }
    }

    public String findTemplateName(Class<?> semanticType)
    {
        Objects.requireNonNull(semanticType, "semanticType");
        for (Class<?> type = semanticType; type != null; type = type.getSuperclass())
        {
            String templateName = templateNames.get(type.getName());
            if (templateName != null)
            {
                return templateName;
            }
        }
        return null;
    }
}
