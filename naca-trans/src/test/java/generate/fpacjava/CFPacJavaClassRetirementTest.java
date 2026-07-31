package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaFPacEntityFactory;
import generate.java.st.MockJavaExporter;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.CEntityClass;
import semantic.Verbs.CEntityCallProgram;
import semantic.expression.CEntityString;
import utils.CObjectCatalog;
import utils.FPacTranscoder.FPacTranscoderEngine;
import org.junit.jupiter.api.Test;

/**
 * Phase 2 (FPAC) — retirement proof for the former {@code CFPacJavaClass} direct backend.
 *
 * <p>The FPac pipeline shares the target-neutral {@link CEntityClass} program root with COBOL,
 * but WRAPS it differently, so a dedicated {@link JavaTemplateRole#FPAC_ROOT} role consults ONLY
 * {@code semantic-fpac-root-bindings.properties} ({@code semantic.CEntityClass ->
 * recursiveFPacClassEntity}) and never the frozen COBOL root manifest
 * ({@code semantic.CEntityClass -> javaProgramRoot}, a {@code BatchProgram}/{@code CalledProgram}
 * wrapper that would not compile for FPac). {@link CJavaFPacEntityFactory#NewEntityClass} now
 * hands back the pure {@code CEntityClass} (no {@code generate.fpacjava} subclass), and the
 * template reproduces the retired {@code CFPacJavaClass.DoExport} wrapper: {@code import
 * nacaLib.fpacPrgEnv.* ;} + {@code public class NAME extends FPacProgram} with NAME UPPERCASE via
 * the {@code fpacClassName} format ({@code GetName().replace('-','_').toUpperCase()}).
 *
 * <p>Children of the FPac root lower under {@link JavaTemplateRole#FPAC_REFERENCE}
 * ({@code JavaTemplateAssembler.childRole}), so a shared verb such as {@link CEntityCallProgram}
 * resolves through the FPac override ({@code call(PROG.class)}) and never the frozen COBOL PERFORM
 * binding. The production path ({@code FPacTranscoderEngine.exportFpacProgramRoot}) reproduces the
 * retired wrapper byte-for-byte through the bound legacy output and drives the still-legacy body
 * containers by reflection — verbs already retired inside a procedure lower via FPAC_REFERENCE —
 * converging to {@code renderRoot(eSem, FPAC_ROOT)} once the container backends retire.
 */
