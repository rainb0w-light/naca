package contract;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import contract.FeatureRegistry.Feature;
import generate.templates.TemplateLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Verifies the Feature Contract Registry is internally consistent and its layers
 * are real and linked:
 * <ul>
 *   <li>feature ids are unique and well-formed (dialects, semantic type, backend
 *       role+template, status present);</li>
 *   <li>each feature's {@code semanticType} resolves to a real class;</li>
 *   <li>each feature's {@code javaBackend.template} exists in the ST4 group;</li>
 *   <li>each feature's {@code runtimeOperations} are a subset of the operations
 *       declared in the Codegen-Runtime Contract (the link to Phase B).</li>
 * </ul>
 * A half-built feature (parser supports it but a layer is missing) fails here,
 * making "supported in name only" visible instead of silent.
 */
class FeatureRegistryTest
{
    private final FeatureRegistry registry = FeatureRegistry.load();
    private final CodegenRuntimeContract contract = CodegenRuntimeContract.load();

    @Test
    @DisplayName("registry loads with a positive version and at least one feature")
    void registryLoads()
    {
        assertTrue(registry.registryVersion() >= 1, "registryVersion must be >= 1");
        assertFalse(registry.features().isEmpty(), "registry must declare features");
    }

    @Test
    @DisplayName("every feature is well-formed and uniquely identified")
    void featuresAreWellFormed()
    {
        for (Feature feature : registry.features().values())
        {
            assertFalse(feature.id().isBlank(), "feature id must not be blank");
            assertFalse(feature.dialects().isEmpty(), "feature " + feature.id() + " missing dialects");
            assertFalse(feature.semanticType().isBlank(), "feature " + feature.id() + " missing semanticType");
            assertFalse(feature.javaBackend().role().isBlank(), "feature " + feature.id() + " missing backend role");
            assertFalse(feature.javaBackend().template().isBlank(), "feature " + feature.id() + " missing backend template");
            assertFalse(feature.status().isBlank(), "feature " + feature.id() + " missing status");
        }
    }

    @Test
    @DisplayName("every feature's semantic type resolves to a real class")
    void semanticTypesResolve()
    {
        for (Feature feature : registry.features().values())
        {
            try
            {
                Class.forName(feature.semanticType());
            }
            catch (ClassNotFoundException e)
            {
                throw new AssertionError(
                    "feature " + feature.id() + " semanticType does not resolve: "
                        + feature.semanticType(), e);
            }
        }
    }

    @Test
    @DisplayName("every feature's backend template exists in the ST4 group")
    void backendTemplatesExist()
    {
        for (Feature feature : registry.features().values())
        {
            String template = feature.javaBackend().template();
            assertTrue(TemplateLoader.hasTemplate(template),
                "feature " + feature.id() + " backend template not defined in STG: " + template);
        }
    }

    @Test
    @DisplayName("every feature's runtime operations are declared in the Codegen-Runtime Contract")
    void runtimeOperationsLinkToContract()
    {
        for (Feature feature : registry.features().values())
        {
            for (String opId : feature.runtimeOperations())
            {
                assertTrue(contract.operations().containsKey(opId),
                    "feature " + feature.id() + " references operation '" + opId
                        + "' not declared in runtime-operations.yaml");
            }
        }
    }

    @Test
    @DisplayName("READ feature is registered end-to-end")
    void readFeatureIsRegistered()
    {
        Feature read = registry.features().get("cobol.file.read");
        assertNotNull(read, "cobol.file.read must be registered");
        assertTrue(read.semanticType().equals("semantic.Verbs.CEntityReadFile"));
        assertTrue(read.javaBackend().template().equals("recursiveReadFileEntity"));
        assertTrue(read.runtimeOperations().contains("file.read"));
    }
}
