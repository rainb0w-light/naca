package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaFPacEntityFactory;
import generate.LegacyDataRenderer;
import parser.expression.CSumExpression;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityExprSum;
import semantic.expression.CEntityExprTerminal;
import semantic.expression.CEntityNumber;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/**
 * Phase 2 (FPAC) — retirement proof for the former {@code CFPacExprSum} direct
 * backend. The FPac pipeline shares the target-neutral semantic expression model,
 * so {@link CJavaFPacEntityFactory#NewEntityExprSum()} must hand back a pure
 * {@link CEntityExprSum} (no {@code generate.fpacjava} subclass). Its operands are
 * precomputed by the parser, and rendering reaches the recursive ST4 assembler
 * ({@code recursiveExprSumEntity}) through the {@link LegacyDataRenderer} bridge —
 * the exact production path a lowered FPac sum expression takes. The deleted
 * backend only carried a dead {@code Export()} override that no code invoked.
 */
class CFPacExprSumRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        assertEquals(CEntityExprSum.class,
            new CJavaFPacEntityFactory(catalog, null).NewEntityExprSum().getClass());
    }

    @Test
    void productionBridgeRendersAddAndSubtractThroughAssembler()
    {
        assertEquals("add(2, \n3)", lower(CSumExpression.CSumType.ADD));
        assertEquals("subtract(2, \n3)", lower(CSumExpression.CSumType.SUB));
    }

    /**
     * Mirrors production lowering: the FPac factory builds the sum, the parser
     * populates its operands, and the legacy data-renderer bridge resolves the
     * reference. Because the pure semantic entity declares no {@code ExportReference}
     * override, the bridge falls through to the recursive ST4 assembler.
     */
    private String lower(CSumExpression.CSumType type)
    {
        CEntityExprSum sum = new CJavaFPacEntityFactory(catalog, null).NewEntityExprSum();
        sum.SetSumExpression(number("2"), number("3"), type);
        return LegacyDataRenderer.renderReference(sum, sum.getLine());
    }

    private CBaseEntityExpression number(String value)
    {
        return new CEntityExprTerminal(new CEntityNumber(catalog, value)) {};
    }
}
