package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaFPacEntityFactory;
import generate.LegacyDataRenderer;
import parser.expression.CAddressTerminal;
import semantic.CDataEntity;
import semantic.expression.CEntityAddress;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/**
 * Phase 2 (FPAC) — retirement proof for the former {@code CFPacJavaAddress} direct
 * backend. The FPac pipeline shares the target-neutral semantic expression model, so
 * {@link CJavaFPacEntityFactory#NewEntityAddress(String)} must hand back a pure
 * {@link CEntityAddress} (no {@code generate.fpacjava} subclass). The address literal
 * is fully resolved while the factory populates the entity, and rendering reaches the
 * recursive ST4 assembler ({@code addressExpressionEntity}) through the
 * {@link LegacyDataRenderer} bridge — the exact production path a lowered FPac address
 * terminal takes: {@code CFPacJavaExprTerminal.Export()} calls
 * {@code LegacyDataRenderer.renderReference(term, line)}, and because the pure semantic
 * entity declares no {@code ExportReference} override the bridge falls through to the
 * assembler. The deleted backend's {@code ExportReference(nLine)} returned exactly
 * {@code csAddress}, which the {@code <entity.address>} template reproduces verbatim.
 */
class CFPacAddressRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        assertEquals(CEntityAddress.class,
            new CJavaFPacEntityFactory(catalog, null).NewEntityAddress("MYVAR").getClass());
    }

    @Test
    void productionBridgeRendersResolvedAddressThroughAssembler()
    {
        CEntityAddress address =
            new CJavaFPacEntityFactory(catalog, null).NewEntityAddress("MYVAR");
        assertEquals("MYVAR", LegacyDataRenderer.renderReference(address, address.getLine()));
    }

    /**
     * Mirrors production lowering end-to-end from the parser node: the FPac parser
     * builds a {@link CAddressTerminal}, {@code GetDataEntity} resolves it through the
     * FPac factory into a pure {@link CEntityAddress}, and the legacy data-renderer
     * bridge (the call {@code CFPacJavaExprTerminal.Export()} makes) renders the
     * resolved address through the recursive ST4 assembler.
     */
    @Test
    void parserAddressTerminalLowersThroughFactoryAndAssembler()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CDataEntity address = new CAddressTerminal("CUST-ADDR").GetDataEntity(1, factory);
        assertEquals(CEntityAddress.class, address.getClass());
        assertEquals("CUST-ADDR", LegacyDataRenderer.renderReference(address, 1));
    }
}
