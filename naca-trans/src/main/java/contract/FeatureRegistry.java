package contract;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

/**
 * Loads and exposes the Feature Contract Registry: the machine-checkable
 * capability matrix mapping every supported language feature across all layers
 * (dialect syntax -> semantic type -> backend binding/template -> runtime
 * operations -> conformance fixtures). See {@code feature-registry.yaml}.
 *
 * <p>This is the unified extension path: adding a new dialect feature means
 * adding one entry and filling in each layer. {@code FeatureRegistryTest}
 * verifies the layers are real (semantic type and template exist) and linked to
 * the Codegen-Runtime Contract (runtime operations are declared). Plain data so
 * it can later move into a standalone module without logic changes.
 */
public final class FeatureRegistry
{
    /** The backend binding a feature renders through. */
    public static final class JavaBackend
    {
        private final String role;
        private final String template;

        JavaBackend(String role, String template)
        {
            this.role = role;
            this.template = template;
        }

        public String role() { return role; }
        public String template() { return template; }
    }

    /** One supported feature and the artifacts it spans across layers. */
    public static final class Feature
    {
        private final String id;
        private final List<String> dialects;
        private final String semanticType;
        private final JavaBackend javaBackend;
        private final List<String> runtimeOperations;
        private final List<String> fixtures;
        private final String status;

        Feature(String id, List<String> dialects, String semanticType, JavaBackend javaBackend,
            List<String> runtimeOperations, List<String> fixtures, String status)
        {
            this.id = id;
            this.dialects = Collections.unmodifiableList(dialects);
            this.semanticType = semanticType;
            this.javaBackend = javaBackend;
            this.runtimeOperations = Collections.unmodifiableList(runtimeOperations);
            this.fixtures = Collections.unmodifiableList(fixtures);
            this.status = status;
        }

        public String id() { return id; }
        public List<String> dialects() { return dialects; }
        public String semanticType() { return semanticType; }
        public JavaBackend javaBackend() { return javaBackend; }
        public List<String> runtimeOperations() { return runtimeOperations; }
        public List<String> fixtures() { return fixtures; }
        public String status() { return status; }
    }

    private final int registryVersion;
    private final Map<String, Feature> features;

    private FeatureRegistry(int registryVersion, Map<String, Feature> features)
    {
        this.registryVersion = registryVersion;
        this.features = Collections.unmodifiableMap(features);
    }

    public int registryVersion() { return registryVersion; }
    public Map<String, Feature> features() { return features; }

    /** Loads the registry from the classpath ({@code /contract/feature-registry.yaml}). */
    public static FeatureRegistry load()
    {
        Map<String, Object> doc = loadYaml("/contract/feature-registry.yaml");
        int version = doc.get("registryVersion") instanceof Number
            ? ((Number) doc.get("registryVersion")).intValue() : 1;

        Map<String, Feature> features = new LinkedHashMap<>();
        for (Object entry : list(doc.get("features")))
        {
            Map<?, ?> f = (Map<?, ?>) entry;
            Map<?, ?> backend = (Map<?, ?>) f.get("javaBackend");
            Feature feature = new Feature(
                str(f.get("id")),
                stringList(f.get("dialects")),
                str(f.get("semanticType")),
                new JavaBackend(str(backend.get("role")), str(backend.get("template"))),
                stringList(f.get("runtimeOperations")),
                stringList(f.get("fixtures")),
                str(f.get("status")));
            features.put(feature.id(), feature);
        }
        return new FeatureRegistry(version, features);
    }

    private static Map<String, Object> loadYaml(String resource)
    {
        try (InputStream in = FeatureRegistry.class.getResourceAsStream(resource))
        {
            if (in == null)
            {
                throw new IllegalStateException("Feature registry not on classpath: " + resource);
            }
            Object loaded = new Yaml().load(in);
            if (!(loaded instanceof Map))
            {
                throw new IllegalStateException("Feature registry is not a YAML mapping: " + resource);
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) loaded;
            return map;
        }
        catch (java.io.IOException e)
        {
            throw new IllegalStateException("Failed to read feature registry: " + resource, e);
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
