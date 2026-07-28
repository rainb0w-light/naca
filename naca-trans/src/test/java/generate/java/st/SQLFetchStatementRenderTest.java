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
import semantic.CDataEntity;
import semantic.SQL.CEntitySQLCursor;
import semantic.SQL.CEntitySQLFetchStatement;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Embedded SQL {@code EXEC SQL FETCH <cursor> INTO :host [, :ind] ...} rendering
 * through the recursive assembler (the production path).
 *
 * <p>SQL FETCH is rendered as a REFERENCE-role executable child via the
 * {@code recursiveSQLFetchStatementEntity} binding. The template reads only
 * {@code entity.*} properties and emits the {@code cursorFetch(<ref>)} runtime call
 * followed by one {@code .into(<host>[, <ind>])} per INTO target (each paired with
 * its optional INDICATOR variable); when a WHENEVER SQLWARNING/SQLERROR policy is in
 * effect (a read-only catalog lookup registered by the WHENEVER statement's Stage-1
 * side effect) its clause chains onto the call. Following the architecture principle,
 * the cursor, every INTO target and every INDICATOR remain semantic children and are
 * recursively rendered through their reference bindings; neither the semantic node
 * nor the factory flattens them to generated text.
 *
 * <p>This test pins parity with the retired backend's output shapes (the legacy
 * backend split the {@code .into(...)} chain across lines via WriteWord/WriteEOL;
 * the template emits the equivalent single-line Java builder chain):
 * <ul>
 *   <li>single INTO -&gt; {@code cursorFetch(<ref>).into(<host>) ;}</li>
 *   <li>INTO with an INDICATOR -&gt; {@code cursorFetch(<ref>).into(<host>, <ind>) ;}</li>
 *   <li>multiple INTO targets -&gt;
 *       {@code cursorFetch(<ref>).into(<a>).into(<b>, <ind>) ;}</li>
 *   <li>with a WHENEVER SQLERROR GOTO policy -&gt;
 *       {@code cursorFetch(<ref>).into(<host>).onErrorGoto(<label>) ;}</li>
 * </ul>
 * Replaces the legacy {@code generate.java.SQL.CJavaSQLFetchStatement} direct backend.
 */
class SQLFetchStatementRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String render(CEntitySQLFetchStatement fetch)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(fetch, JavaTemplateRole.REFERENCE);
    }

    private static CEntitySQLCursor cursor(CObjectCatalog catalog, String name)
    {
        // Pure semantic cursor (the CJavaSQLCursor direct backend is retired):
        // it renders through its dataReferenceEntity reference binding.
        CEntitySQLCursor cursor = new CEntitySQLCursor(name, catalog);
        cursor.setLanguageExporter(new MockJavaExporter());
        return cursor;
    }

    private static CDataEntity ref(String value)
    {
        return new MockDataEntity(1, value);
    }

    @Test
    @DisplayName("single INTO renders cursorFetch(<ref>).into(<host>) ;")
    void singleIntoRendersCursorFetch()
    {
        CObjectCatalog catalog = catalog();
        CEntitySQLFetchStatement fetch =
            new CEntitySQLFetchStatement(1, catalog, cursor(catalog, "CUR-1"));
        fetch.AddFetchInto(ref("CUSTID"), null);
        String output = render(fetch);
        assertEquals("cursorFetch(CUR_1).into(CUSTID) ;", output.trim());
        assertTrue(!output.contains(".onError"), output);
    }

    @Test
    @DisplayName("INTO with an INDICATOR renders .into(<host>, <ind>)")
    void intoWithIndicatorRendersBoth()
    {
        CObjectCatalog catalog = catalog();
        CEntitySQLFetchStatement fetch =
            new CEntitySQLFetchStatement(1, catalog, cursor(catalog, "CUR-1"));
        fetch.AddFetchInto(ref("AMOUNT"), ref("AMOUNT_IND"));
        String output = render(fetch);
        assertEquals("cursorFetch(CUR_1).into(AMOUNT, AMOUNT_IND) ;", output.trim());
    }

    @Test
    @DisplayName("multiple INTO targets render a chained .into(...) per target")
    void multipleIntoTargetsRenderChain()
    {
        CObjectCatalog catalog = catalog();
        CEntitySQLFetchStatement fetch =
            new CEntitySQLFetchStatement(1, catalog, cursor(catalog, "CUR-1"));
        fetch.AddFetchInto(ref("CUSTID"), null);
        fetch.AddFetchInto(ref("AMOUNT"), ref("AMOUNT_IND"));
        String output = render(fetch);
        assertEquals("cursorFetch(CUR_1).into(CUSTID).into(AMOUNT, AMOUNT_IND) ;",
            output.trim());
    }

    @Test
    @DisplayName("FETCH under a WHENEVER SQLERROR GOTO policy chains .onErrorGoto(<label>)")
    void fetchWithSqlErrorGotoChainsClause()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        // Stage-1 side effect of EXEC SQL WHENEVER SQLERROR GOTO PC-ERR-DB2: registers
        // the policy into the catalog (exactly as pinned by SqlOnErrorGotoRenderTest).
        factory.NewEntitySQLOnErrorGoto(108, "PC-ERR-DB2");

        CEntitySQLFetchStatement fetch =
            new CEntitySQLFetchStatement(1, catalog, cursor(catalog, "CUR-1"));
        fetch.AddFetchInto(ref("CUSTID"), null);
        String output = render(fetch);
        assertEquals("cursorFetch(CUR_1).into(CUSTID).onErrorGoto(PC_ERR_DB2) ;",
            output.trim());
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        CEntitySQLCursor cursor = cursor(catalog, "CUR-1");
        CEntitySQLFetchStatement fetch = factory.NewEntitySQLFetchStatement(1, cursor);
        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntitySQLFetchStatement.class, fetch.getClass());
        assertSame(cursor, fetch.getCursor());
    }

    @Test
    @DisplayName("the read-only getters expose the cursor, INTO bindings and catalog clause")
    void gettersExposeCursorIntoBindingsAndClause()
    {
        CObjectCatalog catalog = catalog();
        CEntitySQLCursor cursor = cursor(catalog, "CUR-1");
        CDataEntity into = ref("CUSTID");
        CDataEntity indicator = ref("CUSTID-IND");

        CEntitySQLFetchStatement fetch =
            new CEntitySQLFetchStatement(1, catalog, cursor);
        fetch.AddFetchInto(into, indicator);

        assertSame(cursor, fetch.getCursor());
        assertEquals(1, fetch.getIntoBindings().size());
        assertSame(into, fetch.getIntoBindings().get(0).getInto());
        assertSame(indicator, fetch.getIntoBindings().get(0).getIndicator());
        // No WHENEVER policy registered yet: the clause is null.
        assertNull(fetch.getSqlWarningErrorStatement());

        // A null catalog must not throw (render-path side-effect freedom).
        CEntitySQLFetchStatement detached =
            new CEntitySQLFetchStatement(1, null, null);
        assertNull(detached.getSqlWarningErrorStatement());
    }
}
