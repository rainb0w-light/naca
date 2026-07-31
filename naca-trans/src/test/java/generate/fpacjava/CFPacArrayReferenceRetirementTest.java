package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaFPacEntityFactory;
import generate.LegacyDataRenderer;
import java.util.Vector;
import parser.expression.CExpression;
import parser.expression.CNumberTerminal;
import parser.expression.CTermExpression;
import semantic.CDataEntity;
import semantic.CEntityArrayReference;
import semantic.CEntityStructure;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityExprTerminal;
import semantic.expression.CEntityNumber;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/**
 * Phase 2 (FPAC) — retirement proof for the former {@code CFPacJavaArrayReference}
 * direct backend. The FPac pipeline shares the target-neutral semantic data model
 * with COBOL, so {@link CJavaFPacEntityFactory#NewEntityArrayReference(int)} must hand
 * back the same pure {@link CEntityArrayReference} the COBOL factory builds (no
 * {@code generate.fpacjava} subclass). Reference and indexes are populated by the
 * parser/factory ({@code CEntityStructure.GetArrayReference} → {@code SetReference} +
 * {@code AddIndex}), and rendering reaches the recursive ST4 assembler through the
 * {@link LegacyDataRenderer} bridge: because the pure semantic entity declares no
 * {@code ExportReference} override, the bridge falls through to the existing
 * {@code arrayReferenceEntity} binding ({@code <reference>.getAt(<indexes>)}).
 *
 * <p>The deleted backend's {@code ExportReference(nLine)} emitted
 * {@code reference(idx1, idx2)} — a field invoked with {@code ()}, which is not valid
 * Java array access. The canonical, compilable form is the naca-rt {@code .getAt(...)}
 * accessor COBOL already emits for this exact semantic entity; FPac converges onto it.
 */
class CFPacArrayReferenceRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        assertEquals(CEntityArrayReference.class,
            new CJavaFPacEntityFactory(catalog, null).NewEntityArrayReference(1).getClass());
    }

    /**
     * The factory-built entity renders through the recursive assembler (not a legacy
     * {@code ExportReference}) as {@code <reference>.getAt(<index0>, <index1>)} — the
     * {@code ", "} separator reproduces the retired backend's index spacing, and the
     * {@code .getAt} accessor replaces its invalid {@code reference(...)} call form.
     */
    @Test
    void productionBridgeRendersArrayReferenceThroughAssembler()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityStructure table = new CEntityStructure(1, "MY-TABLE", catalog, "01");
        CEntityArrayReference arrayRef = factory.NewEntityArrayReference(1);
        arrayRef.SetReference(table);
        arrayRef.AddIndex(number("2"));
        arrayRef.AddIndex(number("5"));

        String rendered = LegacyDataRenderer.renderReference(arrayRef, 1);
        String reference = LegacyDataRenderer.renderReference(table, 1);
        assertEquals(reference + ".getAt(2, 5)", rendered);
        assertTrue(rendered.contains(".getAt("),
            "array reference must lower to the naca-rt .getAt accessor: " + rendered);
    }

    /**
     * Mirrors production lowering end-to-end from the parser-facing semantic node:
     * {@link CEntityStructure#GetArrayReference(Vector, semantic.CBaseEntityFactory)}
     * is the method the FPac parser calls for a subscripted table reference, fed here
     * with the real concrete parser expression a constant subscript parses into
     * ({@link CTermExpression} wrapping a {@link CNumberTerminal}). It resolves through
     * the FPac factory into a pure {@link CEntityArrayReference}, and the legacy
     * data-renderer bridge renders it through the recursive ST4 assembler as
     * {@code <reference>.getAt(<index>)}.
     */
    @Test
    void parserArrayReferenceLowersThroughFactoryAndAssembler()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityStructure table = new CEntityStructure(1, "MY-TABLE", catalog, "01");
        Vector<CExpression> subscripts = new Vector<>();
        subscripts.add(new CTermExpression(1, new CNumberTerminal("3")));

        CDataEntity arrayRef = table.GetArrayReference(subscripts, factory);
        assertEquals(CEntityArrayReference.class, arrayRef.getClass());

        String rendered = LegacyDataRenderer.renderReference(arrayRef, 1);
        String reference = LegacyDataRenderer.renderReference(table, 1);
        assertTrue(rendered.startsWith(reference + ".getAt("),
            "array reference must lower to <reference>.getAt(...): " + rendered);
        assertTrue(rendered.endsWith(")"), rendered);
        assertTrue(rendered.contains("3"),
            "the constant subscript must survive lowering: " + rendered);
    }

    private CBaseEntityExpression number(String value)
    {
        return new CEntityExprTerminal(new CEntityNumber(catalog, value)) {};
    }
}
