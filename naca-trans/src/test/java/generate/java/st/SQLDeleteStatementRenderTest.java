package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import java.util.Vector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.SQL.CEntitySQLCursor;
import semantic.SQL.CEntitySQLDeleteStatement;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Embedded SQL {@code EXEC SQL DELETE FROM ... END-EXEC} rendering through the
 * recursive assembler (the production path) after the retirement of the
 * {@code generate.java.SQL.CJavaSQLDeleteStatement} direct backend.
 *
 * <p>SQL DELETE is rendered as a REFERENCE-role executable child via the
 * {@code recursiveSQLDeleteStatementEntity} binding. The template reads only
 * {@code entity.*} properties and emits the {@code sql("<statement>")} runtime call
 * for an unbound DELETE, or {@code cursorDeleteCurrent(<cursor>, "<statement>")}
 * when a cursor is bound: the cursor handle stays a semantic child rendered through
 * its {@code dataReferenceEntity} binding, the trimmed DELETE text is wrapped as a
 * Java string literal (exactly what the retired backend's
 * {@code WriteLongString(csStatement.trim())} emitted), each host-variable parameter
 * chains a 1-based {@code .param(N, <hostRef>)}, and a WHENEVER SQLWARNING/SQLERROR
 * policy (a read-only catalog lookup registered by the WHENEVER statement's Stage-1
 * side effect) chains its clause before the {@code " ;"} terminator. Following the
 * architecture principle, neither the semantic node nor the factory flattens the
 * cursor or the parameters to generated text.
 *
 * <p>This test pins byte-for-byte parity with the retired backend's output shapes:
 * <ul>
 *   <li>bare DELETE -&gt; {@code sql("<statement>") ;}</li>
 *   <li>with parameters -&gt;
 *       {@code sql("<statement>").param(1, <a>).param(2, <b>) ;}</li>
 *   <li>bound cursor -&gt; {@code cursorDeleteCurrent(<ref>, "<statement>") ;}</li>
 *   <li>with a WHENEVER SQLERROR GOTO policy -&gt;
 *       {@code sql("<statement>").onErrorGoto(<label>) ;}</li>
 * </ul>
 * Replaces the legacy {@code generate.java.SQL.CJavaSQLDeleteStatement} direct
 * backend.
 */
class SQLDeleteStatementRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String render(CEntitySQLDeleteStatement delete)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(delete, JavaTemplateRole.REFERENCE);
    }

    private static CEntitySQLCursor cursor(CObjectCatalog catalog, String name)
    {
        CEntitySQLCursor cursor = new CEntitySQLCursor(name, catalog);
        generate.LegacyLanguageRenderer.bind(cursor, new MockJavaExporter());
        return cursor;
    }

    private static CDataEntity ref(String value)
    {
        return new MockDataEntity(1, value);
    }

    @Test
    @DisplayName("bare DELETE renders sql(\"<statement>\") ;")
    void bareDeleteRendersSql()
    {
        CEntitySQLDeleteStatement delete =
            new CEntitySQLDeleteStatement(1, catalog(),
                "DELETE FROM CUSTOMERS", new Vector<>());
        String output = render(delete);
        assertEquals("sql(\"DELETE FROM CUSTOMERS\") ;", output.trim());
        assertTrue(!output.contains(".param"), output);
        assertTrue(!output.contains(".onError"), output);
        assertTrue(!output.contains("cursorDeleteCurrent"), output);
    }

    @Test
    @DisplayName("host parameters render a 1-based .param(N, <ref>) chain")
    void parametersRenderParamChain()
    {
        Vector<CDataEntity> params = new Vector<>();
        params.add(ref("CUSTID"));
        params.add(ref("AMOUNT"));
        CEntitySQLDeleteStatement delete =
            new CEntitySQLDeleteStatement(1, catalog(), "DELETE FROM ORDERS", params);
        String output = render(delete);
        assertEquals(
            "sql(\"DELETE FROM ORDERS\").param(1, CUSTID).param(2, AMOUNT) ;",
            output.trim());
    }

    @Test
    @DisplayName("a bound cursor renders cursorDeleteCurrent(<ref>, \"<statement>\") ;")
    void boundCursorRendersCursorDeleteCurrent()
    {
        CEntitySQLDeleteStatement delete =
            new CEntitySQLDeleteStatement(1, catalog(),
                "DELETE FROM CUSTOMERS", new Vector<>());
        delete.setCursor(cursor(catalog(), "CUR-1"));
        String output = render(delete);
        assertEquals("cursorDeleteCurrent(CUR_1, \"DELETE FROM CUSTOMERS\") ;",
            output.trim());
    }

    @Test
    @DisplayName("a bound cursor with parameters chains the .param(N, <ref>) chain")
    void boundCursorWithParametersRendersParamChain()
    {
        Vector<CDataEntity> params = new Vector<>();
        params.add(ref("CUSTID"));
        CEntitySQLDeleteStatement delete =
            new CEntitySQLDeleteStatement(1, catalog(), "DELETE FROM ORDERS", params);
        delete.setCursor(cursor(catalog(), "CUR-1"));
        String output = render(delete);
        assertEquals(
            "cursorDeleteCurrent(CUR_1, \"DELETE FROM ORDERS\").param(1, CUSTID) ;",
            output.trim());
    }

    @Test
    @DisplayName("a WHENEVER SQLERROR GOTO policy chains .onErrorGoto(<label>)")
    void sqlErrorGotoChainsClause()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog, new MockJavaExporter());
        // Stage-1 side effect of EXEC SQL WHENEVER SQLERROR GOTO PC-ERR-DB2: registers
        // the policy into the catalog (exactly as pinned by SqlOnErrorGotoRenderTest).
        factory.NewEntitySQLOnErrorGoto(108, "PC-ERR-DB2");

        CEntitySQLDeleteStatement delete =
            new CEntitySQLDeleteStatement(1, catalog, "DELETE FROM ORDERS", new Vector<>());
        String output = render(delete);
        assertEquals("sql(\"DELETE FROM ORDERS\").onErrorGoto(PC_ERR_DB2) ;",
            output.trim());
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog, new MockJavaExporter());
        CEntitySQLDeleteStatement delete =
            factory.NewEntitySQLDeleteStatement(1, "DELETE FROM T", new Vector<>());
        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntitySQLDeleteStatement.class, delete.getClass());
    }

    @Test
    @DisplayName("the read-only getters expose the cursor, statement, parameters and clause")
    void gettersExposeCursorStatementParametersAndClause()
    {
        CObjectCatalog catalog = catalog();
        Vector<CDataEntity> params = new Vector<>();
        params.add(ref("CUSTID"));

        CEntitySQLDeleteStatement delete =
            new CEntitySQLDeleteStatement(1, catalog, "  DELETE FROM T  ", params);
        CEntitySQLCursor cursor = cursor(catalog, "CUR-1");
        delete.setCursor(cursor);

        assertSame(cursor, delete.getCursor());
        // The statement is trimmed for the template's string literal.
        assertEquals("DELETE FROM T", delete.getStatement());
        assertEquals(1, delete.getParameters().size());
        assertSame(params.get(0), delete.getParameters().get(0));
        // No WHENEVER policy registered yet: the clause is null.
        assertNull(delete.getSqlWarningErrorStatement());

        // A null catalog must not throw (render-path side-effect freedom).
        CEntitySQLDeleteStatement detached =
            new CEntitySQLDeleteStatement(1, null, "DELETE FROM T", new Vector<>());
        assertNull(detached.getSqlWarningErrorStatement());
    }
}
