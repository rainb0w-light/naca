package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaFPacEntityFactory;
import generate.LegacyLanguageRenderer;
import generate.java.st.MockDataEntity;
import generate.java.st.MockJavaExporter;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.Verbs.CEntityCallProgram;
import semantic.expression.CEntityString;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/**
 * Phase 2 (FPAC) — retirement proof for the former {@code CFPacJavaCallProgram} direct backend.
 * The FPac pipeline shares the target-neutral {@link CEntityCallProgram} semantic verb with
 * COBOL and reuses the SAME lowering SHAPE — {@code call(<program>).using(...).executeCall();} —
 * but its program-class naming genuinely differs, so {@link
 * CJavaFPacEntityFactory#NewEntityCallProgram} hands back the pure {@code CEntityCallProgram}
 * (no {@code generate.fpacjava} subclass) and the independent {@code FPAC_REFERENCE} role layers
 * {@code semantic-fpac-bindings.properties} over the shared reference manifest to redirect ONLY
 * this class to {@code recursiveFPacCallProgramEntity} — leaving the frozen COBOL
 * {@code recursiveCallProgramEntity} binding intact.
 *
 * <p>The decisive difference: COBOL references a checked literal {@code CALL "PROG"} as
 * {@code call(Prog.class)} via the title-case {@code javaClassName} format
 * ({@code CobolNameUtil.fixJavaName}). FPac names generated program classes UPPERCASE
 * ({@code CFPacJavaClass.DoExport}: {@code GetName().replace('-','_').toUpperCase()}), so the
 * FPac template applies the {@code fpacClassName} format to emit {@code call(PROG.class)},
 * matching {@code public class PROG extends FPacProgram}. {@code call(Prog.class)} would not
 * compile against FPac's uppercase class. The deleted backend used the raw literal text
 * ({@code call(PROG.class)} for an uppercase literal, but {@code call(prog.class)} for a
 * lowercase one — a latent casing defect) and, on the unchecked error path, wrapped the name in
 * quotes WITH a stray {@code .class} suffix; the template reproduces the correct uppercase form.
 *
 * <p>Critically, this test proves the PRODUCTION path reaches the recursive assembler: FPac verb
 * bodies render via {@code CFPacJavaProcedure.DoExport -> LegacyLanguageRenderer.exportChildren(
 * this, false, FPAC_REFERENCE)}, whose no-{@code DoExport} fall-through bridges a retired pure
 * verb to {@code renderRoot(entity, FPAC_REFERENCE)}.
 */
