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
import semantic.SQL.CEntitySQLOpenStatement;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Embedded SQL {@code EXEC SQL OPEN <cursor>} rendering through the recursive
 * assembler (the production path) after the retirement of the
 * {@code generate.java.SQL.CJavaSQLOpenStatement} direct backend.
 *
 * <p>SQL OPEN is rendered as a REFERENCE-role executable child via the
 * {@code recursiveSQLOpenStatementEntity} binding. The template reads only
 * {@code entity.*} properties: when the cursor has a bound SELECT (its
 * {@code DECLARE CURSOR} SELECT) that semantic child is rendered in place of the
 * OPEN statement (exactly as the retired backend dispatched the select's own
 * export); otherwise the template emits the {@code cursorOpen(<ref>)} runtime
 * call, taking the optional USING descriptor/host variable semantic child
 * (rendered through its reference binding) as the second argument, and, when a
 * WHENEVER SQLWARNING/SQLERROR policy is in effect (a read-only catalog lookup
 * registered by the WHENEVER statement's Stage-1 side effect), chains its clause
 * onto the call. Following the architecture principle, the cursor, the USING
 * variable and the bound SELECT all remain semantic children recursively rendered
 * through their bindings; neither the semantic node nor the factory flattens them
 * to generated text.
 *
 * <p>This test pins byte-for-byte parity with the retired backend's output shapes:
 * <ul>
 *   <li>bare OPEN -&gt; {@code cursorOpen(<ref>) ;}</li>
 *   <li>with a USING variable -&gt; {@code cursorOpen(<ref>, <variable>) ;}</li>
 *   <li>with a WHENEVER SQLERROR GOTO policy -&gt;
 *       {@code cursorOpen(<ref>).onErrorGoto(<label>) ;}</li>
 *   <li>with a bound SELECT -&gt; the SELECT's own
 *       {@code cursorOpen(<ref>, "<select>") ;} line, rendered in place of OPEN</li>
 * </ul>
 * Replaces the legacy {@code generate.java.SQL.CJavaSQLOpenStatement} direct backend.
 */
class SQLOpenStatementRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String render(CEntitySQLOpenStatement open)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(open, JavaTemplateRole.REFERENCE);
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
    @DisplayName("bare OPEN renders cursorOpen(<ref>) ;")
    void bareOpenRendersCursorOpen()
    {
        CObjectCatalog catalog = catalog();
        CEntitySQLOpenStatement open =
            new CEntitySQLOpenStatement(1, catalog, cursor(catalog, "CUR-1"));
        String output = render(open);
        assertEquals("cursorOpen(CUR_1) ;", output.trim());
        assertTrue(!output.contains(".onError"), output);
    }

    @Test
    @DisplayName("OPEN with a USING variable renders cursorOpen(<ref>, <variable>) ;")
    void openWithVariableStatementRendersSecondArgument()
    {
        CObjectCatalog catalog = catalog();
        CEntitySQLOpenStatement open =
            new CEntitySQLOpenStatement(1, catalog, cursor(catalog, "CUR-1"));
        // Stage-1 side effect of OPEN ... USING DESCRIPTOR :WS-DSC-TAB (the cursor
        // carries the variable, which CExecSQLOpen copies onto the OPEN entity).
        open.setVariableStatement(ref("WS_DSC_TAB"));
        String output = render(open);
        assertEquals("cursorOpen(CUR_1, WS_DSC_TAB) ;", output.trim());
    }

    @Test
    @DisplayName("OPEN under a WHENEVER SQLERROR GOTO policy chains .onErrorGoto(<label>)")
    void openWithSqlErrorGotoChainsClause()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        // Stage-1 side effect of EXEC SQL WHENEVER SQLERROR GOTO PC-ERR-DB2: registers
        // the policy into the catalog (exactly as pinned by SqlOnErrorGotoRenderTest).
        factory.NewEntitySQLOnErrorGoto(108, "PC-ERR-DB2");

        CEntitySQLOpenStatement open =
            new CEntitySQLOpenStatement(1, catalog, cursor(catalog, "CUR-1"));
        String output = render(open);
        assertEquals("cursorOpen(CUR_1).onErrorGoto(PC_ERR_DB2) ;", output.trim());
    }

    @Test
    @DisplayName("OPEN of a cursor with a bound SELECT renders the SELECT in place of OPEN")
    void openWithBoundSelectRendersTheSelectInPlace()
    {
        CObjectCatalog catalog = catalog();
        CEntitySQLCursor cursor = cursor(catalog, "CUR-1");
        // The SELECT bound by DECLARE CURSOR CUR-1 CURSOR FOR SELECT ... (pinned
        // separately by SQLCursorSelectStatementRenderTest).
        CEntitySQLCursorSelectStatement select =
            new CEntitySQLCursorSelectStatement(1, catalog);
        select.SetSelect("SELECT CUST_NAME FROM CUSTOMERS", new Vector<>(), cursor, 1, false);
        cursor.SetSelect(select);

        CEntitySQLOpenStatement open = new CEntitySQLOpenStatement(1, catalog, cursor);
        String output = render(open);
        // The bound SELECT's own cursorOpen(<ref>, "<select>") line, rendered in
        // place of the bare OPEN call — exactly the retired backend's DoExport(select).
        assertEquals("cursorOpen(CUR_1, \"SELECT CUST_NAME FROM CUSTOMERS\") ;",
            output.trim());
        assertTrue(!output.contains("cursorOpen(CUR_1) ;"), output);
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        CEntitySQLCursor cursor = cursor(catalog, "CUR-1");
        CEntitySQLOpenStatement open = factory.NewEntitySQLOpenStatement(1, cursor);
        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntitySQLOpenStatement.class, open.getClass());
        assertSame(cursor, open.getCursor());
        assertEquals("cursorOpen(CUR_1) ;", render(open).trim());
    }

    @Test
    @DisplayName("the read-only getters expose the cursor, bound SELECT, variable and clause")
    void gettersExposeCursorSelectVariableAndClause()
    {
        CObjectCatalog catalog = catalog();
        CEntitySQLCursor cursor = cursor(catalog, "CUR-1");
        CDataEntity variable = ref("WS_DSC_TAB");

        CEntitySQLOpenStatement open = new CEntitySQLOpenStatement(1, catalog, cursor);
        assertSame(cursor, open.getCursor());
        // No bound SELECT on the cursor yet: the delegation getter is null.
        assertNull(open.getSelect());
        open.setVariableStatement(variable);
        assertSame(variable, open.getVariableStatement());
        // No WHENEVER policy registered yet: the clause is null.
        assertNull(open.getSqlWarningErrorStatement());

        // Once the cursor carries a bound SELECT, the OPEN entity exposes it.
        CEntitySQLCursorSelectStatement select =
            new CEntitySQLCursorSelectStatement(1, catalog);
        select.SetSelect("SELECT 1 FROM SYSIBM.SYSDUMMY1", new Vector<>(), cursor, 1, false);
        cursor.SetSelect(select);
        assertSame(select, open.getSelect());

        // A null catalog/cursor must not throw (render-path side-effect freedom).
        CEntitySQLOpenStatement detached = new CEntitySQLOpenStatement(1, null, null);
        assertNull(detached.getCursor());
        assertNull(detached.getSelect());
        assertNull(detached.getVariableStatement());
        assertNull(detached.getSqlWarningErrorStatement());
    }
}
