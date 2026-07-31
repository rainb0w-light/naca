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
    private static final String DECLARATION_RESOURCE =
        "/templates/java/semantic-declaration-bindings.properties";
    private static final String ROOT_RESOURCE =
        "/templates/java/semantic-root-bindings.properties";
    private static final String FPAC_RESOURCE =
        "/templates/java/semantic-fpac-bindings.properties";

    private final Map<String, String> templateNames;

    private JavaSemanticTemplateBindings(Map<String, String> templateNames)
    {
        this.templateNames = Collections.unmodifiableMap(
            new LinkedHashMap<>(templateNames));
    }

    public static JavaSemanticTemplateBindings loadDefault()
    {
        return load(CONCRETE_RESOURCE, RUNTIME_RESOURCE);
    }

    public static JavaSemanticTemplateBindings loadDeclarations()
    {
        return load(DECLARATION_RESOURCE);
    }

    public static JavaSemanticTemplateBindings loadRoots()
    {
        return load(ROOT_RESOURCE);
    }

    /**
     * Bindings for the independent FPac pipeline's reference rendering. FPac shares
     * the target-neutral semantic model with COBOL, so it reuses the whole shared
     * reference manifest (concrete + runtime aliases) and overrides ONLY the semantic
     * types whose FPac lowering differs from COBOL. The override manifest wins for
     * those keys (e.g. {@code semantic.Verbs.CEntityCallFunction}: COBOL PERFORM vs the
     * FPac direct {@code name();} call); every other FPac-migrated verb (assign,
     * assignWithAccessor, ...) resolves through the shared binding it already reuses.
     */
    public static JavaSemanticTemplateBindings loadFpac()
    {
        Map<String, String> bindings = new LinkedHashMap<>();
        loadResource(bindings, CONCRETE_RESOURCE);
        loadResource(bindings, RUNTIME_RESOURCE);
        loadResourceOverride(bindings, FPAC_RESOURCE);
        return new JavaSemanticTemplateBindings(bindings);
    }

    private static JavaSemanticTemplateBindings load(String... resources)
    {
        Map<String, String> bindings = new LinkedHashMap<>();
        for (String resource : resources)
        {
            loadResource(bindings, resource);
        }
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

    /**
     * Loads a pipeline-specific override manifest whose entries WIN over bindings
     * already present (used by {@link #loadFpac()} to redirect a shared semantic type
     * to a pipeline-specific template). Unlike {@link #loadResource}, a key that is
     * already bound is deliberately replaced rather than rejected.
     */
    private static void loadResourceOverride(Map<String, String> bindings, String resource)
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
            bindings.put(semanticType, templateName);
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
