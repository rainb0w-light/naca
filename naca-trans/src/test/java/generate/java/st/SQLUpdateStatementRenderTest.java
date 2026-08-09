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
import semantic.SQL.CEntitySQLUpdateStatement;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Embedded SQL {@code EXEC SQL UPDATE ... END-EXEC} rendering through the recursive
 * assembler (the production path) after the retirement of the
 * {@code generate.java.SQL.CJavaSQLUpdateStatement} direct backend.
 *
 * <p>SQL UPDATE is rendered as a REFERENCE-role executable child via the
 * {@code recursiveSQLUpdateStatementEntity} binding. The template reads only
 * {@code entity.*} properties and emits the {@code sql("<statement>")} runtime call
 * for a standalone UPDATE, or {@code cursorUpdateCurrent(<cursor>, "<statement>")}
 * when a cursor is bound: the cursor handle stays a semantic child rendered through
 * its {@code dataReferenceEntity} binding, the trimmed UPDATE text is wrapped as a
 * Java string literal (exactly what the retired backend's
 * {@code WriteLongString(csStatement.trim())} emitted), each SET host-variable value
 * chains a 1-based {@code .value(N, <hostRef>)}, each non-null WHERE host-variable
 * parameter chains a {@code .param(N, <hostRef>)} whose position continues the SET
 * values' numbering (the retired backend's {@code (i+1+sets.size())} index), and a
 * WHENEVER SQLWARNING/SQLERROR policy (a read-only catalog lookup registered by the
 * WHENEVER statement's Stage-1 side effect) chains its clause before the {@code " ;"}
 * terminator. Following the architecture principle, neither the semantic node nor the
 * factory flattens the cursor, the SET values or the parameters to generated text.
 *
 * <p>This test pins byte-for-byte parity with the retired backend's output shapes:
 * <ul>
 *   <li>bare UPDATE -&gt; {@code sql("<statement>") ;}</li>
 *   <li>with SET values -&gt;
 *       {@code sql("<statement>").value(1, <a>).value(2, <b>) ;}</li>
 *   <li>with SET values and WHERE parameters -&gt;
 *       {@code sql("<statement>").value(1, <a>).param(2, <p>) ;} (parameters keep
 *       numbering after the SET values)</li>
 *   <li>a null WHERE parameter is dropped but numbering is preserved</li>
 *   <li>bound cursor -&gt; {@code cursorUpdateCurrent(<ref>, "<statement>") ;}</li>
 *   <li>with a WHENEVER SQLERROR GOTO policy -&gt;
 *       {@code sql("<statement>").onErrorGoto(<label>) ;}</li>
 * </ul>
 * Replaces the legacy {@code generate.java.SQL.CJavaSQLUpdateStatement} direct
 * backend.
 */
class SQLUpdateStatementRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String render(CEntitySQLUpdateStatement update)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(update, JavaTemplateRole.REFERENCE);
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
    @DisplayName("bare UPDATE renders sql(\"<statement>\") ;")
    void bareUpdateRendersSql()
    {
        CEntitySQLUpdateStatement update =
            new CEntitySQLUpdateStatement(1, catalog(),
                "UPDATE CUSTOMERS SET STATUS = 1", new Vector<>(), new Vector<>());
        String output = render(update);
        assertEquals("sql(\"UPDATE CUSTOMERS SET STATUS = 1\") ;", output.trim());
        assertTrue(!output.contains(".value"), output);
        assertTrue(!output.contains(".param"), output);
        assertTrue(!output.contains(".onError"), output);
        assertTrue(!output.contains("cursorUpdateCurrent"), output);
    }

    @Test
    @DisplayName("SET values render a 1-based .value(N, <ref>) chain")
    void setValuesRenderValueChain()
    {
        Vector<CDataEntity> sets = new Vector<>();
        sets.add(ref("CUSTNAME"));
        sets.add(ref("STATUS"));
        CEntitySQLUpdateStatement update =
            new CEntitySQLUpdateStatement(1, catalog(),
                "UPDATE CUSTOMERS SET NAME = ?, STATUS = ?", sets, new Vector<>());
        String output = render(update);
        assertEquals(
            "sql(\"UPDATE CUSTOMERS SET NAME = ?, STATUS = ?\")"
                + ".value(1, CUSTNAME).value(2, STATUS) ;",
            output.trim());
    }

    @Test
    @DisplayName("WHERE parameters continue the SET values' numbering (.param(N, <ref>))")
    void parametersContinueSetValueNumbering()
    {
        Vector<CDataEntity> sets = new Vector<>();
        sets.add(ref("CUSTNAME"));
        Vector<CDataEntity> params = new Vector<>();
        params.add(ref("CUSTID"));
        CEntitySQLUpdateStatement update =
            new CEntitySQLUpdateStatement(1, catalog(),
                "UPDATE CUSTOMERS SET NAME = ? WHERE ID = ?", sets, params);
        String output = render(update);
        // 1 SET -> .value(1, ...); the single WHERE parameter is numbered (1+1) = 2.
        assertEquals(
            "sql(\"UPDATE CUSTOMERS SET NAME = ? WHERE ID = ?\")"
                + ".value(1, CUSTNAME).param(2, CUSTID) ;",
            output.trim());
    }

    @Test
    @DisplayName("a null WHERE parameter is dropped but numbering is preserved")
    void nullParameterDroppedNumberingPreserved()
    {
        Vector<CDataEntity> sets = new Vector<>();
        sets.add(ref("CUSTNAME"));
        Vector<CDataEntity> params = new Vector<>();
        params.add(null);
        params.add(ref("AMOUNT"));
        CEntitySQLUpdateStatement update =
            new CEntitySQLUpdateStatement(1, catalog(),
                "UPDATE ORDERS SET NAME = ? WHERE A = ? AND B = ?", sets, params);
        String output = render(update);
        // Null parameter at index 0 dropped; AMOUNT keeps (1+1+1) = 3 numbering.
        assertEquals(
            "sql(\"UPDATE ORDERS SET NAME = ? WHERE A = ? AND B = ?\")"
                + ".value(1, CUSTNAME).param(3, AMOUNT) ;",
            output.trim());
    }

    @Test
    @DisplayName("a bound cursor renders cursorUpdateCurrent(<ref>, \"<statement>\") ;")
    void boundCursorRendersCursorUpdateCurrent()
    {
        Vector<CDataEntity> sets = new Vector<>();
        sets.add(ref("STATUS"));
        CEntitySQLUpdateStatement update =
            new CEntitySQLUpdateStatement(1, catalog(),
                "UPDATE CUSTOMERS SET STATUS = ?", sets, new Vector<>());
        update.setCursor(cursor(catalog(), "CUR-1"));
        String output = render(update);
        assertEquals(
            "cursorUpdateCurrent(CUR_1, \"UPDATE CUSTOMERS SET STATUS = ?\")"
                + ".value(1, STATUS) ;",
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

        CEntitySQLUpdateStatement update =
            new CEntitySQLUpdateStatement(1, catalog,
                "UPDATE CUSTOMERS SET STATUS = 1", new Vector<>(), new Vector<>());
        String output = render(update);
        assertEquals("sql(\"UPDATE CUSTOMERS SET STATUS = 1\").onErrorGoto(PC_ERR_DB2) ;",
            output.trim());
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog, new MockJavaExporter());
        CEntitySQLUpdateStatement update =
            factory.NewEntitySQLUpdateStatement(1, "UPDATE T SET A = 1",
                new Vector<>(), new Vector<>());
        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntitySQLUpdateStatement.class, update.getClass());
    }

    @Test
    @DisplayName("the read-only getters expose the cursor, statement, sets, params and clause")
    void gettersExposeCursorStatementSetsParamsAndClause()
    {
        CObjectCatalog catalog = catalog();
        Vector<CDataEntity> sets = new Vector<>();
        sets.add(ref("CUSTNAME"));
        Vector<CDataEntity> params = new Vector<>();
        params.add(ref("CUSTID"));

        CEntitySQLUpdateStatement update =
            new CEntitySQLUpdateStatement(1, catalog, "  UPDATE T SET A = ? WHERE B = ?  ",
                sets, params);
        CEntitySQLCursor cursor = cursor(catalog, "CUR-1");
        update.setCursor(cursor);

        assertSame(cursor, update.getCursor());
        // The statement is trimmed for the template's string literal.
        assertEquals("UPDATE T SET A = ? WHERE B = ?", update.getStatement());
        assertEquals(1, update.getSets().size());
        assertSame(sets.get(0), update.getSets().get(0));
        // The single parameter continues the SET numbering: index (0+1+1) = 2.
        assertEquals(1, update.getParamBindings().size());
        assertEquals(2, update.getParamBindings().get(0).getIndex());
        assertSame(params.get(0), update.getParamBindings().get(0).getRef());
        // No WHENEVER policy registered yet: the clause is null.
        assertNull(update.getSqlWarningErrorStatement());

        // A null catalog must not throw (render-path side-effect freedom).
        CEntitySQLUpdateStatement detached =
            new CEntitySQLUpdateStatement(1, null, "UPDATE T SET A = 1",
                new Vector<>(), new Vector<>());
        assertNull(detached.getSqlWarningErrorStatement());
    }
}
