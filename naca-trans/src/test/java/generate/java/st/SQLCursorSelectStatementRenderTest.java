package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import java.util.Vector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.SQL.CEntitySQLCursor;
import semantic.SQL.CEntitySQLCursorSelectStatement;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Embedded SQL cursor {@code SELECT} rendering through the recursive assembler
 * (the production path) after the retirement of the
 * {@code generate.java.SQL.CJavaSQLCursorSelectStatement} direct backend.
 *
 * <p>The SELECT bound to a {@code DECLARE CURSOR} is {@code ignore()}d as an
 * ordinary procedure child and rendered in place of the {@code OPEN <cursor>}
 * statement, as a REFERENCE-role node via the
 * {@code recursiveSQLCursorSelectStatementEntity} binding. The template reads only
 * {@code entity.*} properties and emits the {@code cursorOpen(<cursor>, "<select>")}
 * runtime call: the cursor handle stays a semantic child rendered through its
 * {@code dataReferenceEntity} binding, the trimmed SELECT text is wrapped as a Java
 * string literal (exactly what the retired backend's
 * {@code WriteLongString(csStatement.trim())} emitted), a WITH HOLD cursor chains
 * {@code .setHoldability(true)}, each host-variable parameter chains a 1-based
 * {@code .param(N, <hostRef>)}, and a WHENEVER SQLWARNING/SQLERROR policy (a
 * read-only catalog lookup registered by the WHENEVER statement's Stage-1 side
 * effect) chains its clause before the {@code " ;"} terminator. Following the
 * architecture principle, neither the semantic node nor the factory flattens the
 * cursor or the parameters to generated text.
 *
 * <p>This test pins byte-for-byte parity with the retired backend's output shapes:
 * <ul>
 *   <li>bare open -&gt; {@code cursorOpen(<ref>, "<select>") ;}</li>
 *   <li>WITH HOLD -&gt; {@code cursorOpen(<ref>, "<select>").setHoldability(true) ;}</li>
 *   <li>with parameters -&gt;
 *       {@code cursorOpen(<ref>, "<select>").param(1, <a>).param(2, <b>) ;}</li>
 *   <li>with a WHENEVER SQLERROR GOTO policy -&gt;
 *       {@code cursorOpen(<ref>, "<select>").onErrorGoto(<label>) ;}</li>
 * </ul>
 * Replaces the legacy {@code generate.java.SQL.CJavaSQLCursorSelectStatement}
 * direct backend.
 */
class SQLCursorSelectStatementRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String render(CEntitySQLCursorSelectStatement select)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(select, JavaTemplateRole.REFERENCE);
    }

    private static CEntitySQLCursor cursor(CObjectCatalog catalog, String name)
    {
        CEntitySQLCursor cursor = new CEntitySQLCursor(name, catalog);
        cursor.setLanguageExporter(new MockJavaExporter());
        return cursor;
    }

    private static CDataEntity ref(String value)
    {
        return new MockDataEntity(1, value);
    }

    private static CEntitySQLCursorSelectStatement select(CObjectCatalog catalog,
        String statement, Vector<CDataEntity> params, boolean withHold)
    {
        CEntitySQLCursorSelectStatement select =
            new CEntitySQLCursorSelectStatement(1, catalog);
        select.SetSelect(statement, params, cursor(catalog, "CUR-1"), 1, withHold);
        return select;
    }

    @Test
    @DisplayName("bare cursor SELECT renders cursorOpen(<ref>, \"<select>\") ;")
    void bareSelectRendersCursorOpen()
    {
        CEntitySQLCursorSelectStatement select =
            select(catalog(), "SELECT CUST_NAME FROM CUSTOMERS", new Vector<>(), false);
        String output = render(select);
        assertEquals("cursorOpen(CUR_1, \"SELECT CUST_NAME FROM CUSTOMERS\") ;",
            output.trim());
        assertTrue(!output.contains(".setHoldability"), output);
        assertTrue(!output.contains(".param"), output);
        assertTrue(!output.contains(".onError"), output);
    }

    @Test
    @DisplayName("a WITH HOLD cursor chains .setHoldability(true)")
    void withHoldChainsSetHoldability()
    {
        CEntitySQLCursorSelectStatement select =
            select(catalog(), "SELECT 1 FROM SYSIBM.SYSDUMMY1", new Vector<>(), true);
        String output = render(select);
        assertEquals(
            "cursorOpen(CUR_1, \"SELECT 1 FROM SYSIBM.SYSDUMMY1\").setHoldability(true) ;",
            output.trim());
    }

    @Test
    @DisplayName("host parameters render a 1-based .param(N, <ref>) chain")
    void parametersRenderParamChain()
    {
        Vector<CDataEntity> params = new Vector<>();
        params.add(ref("CUSTID"));
        params.add(ref("AMOUNT"));
        CEntitySQLCursorSelectStatement select =
            select(catalog(), "SELECT * FROM ORDERS", params, false);
        String output = render(select);
        assertEquals(
            "cursorOpen(CUR_1, \"SELECT * FROM ORDERS\").param(1, CUSTID).param(2, AMOUNT) ;",
            output.trim());
    }

    @Test
    @DisplayName("a WHENEVER SQLERROR GOTO policy chains .onErrorGoto(<label>)")
    void sqlErrorGotoChainsClause()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        // Stage-1 side effect of EXEC SQL WHENEVER SQLERROR GOTO PC-ERR-DB2: registers
        // the policy into the catalog (exactly as pinned by SqlOnErrorGotoRenderTest).
        factory.NewEntitySQLOnErrorGoto(108, "PC-ERR-DB2");

        CEntitySQLCursorSelectStatement select =
            select(catalog, "SELECT * FROM ORDERS", new Vector<>(), false);
        String output = render(select);
        assertEquals("cursorOpen(CUR_1, \"SELECT * FROM ORDERS\").onErrorGoto(PC_ERR_DB2) ;",
            output.trim());
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        CEntitySQLCursorSelectStatement select =
            factory.NewEntitySQLCursorSelectStatement(1);
        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntitySQLCursorSelectStatement.class, select.getClass());
    }

    @Test
    @DisplayName("the read-only getters expose the cursor, statement, parameters and clause")
    void gettersExposeCursorStatementParametersAndClause()
    {
        CObjectCatalog catalog = catalog();
        Vector<CDataEntity> params = new Vector<>();
        params.add(ref("CUSTID"));

        CEntitySQLCursorSelectStatement select =
            new CEntitySQLCursorSelectStatement(1, catalog);
        CEntitySQLCursor cursor = cursor(catalog, "CUR-1");
        select.SetSelect("  SELECT * FROM T  ", params, cursor, 3, true);

        assertSame(cursor, select.getCursor());
        // The statement is trimmed for the template's string literal.
        assertEquals("SELECT * FROM T", select.getStatement());
        assertEquals(1, select.getParameters().size());
        assertSame(params.get(0), select.getParameters().get(0));
        assertTrue(select.isWithHold());
        assertEquals(3, select.GetNbColumns());
        // No WHENEVER policy registered yet: the clause is null.
        assertNull(select.getSqlWarningErrorStatement());

        // A null catalog must not throw (render-path side-effect freedom).
        CEntitySQLCursorSelectStatement detached =
            new CEntitySQLCursorSelectStatement(1, null);
        assertNull(detached.getSqlWarningErrorStatement());
    }
}
