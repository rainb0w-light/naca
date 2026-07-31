package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaFPacEntityFactory;
import generate.java.st.MockJavaExporter;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import parser.CGlobalCommentContainer;
import parser.Cobol.elements.CComment;
import semantic.CEntityComment;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/**
 * Phase 2 (FPAC) — retirement proof for the former {@code CFPacJavaComment} direct backend.
 * The independent FPac pipeline shares the target-neutral {@link CEntityComment} semantic
 * entity with COBOL, and its rendering is the SAME shape COBOL already emits for this class
 * ({@code "// " + text}, newlines escaped to {@code "0x000A"}/{@code "Ox000D"} behind the
 * historical {@code indexOf > 0} gate), so {@link CJavaFPacEntityFactory#NewEntityComment}
 * hands back the pure {@code CEntityComment} the COBOL factories build (no
 * {@code generate.fpacjava} subclass) and the {@code FPAC_REFERENCE} role resolves it through
 * the SHARED {@code semantic.CEntityComment=javaComment} binding that {@code
 * JavaSemanticTemplateBindings.loadFpac()} layers verbatim on top of the frozen COBOL
 * manifest — no FPac override is required in {@code semantic-fpac-bindings.properties}
 * (unlike {@code CEntityCallFunction}/{@code CEntityCallProgram}, whose FPac lowering
 * genuinely differs from COBOL). The template only reads {@code entity.comment}, a pure
 * getter; the {@code javaCommentText} atomic renderer performs the newline escaping and
 * right trim, never the semantic layer.
 *
 * <p>The deleted backend's {@code DoExport()} emitted {@code "// " + escaped(comment)} via
 * the legacy output controller, and its {@code ExportReference(int)} is dead code: the
 * shared {@code CBaseLanguageExporter.renderComment} bridge already renders commentContainer
 * (header/listing) comments through {@code renderRoot(comment, REFERENCE).strip()} for every
 * pipeline, so FPac comments in production reach the assembler regardless of the entity's
 * runtime class. FPac comments NEVER appear as AST body children (the FPac parser routes
 * every COMMENTS token into the {@code CGlobalCommentContainer}, and a procedure body could
 * not legally hold one — {@code CEntityProcedure.hasExplicitGetOut()} casts its last child
 * to {@code CBaseActionEntity}, which the deleted backend, a {@code CEntityComment}
 * subclass, would have failed identically). The production chain these tests prove is
 * therefore the real one: {@code CComment} parser node (exactly what {@code
 * CGlobalCommentContainer.ParseComment} builds from a COMMENTS token) {@code ->}
 * container {@code DoSemanticAnalysis(fpacFactory) ->} factory-built pure {@code
 * CEntityComment} (bound to the legacy output at creation) {@code ->} the exporter's
 * {@code renderComment} expression. The template only reads {@code entity.comment}, a pure
 * getter; the {@code javaCommentText} atomic renderer performs the newline escaping and
 * right trim, never the semantic layer.
 */
class CFPacJavaCommentRetirementTest
{
    private static String render(CEntityComment comment, JavaTemplateRole role)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(comment, role)
            .strip();
    }

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        assertEquals(CEntityComment.class,
            new CJavaFPacEntityFactory(catalog, null).NewEntityComment(1, "text").getClass());
    }

    /**
     * The factory-built entity renders through the recursive assembler with the
     * FPAC_REFERENCE role (NOT a legacy {@code DoExport}) as {@code // <text>}, resolving
     * through the shared {@code javaComment} binding.
     */
    @Test
    void assemblerRendersCommentThroughFpacReference()
    {
        MockJavaExporter out = new MockJavaExporter();
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, out);

        CEntityComment comment = factory.NewEntityComment(2, "TRANSFER THE BALANCE");

        assertEquals("// TRANSFER THE BALANCE", render(comment, JavaTemplateRole.FPAC_REFERENCE));
    }

    /**
     * The shared header-comment production path ({@code CBaseLanguageExporter.renderComment}
     * runs exactly {@code renderRoot(comment, REFERENCE).strip()} on the entity the factory
     * built from the commentContainer) renders the factory entity identically.
     */
    @Test
    void headerCommentBridgeRendersFactoryEntityThroughReference()
    {
        MockJavaExporter out = new MockJavaExporter();
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, out);

        CEntityComment comment = factory.NewEntityComment(1, "PROGRAM HEADER");

        assertEquals("// PROGRAM HEADER", render(comment, JavaTemplateRole.REFERENCE));
    }

    /**
     * Multi-line comment text is escaped exactly as the deleted backend did: the
     * {@code javaCommentText} renderer reproduces the historical {@code indexOf > 0} gate
     * and the {@code "Ox000D"} capital-O quirk; the {@code "// "} marker comes from the STG.
     */
    @Test
    void assemblerEscapesNewlinesLikeDeletedBackend()
    {
        MockJavaExporter out = new MockJavaExporter();
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, out);

        assertEquals("// line10x000Aline2",
            render(factory.NewEntityComment(3, "line1\nline2"), JavaTemplateRole.FPAC_REFERENCE));
        assertEquals("// aOx000Db",
            render(factory.NewEntityComment(4, "a\rb"), JavaTemplateRole.FPAC_REFERENCE));
    }

    /**
     * End-to-end production lowering of an FPac comment, driving the exact chain the FPac
     * pipeline runs: the parser routes every COMMENTS token into the {@code
     * CGlobalCommentContainer} (as the {@code CComment} node {@code ParseComment} builds),
     * the engine's {@code commentContainer.DoSemanticAnalysis(factory)} lowers each node
     * with {@code factory.NewEntityComment} (which binds the legacy output controller at
     * creation), and the exporter renders the entity with the exact {@code renderComment}
     * expression {@code CBaseLanguageExporter} runs ({@code renderRoot(comment,
     * REFERENCE).strip()}). Asserts the container yields the pure {@code CEntityComment}
     * (no {@code generate.fpacjava} subclass) and that the production rendering expression
     * emits {@code // FPAC HEADER COMMENT}.
     */
    @Test
    void productionCommentContainerChainRendersThroughAssembler()
    {
        MockJavaExporter out = new MockJavaExporter();
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, out);

        // Exactly what CGlobalCommentContainer.ParseComment builds from a COMMENTS token.
        CGlobalCommentContainer container = new CGlobalCommentContainer();
        container.RegisterComment(7, new CComment(7, "FPAC HEADER COMMENT"));

        // The engine's parser.commentContainer.DoSemanticAnalysis(factory) step.
        container.DoSemanticAnalysis(factory);

        CEntityComment lowered = container.GetCurrentComment();
        assertEquals(CEntityComment.class, lowered.getClass(),
            "FPac lowering must yield the pure semantic comment, not a generate.fpacjava subclass");

        // The exact CBaseLanguageExporter.renderComment expression the production exporter runs.
        assertEquals("// FPAC HEADER COMMENT", render(lowered, JavaTemplateRole.REFERENCE));
    }
}