class CFPacJavaCallProgramRetirementTest
{
    private static String render(CEntityCallProgram call)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(call, JavaTemplateRole.FPAC_REFERENCE).trim();
    }

    private static CEntityString programLiteral(CObjectCatalog catalog, String value)
    {
        return new CEntityString(catalog, value.toCharArray());
    }

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        assertEquals(CEntityCallProgram.class,
            new CJavaFPacEntityFactory(catalog, null)
                .NewEntityCallProgram(1, programLiteral(catalog, "SUBPROG")).getClass());
    }

    /**
     * The checked literal CALL "SUBPROG" lowers through FPAC_REFERENCE as
     * {@code call(SUBPROG.class).executeCall();} — UPPERCASE, because {@code fpacClassName}
     * applies the exact same {@code replace('-','_').toUpperCase()} rules {@code CFPacJavaClass}
     * uses to NAME the generated {@code public class SUBPROG extends FPacProgram}. It must NOT
     * emit COBOL's title-case {@code call(Subprog.class)}, which would not compile against FPac.
     */
    @Test
    void checkedLiteralCallUsesUppercaseProgramClass()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CEntityCallProgram call = new CEntityCallProgram(
            1, catalog, programLiteral(catalog, "SUBPROG"));
        call.setChecked(true);

        assertEquals("call(SUBPROG.class).executeCall();", render(call));
        assertFalse(render(call).contains("Subprog"),
            "FPAC_REFERENCE must not apply COBOL's title-case javaClassName; got:\n" + render(call));
    }

    /**
     * The unchecked literal CALL (the "missing sub program" error path where {@code CFPacCall}
     * sets {@code ischeck=false}) lowers to {@code call("SUBPROG").executeCall();} — a quoted
     * uppercase program name via {@code call(String)}. The deleted backend emitted the buggy
     * {@code call("SUBPROG.class")} (a stray {@code .class} INSIDE the string); the template
     * drops that suffix, matching the canonical COBOL shape with FPac casing.
     */
    @Test
    void uncheckedLiteralCallRetainsQuotedUppercaseProgramName()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CEntityCallProgram call = new CEntityCallProgram(
            1, catalog, programLiteral(catalog, "SUBPROG"));

        assertEquals("call(\"SUBPROG\").executeCall();", render(call));
    }

    /**
     * A dynamic (non-literal) program reference renders its semantic reference UNQUOTED through
     * {@code call(VarAndEdit)} — the deleted backend wrapped the variable expression in quotes
     * ({@code call("wProg...")}), turning a dynamic call into a constant string; the template
     * fixes that latent defect, matching the frozen COBOL behavior.
     */
    @Test
    void dynamicCallRendersItsSemanticReference()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CEntityCallProgram call = new CEntityCallProgram(
            1, catalog, new MockDataEntity(1, "PROGRAM_NAME"));

        assertEquals("call(PROGRAM_NAME).executeCall();", render(call));
    }

    /**
     * By-reference and by-value parameters (the two methods {@code CFPacCall} populates) render
     * as the chained {@code .using(...)} / {@code .usingValue(...)} calls on the
     * {@code CCallProgram} returned by {@code call(...)}, exactly as the deleted backend emitted
     * (minus its stray inter-token spaces). {@code getCallParameters()} already filters ignored
     * references, mirroring the backend's {@code !p.reference.ignore()} guard.
     */
    @Test
    void callParametersRenderChainedUsingCalls()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CEntityCallProgram call = new CEntityCallProgram(
            1, catalog, programLiteral(catalog, "SUBPROG"));
        call.setChecked(true);
        call.SetParameterByRef(new MockDataEntity(1, "ARG"));
        call.SetParameterByValue(new MockDataEntity(1, "VAL"));

        assertEquals("call(SUBPROG.class).using(ARG).usingValue(VAL).executeCall();", render(call));
    }

    /**
     * End-to-end production lowering: {@code CFPacCall.DoCustomSemanticAnalysis} builds the verb
     * with {@code factory.NewEntityCallProgram(line, ref)} + {@code setChecked} then
     * {@code parent.AddChild(call)}. Driving the calling procedure's export through the SAME
     * statement {@code CFPacJavaProcedure.DoExport} runs ({@code exportChildren(this, false,
     * FPAC_REFERENCE)}) emits {@code call(SUBPROG.class)...executeCall();} into the bound output
     * — proving the FPac pipeline's production rendering reaches the recursive assembler with
     * FPAC_REFERENCE and emits the FPac-uppercase class form, never COBOL's title-case form.
     */
    @Test
    void productionProcedureBodyRendersCallThroughAssembler()
    {
        MockJavaExporter out = new MockJavaExporter();
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, out);

        CFPacJavaProcedure caller = new CFPacJavaProcedure(2, "MAIN", catalog, out, null);
        CEntityCallProgram call = factory.NewEntityCallProgram(3, programLiteral(catalog, "SUBPROG"));
        call.setChecked(true);
        caller.AddChild(call);
        LegacyLanguageRenderer.bind(caller, out);

        // The exact production driver: CFPacJavaProcedure.DoExport -> exportChildren(FPAC_REFERENCE).
        LegacyLanguageRenderer.invokeExport(caller);

        String rendered = out.getCapturedOutput();
        assertTrue(rendered.contains("call(SUBPROG.class)"),
            "production FPac rendering must emit the uppercase program-class call; got:\n" + rendered);
        assertTrue(rendered.contains(".executeCall();"),
            "production FPac rendering must terminate the call chain; got:\n" + rendered);
        assertFalse(rendered.contains("call(Subprog"),
            "FPAC_REFERENCE must not fall back to COBOL's title-case javaClassName; got:\n" + rendered);
    }
}
