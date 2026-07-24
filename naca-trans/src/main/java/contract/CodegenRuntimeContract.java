package contract;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

/**
 * Loads and exposes the versioned Codegen-Runtime Contract: the set of runtime
 * operations the Java backend may emit ({@code runtime-operations.yaml}) and the
 * runtime operations each production template requires
 * ({@code template-runtime-requirements.yaml}).
 *
 * <p>This is the stable boundary between the ST4 backend and naca-rt. Build gates
 * verify the two registries stay consistent (template requirements resolve to
 * declared operations; declared operations have real runtime implementations).
 * The model is deliberately plain data so it can later move into a standalone
 * {@code naca-codegen-contract} module without logic changes.
 */
public final class CodegenRuntimeContract
{
    /** A runtime operation the backend may emit, keyed by a stable id. */
    public static final class RuntimeOperation
    {
        private final String id;
        private final int since;
        private final String capability;
        private final String runtimeClass;
        private final String runtimeMethod;
        private final List<String> paramTypes;
        private final String returns;

        RuntimeOperation(String id, int since, String capability, String runtimeClass,
            String runtimeMethod, List<String> paramTypes, String returns)
        {
            this.id = id;
            this.since = since;
            this.capability = capability;
            this.runtimeClass = runtimeClass;
            this.runtimeMethod = runtimeMethod;
            this.paramTypes = Collections.unmodifiableList(paramTypes);
            this.returns = returns;
        }

        public String id() { return id; }
        public int since() { return since; }
        public String capability() { return capability; }
        public String runtimeClass() { return runtimeClass; }
        public String runtimeMethod() { return runtimeMethod; }
        public List<String> paramTypes() { return paramTypes; }
        public String returns() { return returns; }
    }

    /** The runtime operations a single template requires. */
    public static final class TemplateRequirement
    {
        private final String template;
        private final String capability;
        private final List<String> requires;

        TemplateRequirement(String template, String capability, List<String> requires)
        {
            this.template = template;
            this.capability = capability;
            this.requires = Collections.unmodifiableList(requires);
        }

        public String template() { return template; }
        public String capability() { return capability; }
        public List<String> requires() { return requires; }
    }

    private final int contractVersion;
    private final Map<String, RuntimeOperation> operations;
    private final Map<String, TemplateRequirement> templateRequirements;

    private CodegenRuntimeContract(int contractVersion,
        Map<String, RuntimeOperation> operations,
        Map<String, TemplateRequirement> templateRequirements)
    {
        this.contractVersion = contractVersion;
        this.operations = Collections.unmodifiableMap(operations);
        this.templateRequirements = Collections.unmodifiableMap(templateRequirements);
    }

    public int contractVersion() { return contractVersion; }
    public Map<String, RuntimeOperation> operations() { return operations; }
    public Map<String, TemplateRequirement> templateRequirements() { return templateRequirements; }

    /** Loads the contract from the classpath ({@code /contract/*.yaml}). */
    public static CodegenRuntimeContract load()
    {
        Map<String, Object> opsDoc = loadYaml("/contract/runtime-operations.yaml");
        Map<String, Object> reqDoc = loadYaml("/contract/template-runtime-requirements.yaml");

        int version = ((Number) opsDoc.getOrDefault("contractVersion", 1)).intValue();

        Map<String, RuntimeOperation> operations = new LinkedHashMap<>();
        for (Object entry : list(opsDoc.get("operations")))
        {
            Map<?, ?> op = (Map<?, ?>) entry;
            String id = str(op.get("id"));
            RuntimeOperation operation = new RuntimeOperation(
                id,
                op.get("since") instanceof Number ? ((Number) op.get("since")).intValue() : 1,
                str(op.get("capability")),
                str(op.get("runtimeClass")),
                str(op.get("runtimeMethod")),
                stringList(op.get("paramTypes")),
                str(op.get("returns")));
            operations.put(id, operation);
        }

        Map<String, TemplateRequirement> requirements = new LinkedHashMap<>();
        Map<?, ?> templates = (Map<?, ?>) reqDoc.getOrDefault("templates", Collections.emptyMap());
        for (Map.Entry<?, ?> entry : templates.entrySet())
        {
            String template = String.valueOf(entry.getKey());
            Map<?, ?> spec = (Map<?, ?>) entry.getValue();
            requirements.put(template, new TemplateRequirement(
                template,
                str(spec.get("capability")),
                stringList(spec.get("requires"))));
        }

        return new CodegenRuntimeContract(version, operations, requirements);
    }

    private static Map<String, Object> loadYaml(String resource)
    {
        try (InputStream in = CodegenRuntimeContract.class.getResourceAsStream(resource))
        {
            if (in == null)
            {
                throw new IllegalStateException("Contract resource not on classpath: " + resource);
            }
            Object loaded = new Yaml().load(in);
            if (!(loaded instanceof Map))
            {
                throw new IllegalStateException("Contract resource is not a YAML mapping: " + resource);
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) loaded;
            return map;
        }
        catch (java.io.IOException e)
        {
            throw new IllegalStateException("Failed to read contract resource: " + resource, e);
        }
    }

    private static List<?> list(Object value)
    {
        return value instanceof List ? (List<?>) value : Collections.emptyList();
    }

    private static List<String> stringList(Object value)
    {
        List<String> result = new ArrayList<>();
        for (Object item : list(value))
        {
            result.add(String.valueOf(item));
        }
        return result;
    }

    private static String str(Object value)
    {
        return value == null ? "" : String.valueOf(value);
    }
}
