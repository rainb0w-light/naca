package contract;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import contract.CodegenRuntimeContract.RuntimeOperation;
import contract.CodegenRuntimeContract.TemplateRequirement;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * templateRuntimeContractCheck (Codegen-Runtime Contract, stage 1/3): the
 * template side of the boundary. Verifies that
 * <ul>
 *   <li>every declared operation is well-formed (id, capability, runtime
 *       class+method present) and ids are unique;</li>
 *   <li>every production template's required operations resolve to operations
 *       declared in {@code runtime-operations.yaml} — so a template can never
 *       silently emit a runtime call the contract doesn't define;</li>
 *   <li>the two contract files agree on the contract version.</li>
 * </ul>
 * The runtime side (every declared operation has a real naca-rt implementation)
 * is verified by {@code RuntimeContractTest} in naca-cloud-native, which can see
 * naca-rt.
 */
class TemplateRuntimeContractTest
{
    private static final Pattern TEMPLATE = Pattern.compile(
        "(?ms)^(\\w+)\\([^\\n]*\\)\\s*::=\\s*<<(.*?)^>>\\s*$");
    private static final Pattern INLINE_TEMPLATE = Pattern.compile(
        "(?m)^(\\w+)\\([^\\n]*\\)\\s*::=\\s*\"(.*)\"\\s*$");
    private static final Pattern STATIC_CALL = Pattern.compile("\\b(?:CESM|tools)\\.(\\w+)\\s*\\(");
    private static final Pattern FLUENT_CALL = Pattern.compile("\\.(\\w+)\\s*\\(");
    private final CodegenRuntimeContract contract = CodegenRuntimeContract.load();

    @Test
    @DisplayName("contract loads with a positive version and at least one operation")
    void contractLoads()
    {
        assertTrue(contract.contractVersion() >= 1, "contractVersion must be >= 1");
        assertFalse(contract.operations().isEmpty(), "contract must declare operations");
    }

    @Test
    @DisplayName("every declared operation is well-formed and uniquely identified")
    void operationsAreWellFormed()
    {
        for (RuntimeOperation op : contract.operations().values())
        {
            assertFalse(op.id().isBlank(), "operation id must not be blank");
            assertFalse(op.capability().isBlank(), "operation " + op.id() + " missing capability");
            assertFalse(op.runtimeClass().isBlank(), "operation " + op.id() + " missing runtimeClass");
            assertFalse(op.runtimeMethod().isBlank(), "operation " + op.id() + " missing runtimeMethod");
            assertTrue(op.since() >= 1, "operation " + op.id() + " since must be >= 1");
        }
    }

    @Test
    @DisplayName("every template requirement resolves to a declared operation")
    void everyTemplateRequirementResolves()
    {
        assertFalse(contract.templateRequirements().isEmpty(),
            "at least one template must declare runtime requirements");
        for (TemplateRequirement req : contract.templateRequirements().values())
        {
            assertFalse(req.capability().isBlank(),
                "template " + req.template() + " missing capability");
            for (String opId : req.requires())
            {
                assertTrue(contract.operations().containsKey(opId),
                    "template " + req.template() + " requires undeclared operation '" + opId
                        + "' (add it to runtime-operations.yaml)");
            }
        }
    }

    @Test
    @DisplayName("every literal runtime call emitted by a contracted template is declared")
    void everyTemplateRuntimeCallIsDeclared() throws IOException
    {
        String source;
        try (InputStream in = getClass().getResourceAsStream("/templates/java/java.stg"))
        {
            assertNotNull(in, "java.stg must be on the test classpath");
            source = new String(in.readAllBytes(), StandardCharsets.ISO_8859_1);
        }

        java.util.Map<String, String> bodies = new java.util.HashMap<>();
        Matcher templates = TEMPLATE.matcher(source);
        while (templates.find())
        {
            bodies.put(templates.group(1), templates.group(2));
        }
        Matcher inlineTemplates = INLINE_TEMPLATE.matcher(source);
        while (inlineTemplates.find())
        {
            bodies.put(inlineTemplates.group(1), inlineTemplates.group(2));
        }

        for (TemplateRequirement req : contract.templateRequirements().values())
        {
            String body = bodies.get(req.template());
            assertNotNull(body, "contracted template not found in java.stg: " + req.template());
            Set<String> emittedMethods = new HashSet<>();
            collectMethods(STATIC_CALL.matcher(body), emittedMethods);
            collectMethods(FLUENT_CALL.matcher(body), emittedMethods);

            Set<String> declaredMethods = new HashSet<>();
            for (String operationId : req.requires())
            {
                declaredMethods.add(contract.operations().get(operationId).runtimeMethod());
            }
            assertTrue(declaredMethods.containsAll(emittedMethods),
                "template " + req.template() + " emits undeclared runtime methods "
                    + difference(emittedMethods, declaredMethods));
        }
    }

    @Test
    @DisplayName("dynamic SEND MAP and INQUIRE branches declare all overload families")
    void dynamicCicsBranchesAreExplicitlyContracted()
    {
        assertTrue(contract.templateRequirements().get("recursiveCICSSendMapEntity").requires()
            .containsAll(List.of("cics.sendMap.dataFrom", "cics.sendMap.dataFromWithLength",
                "cics.sendMap.dataOnlyFrom", "cics.sendMap.dataOnlyFromWithLength")));
        assertTrue(contract.templateRequirements().get("recursiveCICSInquireEntity").requires()
            .containsAll(List.of("cics.inquire.resolveProgramVars",
                "cics.inquire.resolveProgramLiteral", "cics.inquire.transactionString")));
    }

    private static void collectMethods(Matcher matcher, Set<String> methods)
    {
        while (matcher.find())
        {
            methods.add(matcher.group(1));
        }
    }

    private static Set<String> difference(Set<String> left, Set<String> right)
    {
        Set<String> result = new HashSet<>(left);
        result.removeAll(right);
        return result;
    }

    @Test
    @DisplayName("READ is the first contract proof: recursiveReadFileEntity requires the READ operations")
    void readTemplateIsUnderContract()
    {
        TemplateRequirement read = contract.templateRequirements().get("recursiveReadFileEntity");
        assertNotNull(read, "recursiveReadFileEntity must be under contract");
        assertTrue(read.requires().containsAll(List.of("file.read", "file.readInto", "ioResult.atEnd")),
            "recursiveReadFileEntity must require file.read/file.readInto/ioResult.atEnd; got "
                + read.requires());
    }
}