class CFPacJavaClassRetirementTest
{
    private static String render(CEntityClass programClass)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(programClass, JavaTemplateRole.FPAC_ROOT);
    }

    private static CEntityString programLiteral(CObjectCatalog catalog, String value)
    {
        return new CEntityString(catalog, value.toCharArray());
    }

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        assertEquals(CEntityClass.class,
            new CJavaFPacEntityFactory(catalog, null)
                .NewEntityClass(1, "PROG").getClass());
    }

    /**
     * The FPAC_ROOT contract emits the exact FPac wrapper the retired backend did: the
     * {@code nacaLib.fpacPrgEnv} wildcard import, {@code extends FPacProgram}, and the class name
     * UPPERCASE with dashes folded to underscores ({@code MY-PROG -> MY_PROG}). It must NOT emit
     * COBOL's title-case {@code javaClassName} form nor a COBOL base type ({@code BatchProgram}).
     */
    @Test
    void fpacRootRendersFpacProgramWrapper()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CEntityClass programClass =
            new CJavaFPacEntityFactory(catalog, null).NewEntityClass(1, "MY-PROG");

        String rendered = render(programClass);
        assertTrue(rendered.contains("import nacaLib.fpacPrgEnv.* ;"),
            "FPac root must import nacaLib.fpacPrgEnv.*; got:\n" + rendered);
        assertTrue(rendered.contains("public class MY_PROG extends FPacProgram"),
            "FPac root must declare the UPPERCASE FPacProgram class; got:\n" + rendered);
        assertTrue(rendered.contains("{") && rendered.contains("}"),
            "FPac root must open and close the class body; got:\n" + rendered);
        assertFalse(rendered.contains("My_Program") || rendered.contains("My-Prog"),
            "FPAC_ROOT must not apply COBOL's title-case javaClassName; got:\n" + rendered);
        assertFalse(rendered.contains("BatchProgram") || rendered.contains("CalledProgram"),
            "FPAC_ROOT must not emit a COBOL program base type; got:\n" + rendered);
    }

    /**
     * Children of the FPac root render under FPAC_REFERENCE: a shared {@link CEntityCallProgram}
     * literal CALL "SUBPROG" lowers to the FPac {@code call(SUBPROG.class).executeCall();} inside
     * the class body — never the frozen COBOL PERFORM binding. This proves
     * {@code JavaTemplateAssembler.childRole} propagates FPAC_ROOT -> FPAC_REFERENCE.
     */
    @Test
    void fpacRootChildrenLowerThroughFpacReference()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityClass programClass = factory.NewEntityClass(1, "PROG");

        CEntityCallProgram call = factory.NewEntityCallProgram(2, programLiteral(catalog, "SUBPROG"));
        call.setChecked(true);
        programClass.AddChild(call);

        String rendered = render(programClass);
        assertTrue(rendered.contains("call(SUBPROG.class).executeCall();"),
            "FPac root child must lower via FPAC_REFERENCE to the uppercase program-class call; got:\n"
                + rendered);
        assertFalse(rendered.contains("perform"),
            "FPAC_REFERENCE must not fall back to COBOL's PERFORM binding; got:\n" + rendered);
        assertFalse(rendered.contains("call(Subprog"),
            "FPAC_REFERENCE must not apply COBOL's title-case javaClassName; got:\n" + rendered);
    }

    /**
     * End-to-end production lowering: {@code FPacTranscoderEngine.exportFpacProgramRoot} is the
     * exact driver the FPac transcoder runs on the pure {@code CEntityClass} the factory now
     * returns. With a real still-legacy {@code CFPacJavaProcedure} body holding a retired
     * {@code CEntityCallProgram}, it emits the FPac wrapper, drives the procedure by reflection
     * ({@code protected int MAIN() { ... return NEXT ;}}), and lowers the retired verb inside via
     * FPAC_REFERENCE — proving the production path reaches the recursive assembler with the FPac
     * uppercase class form and preserves the retired backend's output.
     */
    @Test
    void productionRootExportPreservesWrapperAndBridgesBody()
    {
        MockJavaExporter out = new MockJavaExporter();
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, out);

        CEntityClass programClass = factory.NewEntityClass(1, "PROG");

        CFPacJavaProcedure main = new CFPacJavaProcedure(2, "MAIN", catalog, out, null);
        CEntityCallProgram call = factory.NewEntityCallProgram(3, programLiteral(catalog, "SUBPROG"));
        call.setChecked(true);
        main.AddChild(call);
        programClass.AddChild(main);

        FPacTranscoderEngine.exportFpacProgramRoot(programClass);

        String rendered = out.getCapturedOutput();
        assertTrue(rendered.contains("import nacaLib.fpacPrgEnv.* ;"),
            "production FPac root must emit the fpacPrgEnv import; got:\n" + rendered);
        assertTrue(rendered.contains("public class PROG extends FPacProgram"),
            "production FPac root must declare the UPPERCASE FPacProgram class; got:\n" + rendered);
        assertTrue(rendered.contains("protected int MAIN() {"),
            "production FPac root must drive the still-legacy procedure by reflection; got:\n" + rendered);
        assertTrue(rendered.contains("call(SUBPROG.class)"),
            "production FPac root must lower the retired verb via FPAC_REFERENCE; got:\n" + rendered);
        assertTrue(rendered.contains("return NEXT ;"),
            "production FPac procedure body must retain its implicit return; got:\n" + rendered);
        assertFalse(rendered.contains("call(Subprog"),
            "production FPAC_REFERENCE must not apply COBOL's title-case javaClassName; got:\n" + rendered);
    }
}
