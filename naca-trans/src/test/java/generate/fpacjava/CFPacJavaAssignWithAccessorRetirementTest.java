package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaFPacEntityFactory;
import generate.java.st.MockDataEntity;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.CEntityEnvironmentVariable;
import semantic.SQL.CEntitySQLCode;
import semantic.Verbs.CEntityAssignWithAccessor;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/**
 * Phase 2 (FPAC) — retirement proof for the former {@code CFPacJavaAssignWithAccessor}
 * direct backend. The FPac pipeline shares the target-neutral
 * {@link CEntityAssignWithAccessor} semantic verb with COBOL, so
 * {@link CJavaFPacEntityFactory#NewEntityAssignWithAccessor(int)} must hand back the same
 * pure {@code CEntityAssignWithAccessor} the COBOL factories build (no
 * {@code generate.fpacjava} subclass). Reference and value are precomputed by the parser
 * ({@code CFPacAssign} calls {@code SetAssign}; {@code CFPacMove} calls {@code SetRefTo} +
 * {@code SetValue}; FPac never sets {@code fillAll}), and rendering reaches the recursive
 * ST4 assembler through the frozen
 * {@code semantic.Verbs.CEntityAssignWithAccessor -> recursiveAssignWithAccessorEntity}
 * binding — the exact production path a lowered FPac accessor assignment takes.
 *
 * <p>The only FPac destination that carries accessors is a {@code CEntityEnvironmentVariable}
 * built from the rules engine (e.g. {@code RETCD}, writer {@code "setReturnCode("}), so the
 * shared template's environment branch lowers to {@code setReturnCode(<value>);}. The deleted
 * backend emitted {@code setReturnCode(<value>.getInt()) ;} — a redundant {@code .getInt()}
 * plus a stray space: {@code nacaLib.basePrgEnv.BaseProgram} (superclass of
 * {@code nacaLib.fpacPrgEnv.FPacProgram}) declares a {@code setReturnCode(Var)} overload that
 * coerces the value internally, so FPac converges onto the canonical shape COBOL already emits
 * for this same semantic entity (asserted COBOL-side in {@code CEntityEnvironmentVariableRenderTest}).
 * The shared template's {@code resetSQLCode} branch is exercised for completeness even though
 * FPac never builds a {@code CEntitySQLCode}.
 */
class CFPacJavaAssignWithAccessorRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String render(CEntityAssignWithAccessor assign)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(assign, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        assertEquals(CEntityAssignWithAccessor.class,
            new CJavaFPacEntityFactory(catalog, null).NewEntityAssignWithAccessor(1).getClass());
    }

    /**
     * The factory-built entity renders through the recursive assembler (not a legacy
     * {@code DoExport}) as {@code setReturnCode(<value>);} for the environment-reference
     * case — the canonical shape the retired backend approximated with a redundant
     * {@code .getInt()} and a stray {@code " ;"}.
     */
    @Test
    void productionBridgeRendersEnvironmentAccessorThroughAssembler()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);

        CEntityAssignWithAccessor assign = factory.NewEntityAssignWithAccessor(1);
        assign.SetRefTo(new CEntityEnvironmentVariable(
            1, "RETCD", catalog, "getReturnCode()", "setReturnCode(", true));
        assign.SetValue(new MockDataEntity(2, "WS-RETCODE"));

        assertEquals("setReturnCode(WS-RETCODE);", render(assign));
    }

    /**
     * The shared template's SQLCode branch (unreachable from FPac, which never builds a
     * {@code CEntitySQLCode}) still lowers through the same pure entity to
     * {@code resetSQLCode(<value>);}, proving the whole shared contract is reachable.
     */
    @Test
    void sqlCodeReferenceRendersResetThroughAssembler()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);

        CEntityAssignWithAccessor assign = factory.NewEntityAssignWithAccessor(1);
        assign.SetRefTo(new CEntitySQLCode("SQLCODE", catalog));
        assign.SetValue(new MockDataEntity(2, "0"));

        assertEquals("resetSQLCode(0);", render(assign));
    }

    /**
     * Mirrors production lowering end-to-end from the parser-facing population sequences:
     * {@code CFPacAssign.DoCustomSemanticAnalysis} builds the verb with
     * {@code factory.NewEntityAssignWithAccessor(line)} then {@code SetAssign(eid, exp)},
     * while {@code CFPacMove} uses {@code SetRefTo(var2)} + {@code SetValue(var1)}. Fed here
     * with the same calls against the FPac factory, the pure {@link CEntityAssignWithAccessor}
     * lowers through the recursive ST4 assembler as {@code setReturnCode(<value>);}, proving
     * the accessor assignment the FPac parser emits is production-reachable and compilable.
     */
    @Test
    void parserFacingAssignWithAccessorLowersThroughFactoryAndAssembler()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);

        // CFPacAssign shape: SetAssign(reference, value)
        CEntityAssignWithAccessor viaAssign = factory.NewEntityAssignWithAccessor(7);
        viaAssign.SetAssign(
            new CEntityEnvironmentVariable(
                7, "RETCD", catalog, "getReturnCode()", "setReturnCode(", true),
            new MockDataEntity(7, "WS-RETCD"));
        assertEquals(CEntityAssignWithAccessor.class, viaAssign.getClass());
        assertEquals("setReturnCode(WS-RETCD);", render(viaAssign));

        // CFPacMove shape: SetRefTo(reference) + SetValue(value)
        CEntityAssignWithAccessor viaMove = factory.NewEntityAssignWithAccessor(9);
        viaMove.SetRefTo(new CEntityEnvironmentVariable(
            9, "RETCD", catalog, "getReturnCode()", "setReturnCode(", true));
        viaMove.SetValue(new MockDataEntity(9, "WS-RETCD"));
        assertEquals("setReturnCode(WS-RETCD);", render(viaMove));
    }
}
