package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.fixtures.LegacyExternalDataStructureFixture;
import generate.fixtures.LegacyInlineFixture;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Embedded SQL {@code EXEC SQL INCLUDE <copybook> END-EXEC} rendering through the
 * recursive assembler (the production DECLARATION path).
 *
 * <p>SQL INCLUDE has no statement-level backend of its own: during semantic analysis
 * {@code CExecSQLInclude.DoCustomSemanticAnalysis} resolves the copybook reference
 * ({@code programCatalog.GetExternalDataReference(ref, ...)}) to a
 * {@code CBaseExternalEntity} and wraps it with {@code factory.NewEntityInline(line, e)}
 * — a {@link semantic.CEntityInline}. The recursive assembler renders that inline entity
 * via the {@code semantic.CEntityInline=dataInlineDeclaration} declaration binding, which
 * reads only {@code entity.*} properties ({@code copyType}, {@code externalEntity},
 * {@code replacing}, {@code replaceItem}, {@code replaceValue}). This is the same inline
 * mechanism COBOL {@code COPY} rides on; this test pins the SQL INCLUDE resolution shape:
 * <ul>
 *   <li>a resolved include -&gt; {@code <Type> <ref> = <Type>.Copy(this) ;}</li>
 *   <li>a resolved include carrying a REPLACING clause -&gt;
 *       {@code <Type> <ref> = <Type>.Copy(this, replacing(item, value)) ;}.</li>
 * </ul>
 * Asserts byte-for-byte parity with the output the retired direct generator
 * produces, so the assembler-bound path stays faithful to the legacy lowering.
 */
class SQLIncludeRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String normalize(String source)
    {
        return source.replaceAll("\\s+", " ").strip();
    }

    @Test
    @DisplayName("a resolved EXEC SQL INCLUDE renders <Type> <ref> = <Type>.Copy(this) ;")
    void resolvedIncludeRendersCopybookInstanceDeclaration()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CObjectCatalog catalog = catalog();
        // EXEC SQL INCLUDE SQLCA: the resolved copybook external entity wrapped in the
        // inline entity (factory.NewEntityInline), as CExecSQLInclude lowers it.
        LegacyExternalDataStructureFixture copybook =
            new LegacyExternalDataStructureFixture(1, "Sqlca", catalog, exporter);
        LegacyInlineFixture inline =
            new LegacyInlineFixture(2, catalog, exporter, copybook);

        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(inline, JavaTemplateRole.DECLARATION);
        generate.LegacyLanguageRenderer.startExport(inline);

        assertEquals(normalize(exporter.getCapturedOutput()), normalize(rendered));
        assertTrue(rendered.contains("= Sqlca.Copy(this) ;"), rendered);
    }

    @Test
    @DisplayName("a resolved include with a REPLACING clause renders replacing(item, value)")
    void resolvedIncludeWithReplacingClause()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CObjectCatalog catalog = catalog();
        LegacyExternalDataStructureFixture copybook =
            new LegacyExternalDataStructureFixture(1, "Sqlca", catalog, exporter);
        // REPLACING ==2 BY 5== on the INCLUDE: the inline carries the replace level/value.
        copybook.ReplaceLevel(2, 5);
        LegacyInlineFixture inline =
            new LegacyInlineFixture(2, catalog, exporter, copybook);

        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(inline, JavaTemplateRole.DECLARATION);
        generate.LegacyLanguageRenderer.startExport(inline);

        assertEquals(normalize(exporter.getCapturedOutput()), normalize(rendered));
        assertTrue(rendered.contains("= Sqlca.Copy(this, replacing(2, 5)) ;"), rendered);
    }
}
