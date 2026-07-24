package contract;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import contract.CodegenRuntimeContract.RuntimeOperation;
import contract.CodegenRuntimeContract.TemplateRequirement;
import java.util.List;
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
