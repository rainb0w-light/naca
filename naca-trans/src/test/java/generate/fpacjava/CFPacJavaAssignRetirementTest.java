package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaFPacEntityFactory;
import generate.java.st.MockDataEntity;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.Verbs.CEntityAssign;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/**
 * Phase 2 (FPAC) — retirement proof for the former {@code CFPacJavaAssign} direct
 * backend. The FPac pipeline shares the target-neutral {@link CEntityAssign} semantic
 * verb with COBOL, so {@link CJavaFPacEntityFactory#NewEntityAssign(int)} must hand back
 * the same pure {@code CEntityAssign} the COBOL factories build (no
 * {@code generate.fpacjava} subclass). Value and destinations are precomputed by the
 * parser/factory ({@code CFPacAssign}/{@code CFPacMove}/{@code CFPacArithmeticOperation}/
 * {@code CFPacConvert} call {@code SetValue} + {@code AddRefTo}; FPac never sets
 * {@code fillAll}/{@code corresponding}), and rendering reaches the recursive ST4
 * assembler through the frozen {@code semantic.Verbs.CEntityAssign -> recursiveMoveEntity}
 * binding — the exact production path a lowered FPac assignment takes.
 *
 * <p>The deleted backend's {@code DoExport()} emitted {@code move(<value>, <ref>) ;} (a
 * stray space before the semicolon) and ignored {@code fillAll}/{@code corresponding}
 * entirely; FPac converges onto the canonical {@code move(<value>, <destination>);} shape
 * COBOL already emits for this same semantic entity. Runtime-legal:
 * {@code nacaLib.fpacPrgEnv.FPacProgram extends nacaLib.basePrgEnv.BaseProgram}, which
 * declares {@code move}.
 */
class CFPacJavaAssignRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String render(CEntityAssign assign)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(assign, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        assertEquals(CEntityAssign.class,
            new CJavaFPacEntityFactory(catalog, null).NewEntityAssign(1).getClass());
    }

    /**
     * The factory-built entity renders through the recursive assembler (not a legacy
     * {@code DoExport}) as {@code move(<value>, <destination>);} — one statement per
     * destination, the canonical shape the retired backend approximated with a stray
     * {@code ") ;"} spacing.
     */
    @Test
    void productionBridgeRendersMoveThroughAssembler()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);

        CEntityAssign single = factory.NewEntityAssign(1);
        single.SetValue(new MockDataEntity(2, "SOURCE"));
        single.AddRefTo(new MockDataEntity(3, "DEST"));
        assertEquals("move(SOURCE, DEST);", render(single));

        CEntityAssign multi = factory.NewEntityAssign(1);
        multi.SetValue(new MockDataEntity(2, "SOURCE"));
        multi.AddRefTo(new MockDataEntity(3, "DEST-A"));
        multi.AddRefTo(new MockDataEntity(4, "DEST-B"));
        assertEquals("move(SOURCE, DEST-A);\nmove(SOURCE, DEST-B);", render(multi));
    }

    /**
     * Mirrors production lowering end-to-end from the parser-facing population sequence:
     * {@code CFPacAssign.DoCustomSemanticAnalysis} builds the verb with
     * {@code factory.NewEntityAssign(line)} then {@code SetValue(exp)} + {@code AddRefTo(eid)}.
     * Fed here with the same calls against the FPac factory, the pure {@link CEntityAssign}
     * lowers through the recursive ST4 assembler as {@code move(<value>, <destination>);},
     * proving the assignment the FPac parser emits is production-reachable and compilable.
     */
    @Test
    void parserFacingAssignLowersThroughFactoryAndAssembler()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);

        CEntityAssign assign = factory.NewEntityAssign(7);
        assign.SetValue(new MockDataEntity(7, "WS-SOURCE"));
        assign.AddRefTo(new MockDataEntity(7, "WS-TARGET"));

        assertEquals(CEntityAssign.class, assign.getClass());
        assertEquals("move(WS-SOURCE, WS-TARGET);", render(assign));
    }
}
