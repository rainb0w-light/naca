package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaFPacEntityFactory;
import generate.java.st.MockJavaExporter;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.CEntityFileDescriptor;
import semantic.CEntityProcedure;
import semantic.Verbs.CEntityCloseFile;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/**
 * Phase 2 (FPAC) — retirement proof for the former {@code CFPacJavaCloseFile} direct backend.
 * The independent FPac pipeline shares the target-neutral {@link CEntityCloseFile} semantic
 * verb with COBOL, and its lowering is the SAME shape COBOL already emits for this class
 * ({@code descriptor.getFormattedName() + ".close()"}), so {@link
 * CJavaFPacEntityFactory#NewEntityCloseFile} hands back the pure {@code CEntityCloseFile} the
 * COBOL factories build (no {@code generate.fpacjava} subclass) and the {@code FPAC_REFERENCE}
 * role resolves it through the SHARED {@code recursiveCloseFileEntity} binding — no FPac
 * override is required in {@code semantic-fpac-bindings.properties} (unlike
 * {@code CEntityCallFunction}/{@code CEntityCallProgram}, whose FPac lowering genuinely
 * differs from COBOL). The template only reads {@code entity.fileDescriptor}, a pure getter
 * ({@code getFormattedName()} performs no {@code FormatIdentifier}/output work).
 *
 * <p>The deleted backend's {@code DoExport()} emitted
 * {@code eFileDescriptor.getFormattedName() + ".close() ;"}. The shared template reproduces
 * the identical, compilable statement {@code <name>.close();}.
 *
 * <p>Critically, these tests prove the PRODUCTION path reaches the recursive assembler with
 * the legacy output controller bound. FPac verb bodies render via {@code
 * CFPacJavaProcedure.DoExport -> LegacyLanguageRenderer.exportChildren(this, false,
 * FPAC_REFERENCE)}; a retired pure verb (no {@code DoExport}) falls through to {@code
 * renderRoot(entity, FPAC_REFERENCE)} and is written via {@code writeLine}, which
 * {@code requireOutput}s the controller bound to THAT entity. Production binds each entity at
 * creation (the procedure self-binds in its constructor; the factory binds the verb it
 * returns), and neither {@code CBaseLanguageEntity.AddChild} nor {@code FPacTranscoderEngine}
 * performs a whole-tree bind pass. The production test below therefore mirrors that exact
 * sequence and deliberately performs NO post-{@code AddChild} tree bind — a previous attempt
 * masked a missing factory bind with such a tree bind, which does not exist in production.
 */
class CFPacJavaCloseFileRetirementTest
{
    /** A named descriptor whose formatted name needs no catalog for this focused fixture. */
    private static CEntityFileDescriptor fileDescriptor(String name)
    {
        return new CEntityFileDescriptor(1, name, null)
        {
            @Override
            protected void RegisterMySelfToCatalog()
            {
                // No catalog registration is needed for this focused reference fixture.
            }
        };
    }

    private static String render(CEntityCloseFile close)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(close, JavaTemplateRole.FPAC_REFERENCE).trim();
    }

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        assertEquals(CEntityCloseFile.class,
            new CJavaFPacEntityFactory(catalog, null).NewEntityCloseFile(1).getClass());
    }

    /**
     * The factory-built entity renders through the recursive assembler with the FPAC_REFERENCE
     * role (NOT a legacy {@code DoExport}) as the compilable {@code CUSTOMER_FILE.close();},
     * resolving through the shared {@code recursiveCloseFileEntity} binding.
     */
    @Test
    void assemblerRendersDescriptorCloseThroughFpacReference()
    {
        MockJavaExporter out = new MockJavaExporter();
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, out);

        CEntityCloseFile close = factory.NewEntityCloseFile(2);
        close.setFileDescriptor(fileDescriptor("CUSTOMER-FILE"));

        assertEquals("CUSTOMER_FILE.close();", render(close));
    }

    /**
     * End-to-end production lowering mirroring {@code CFPacClose.DoCustomSemanticAnalysis}:
     * the verb is built with {@code factory.NewEntityCloseFile(line)} (which binds the legacy
     * output controller at creation), the descriptor is set, then {@code parent.AddChild(close)}
     * — which does NOT propagate bindings. Driving the owning procedure's export through the
     * SAME statement {@code CFPacJavaProcedure.DoExport} runs ({@code exportChildren(this,
     * false, FPAC_REFERENCE)}) emits {@code CUSTOMER_FILE.close();} into the bound output.
     *
     * <p>The owning procedure self-binds in its constructor (production behavior); this test
     * performs NO {@code LegacyLanguageRenderer.bind(owner, out)} after {@code AddChild}, so a
     * missing factory bind would surface here exactly as it does in production — an
     * {@code IllegalStateException("No legacy output controller bound to ...")} — rather than
     * being masked by a whole-tree bind the FPac engine never performs.
     */
    @Test
    void productionProcedureBodyRendersCloseThroughAssembler()
    {
        MockJavaExporter out = new MockJavaExporter();
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, out);

        // Constructor self-binds the owner to `out`, exactly as in production.
        CEntityProcedure owner = factory.NewEntityProcedure(2, "MAIN", null);

        // Factory binds the verb to `out` at creation; AddChild below does not propagate.
        CEntityCloseFile close = factory.NewEntityCloseFile(3);
        close.setFileDescriptor(fileDescriptor("CUSTOMER-FILE"));
        owner.AddChild(close);

        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(owner, JavaTemplateRole.FPAC_REFERENCE);
        assertTrue(rendered.contains("CUSTOMER_FILE.close();"),
            "production FPac rendering must emit the descriptor close through the assembler; got:\n"
                + rendered);
    }
}
