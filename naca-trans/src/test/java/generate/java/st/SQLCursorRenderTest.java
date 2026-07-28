package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import java.util.Vector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CEntitySQLCursorSection;
import semantic.SQL.CEntitySQLCursor;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Embedded SQL {@code DECLARE CURSOR} handle rendering through the recursive
 * assembler after the retirement of the {@code generate.java.SQL.CJavaSQLCursor}
 * direct backend.
 *
 * <p>The cursor is a declaration-only semantic data entity: it renders as a
 * plain Java identifier through the {@code dataReferenceEntity} reference
 * binding ({@code semantic.SQL.CEntitySQLCursor} in
 * {@code semantic-runtime-bindings.properties}), both standalone (REFERENCE
 * role) and as a semantic child of the cursor declaration section
 * ({@code recursiveSQLCursorSectionEntity}). The template reads only
 * {@code entity.*} properties; neither the semantic node nor the factory
 * flattens the cursor to generated text. The tests pin:
 * <ul>
 *   <li>reference render -&gt; the formatted cursor identifier
 *       ({@code CUR-1 -&gt; CUR_1}), byte-for-byte what the retired backend's
 *       {@code ExportReference} emitted;</li>
 *   <li>section child render -&gt; {@code SQLCursor CUR_1 = declare.cursor() ;};</li>
 *   <li>both entity factories now hand back the pure semantic entity class.</li>
 * </ul>
 * Replaces the legacy {@code generate.java.SQL.CJavaSQLCursor} direct backend.
 */
class SQLCursorRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static CEntitySQLCursor cursor(CObjectCatalog catalog, String name)
    {
        CEntitySQLCursor cursor = new CEntitySQLCursor(name, catalog);
        cursor.setLanguageExporter(new MockJavaExporter());
        return cursor;
    }

    @Test
    @DisplayName("a cursor reference renders its formatted identifier (dataReferenceEntity)")
    void cursorReferenceRendersFormattedIdentifier()
    {
        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(cursor(catalog(), "CUR-1"), JavaTemplateRole.REFERENCE);
        assertEquals("CUR_1", output.trim());
    }

    @Test
    @DisplayName("a cursor renders SQLCursor <ref> = declare.cursor() ; inside its section")
    void cursorInsideSectionRendersDeclarationLine()
    {
        CObjectCatalog catalog = catalog();
        CEntitySQLCursorSection section = new CEntitySQLCursorSection(catalog);
        Vector<Object> cursors = new Vector<>();
        cursors.add(cursor(catalog, "CUR-1"));
        section.SetCursors(cursors);
        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(section, JavaTemplateRole.DECLARATION);
        assertTrue(output.contains("DataSection SQLCursorSection = declare.cursorSection() ;"),
            output);
        assertTrue(output.contains("SQLCursor CUR_1 = declare.cursor() ;"), output);
    }

    @Test
    @DisplayName("the legacy ExportReference keeps the retired backend's formatted identifier")
    void exportReferencePreservesLegacyValue()
    {
        // Direct-path compatibility value: identical to what the retired
        // CJavaSQLCursor.ExportReference produced for the direct SQL statement
        // backends (cursorFetch(cursor.ExportReference(...)) &co.).
        assertEquals("CUR_1", cursor(catalog(), "CUR-1").ExportReference(0));
    }

    @Test
    @DisplayName("both factories return the pure semantic cursor (direct backend retired)")
    void factoriesReturnPureSemanticCursor()
    {
        CObjectCatalog catalog = catalog();
        MockJavaExporter exporter = new MockJavaExporter();

        CEntitySQLCursor fromDirect =
            new CJavaEntityFactory(catalog, exporter).NewEntitySQLCursor("CUR-DIR");
        assertEquals(CEntitySQLCursor.class, fromDirect.getClass());

        CEntitySQLCursor fromST =
            new CJavaEntityFactoryST(catalog, exporter).NewEntitySQLCursor("CUR-ST");
        assertEquals(CEntitySQLCursor.class, fromST.getClass());
        assertSame(fromST, catalog.GetSQLCursor("CUR-ST"));
    }
}
