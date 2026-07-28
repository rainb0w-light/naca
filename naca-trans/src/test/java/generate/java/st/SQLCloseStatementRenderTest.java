package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.SQL.CEntitySQLCloseStatement;
import semantic.SQL.CEntitySQLCursor;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Embedded SQL {@code EXEC SQL CLOSE <cursor>} rendering through the recursive
 * assembler (the production path).
 *
 * <p>SQL CLOSE is rendered as a REFERENCE-role executable child via the
 * {@code recursiveSQLCloseStatementEntity} binding. The template reads only
 * {@code entity.*} properties and emits the {@code cursorClose(<ref>)} runtime
 * call; when a WHENEVER SQLWARNING/SQLERROR policy is in effect (a read-only
 * catalog lookup registered by the WHENEVER statement's Stage-1 side effect) its
 * clause chains onto the call. Following the architecture principle, the closed
 * cursor remains a semantic child and is recursively rendered through its
 * reference binding; neither the semantic node nor the factory flattens it to
 * generated text.
 * This test pins byte-for-byte parity with the retired backend's output shapes:
 * <ul>
 *   <li>bare CLOSE -&gt; {@code cursorClose(<ref>) ;}</li>
 *   <li>with a WHENEVER SQLERROR GOTO policy -&gt;
 *       {@code cursorClose(<ref>).onErrorGoto(<label>) ;}</li>
 * </ul>
 * Replaces the legacy {@code generate.java.SQL.CJavaSQLCloseStatement} direct backend.
 */
class SQLCloseStatementRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String render(CEntitySQLCloseStatement close)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(close, JavaTemplateRole.REFERENCE);
    }

    private static CEntitySQLCursor cursor(CObjectCatalog catalog, String name)
    {
        // Pure semantic cursor (the CJavaSQLCursor direct backend is retired):
        // it renders through its dataReferenceEntity reference binding.
        CEntitySQLCursor cursor = new CEntitySQLCursor(name, catalog);
        cursor.setLanguageExporter(new MockJavaExporter());
        return cursor;
    }

    @Test
    @DisplayName("bare CLOSE renders cursorClose(<ref>) ;")
    void bareCloseRendersCursorClose()
    {
        CObjectCatalog catalog = catalog();
        CEntitySQLCloseStatement close =
            new CEntitySQLCloseStatement(1, catalog, cursor(catalog, "CUR-1"));
        String output = render(close);
        assertEquals("cursorClose(CUR_1) ;", output.trim());
        assertTrue(!output.contains(".onError"), output);
    }

    @Test
    @DisplayName("CLOSE under a WHENEVER SQLERROR GOTO policy chains .onErrorGoto(<label>)")
    void closeWithSqlErrorGotoChainsClause()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        // Stage-1 side effect of EXEC SQL WHENEVER SQLERROR GOTO PC-ERR-DB2: registers
        // the policy into the catalog (exactly as pinned by SqlOnErrorGotoRenderTest).
        factory.NewEntitySQLOnErrorGoto(108, "PC-ERR-DB2");

        CEntitySQLCloseStatement close =
            new CEntitySQLCloseStatement(1, catalog, cursor(catalog, "CUR-1"));
        String output = render(close);
        assertEquals("cursorClose(CUR_1).onErrorGoto(PC_ERR_DB2) ;", output.trim());
    }

    @Test
    @DisplayName("the ST4 factory preserves the closed cursor as a semantic child")
    void factoryPreservesCursorSemanticChild()
    {
        CObjectCatalog catalog = catalog();
        MockJavaExporter exporter = new MockJavaExporter();
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog, exporter);
        CEntitySQLCursor cursor = new CEntitySQLCursor("CUR-1", catalog);
        cursor.setLanguageExporter(exporter);
        CEntitySQLCloseStatement close = factory.NewEntitySQLCloseStatement(1, cursor);
        assertSame(cursor, close.getCursor());
        assertEquals("cursorClose(CUR_1) ;", render(close).trim());
    }

    @Test
    @DisplayName("the read-only getters expose the closed cursor and the catalog clause")
    void gettersExposeCursorAndClause()
    {
        CObjectCatalog catalog = catalog();
        MockJavaExporter exporter = new MockJavaExporter();
        CEntitySQLCursor cursor = new CEntitySQLCursor("CUR-1", catalog);
        cursor.setLanguageExporter(exporter);

        CEntitySQLCloseStatement close = new CEntitySQLCloseStatement(1, catalog, cursor);
        assertSame(cursor, close.getCursor());
        // No WHENEVER policy registered yet: the clause is null.
        assertNull(close.getSqlWarningErrorStatement());

        // A null catalog must not throw (render-path side-effect freedom).
        CEntitySQLCloseStatement detached = new CEntitySQLCloseStatement(1, null, null);
        assertNull(detached.getSqlWarningErrorStatement());
    }
}
