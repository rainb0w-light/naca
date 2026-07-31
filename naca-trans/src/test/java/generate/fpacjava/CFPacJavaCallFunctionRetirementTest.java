package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaFPacEntityFactory;
import generate.LegacyLanguageRenderer;
import generate.java.st.MockJavaExporter;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.Verbs.CEntityCallFunction;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/**
 * Phase 2 (FPAC) — retirement proof for the former {@code CFPacJavaCallFunction} direct
 * backend. The FPac pipeline shares the target-neutral {@link CEntityCallFunction} semantic
 * verb with COBOL, but its lowering genuinely differs: COBOL renders this class as a PERFORM
 * ({@code recursiveCallFunctionEntity}, REFERENCE role), whereas an FPac {@code DOSUBR} is a
 * DIRECT call to the generated paragraph method, {@code name() ;}. {@link
 * CJavaFPacEntityFactory#NewEntityCallFunction} therefore hands back the same pure {@code
 * CEntityCallFunction} the COBOL factories build (no {@code generate.fpacjava} subclass), and
 * the independent {@code FPAC_REFERENCE} role layers {@code semantic-fpac-bindings.properties}
 * over the shared reference manifest to redirect ONLY this class to
 * {@code recursiveFPacCallFunctionEntity} — leaving the frozen COBOL PERFORM binding intact.
 *
 * <p>The deleted backend's {@code DoExport()} emitted
 * {@code formatIdentifier(reference.getProcedure().GetName()) + "() ;"}. The template
 * reproduces that through the {@code fpacIdentifier} format, which applies the exact same
 * {@code CJavaExporter.FormatIdentifier} rules {@code CFPacJavaProcedure} uses to NAME the
 * generated paragraph method, so the emitted call always matches the method and compiles.
 *
 * <p>Critically, this test proves the PRODUCTION path reaches the recursive assembler: FPac
 * verb bodies render via {@code CFPacJavaProcedure.DoExport ->
 * LegacyLanguageRenderer.exportChildren(this, false, FPAC_REFERENCE)}, whose no-{@code DoExport}
 * fall-through bridges a retired pure verb to {@code renderRoot(entity, FPAC_REFERENCE)} — the
 * silent-drop regression a previous attempt introduced (pure entity, no {@code DoExport},
 * reflective {@code findExportMethod} returns null) is fixed here.
 */
class CFPacJavaCallFunctionRetirementTest
{
    private static String render(CEntityCallFunction call)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(call, JavaTemplateRole.FPAC_REFERENCE).trim();
    }

    /** Builds a catalog whose procedure registry resolves a subroutine named DOSUBR. */
    private static CObjectCatalog catalogWithDosubr(MockJavaExporter out)
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        // Constructing the procedure registers it in the catalog (CEntityProcedure ctor),
        // so a DOSUBR call reference resolves to it exactly as in a transcoded program.
        new CFPacJavaProcedure(1, "DOSUBR", catalog, out, null);
        return catalog;
    }

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        assertEquals(CEntityCallFunction.class,
            new CJavaFPacEntityFactory(catalog, null)
                .NewEntityCallFunction(1, "DOSUBR", null, null).getClass());
    }

    /**
     * The factory-built entity renders through the recursive assembler with the FPAC_REFERENCE
     * role (NOT a legacy {@code DoExport}, NOT the COBOL PERFORM template) as {@code dosubr() ;}
     * — lowercase, because {@code fpacIdentifier} applies the same {@code FormatIdentifier}
     * rules that name the generated {@code protected int dosubr()} method.
     */
    @Test
    void assemblerRendersDirectCallThroughFpacReference()
    {
        MockJavaExporter out = new MockJavaExporter();
        CObjectCatalog catalog = catalogWithDosubr(out);
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, out);

        CEntityCallFunction call = factory.NewEntityCallFunction(2, "DOSUBR", null, null);
        assertEquals("dosubr() ;", render(call));
    }

    /**
     * End-to-end production lowering: {@code CFPacDoSubr.DoCustomSemanticAnalysis} builds the
     * verb with {@code factory.NewEntityCallFunction(line, name, null, null)} then
     * {@code parent.AddChild(call)}. Driving the calling procedure's export through the SAME
     * statement {@code CFPacJavaProcedure.DoExport} runs ({@code exportChildren(this, false,
     * FPAC_REFERENCE)}) emits {@code dosubr() ;} into the bound output — proving the FPac
     * pipeline's production rendering reaches the recursive assembler with FPAC_REFERENCE and
     * no longer silently drops the call. It also must NOT emit the COBOL PERFORM shape.
     */
    @Test
    void productionProcedureBodyRendersCallThroughAssembler()
    {
        MockJavaExporter out = new MockJavaExporter();
        CObjectCatalog catalog = catalogWithDosubr(out);
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, out);

        CFPacJavaProcedure caller = new CFPacJavaProcedure(2, "MAIN", catalog, out, null);
        CEntityCallFunction call = factory.NewEntityCallFunction(3, "DOSUBR", null, null);
        caller.AddChild(call);
        LegacyLanguageRenderer.bind(caller, out);

        // The exact production driver: CFPacJavaProcedure.DoExport -> exportChildren(FPAC_REFERENCE).
        LegacyLanguageRenderer.invokeExport(caller);

        String rendered = out.getCapturedOutput();
        assertTrue(rendered.contains("dosubr() ;"),
            "production FPac rendering must emit the direct call; got:\n" + rendered);
        assertFalse(rendered.contains("perform("),
            "FPAC_REFERENCE must not fall back to the COBOL PERFORM template; got:\n" + rendered);
    }

    /**
     * Mirrors the parser-facing population sequence of {@code CFPacDoSubr} against the FPac
     * factory and asserts the pure {@link CEntityCallFunction} lowers through the recursive
     * ST4 assembler as {@code dosubr() ;} — the call the FPac parser emits is
     * production-reachable and matches the generated method name.
     */
    @Test
    void parserFacingDoSubrLowersThroughFactoryAndAssembler()
    {
        MockJavaExporter out = new MockJavaExporter();
        CObjectCatalog catalog = catalogWithDosubr(out);
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, out);

        CEntityCallFunction call = factory.NewEntityCallFunction(7, "DOSUBR", null, null);

        assertEquals(CEntityCallFunction.class, call.getClass());
        assertEquals("DOSUBR", call.getCalledProcedureName());
        assertEquals("dosubr() ;", render(call));
    }
}
