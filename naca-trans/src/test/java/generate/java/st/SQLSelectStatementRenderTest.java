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
import semantic.SQL.CEntitySQLSelectStatement;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Embedded SQL {@code EXEC SQL SELECT ... INTO :host [, :ind] ...} rendering through
 * the recursive assembler (the production path), for the standalone non-cursor form
 * (a SELECT bound to a DECLARE CURSOR is rendered by the
 * {@code recursiveSQLCursorSelectStatementEntity} binding).
 *
 * <p>SQL SELECT is rendered as a REFERENCE-role executable child via the
 * {@code recursiveSQLSelectStatementEntity} binding. The template reads only
 * {@code entity.*} properties and emits the {@code sql("<statement>")} runtime call
 * (the prepared SELECT text assembled in Stage 1), followed by one
 * {@code .into(<host>[, <ind>])} per INTO target (each paired by position with its
 * optional INDICATOR variable) and one 1-based {@code .param(N, <hostRef>)} per
 * non-null host-variable parameter; when a WHENEVER SQLWARNING/SQLERROR policy is in
 * effect (a read-only catalog lookup registered by the WHENEVER statement's Stage-1
 * side effect) its clause chains onto the call. Following the architecture principle,
 * every parameter, INTO target and INDICATOR remain semantic children and are
 * recursively rendered through their reference bindings; neither the semantic node
 * nor the factory flattens them to generated text.
 *
 * <p>This test pins parity with the retired backend's output shapes (the legacy
 * backend split the {@code .into(...)}/{@code .param(...)} chain across lines via
 * WriteWord/WriteEOL; the template emits the equivalent single-line Java builder
 * chain):
 * <ul>
 *   <li>single INTO -&gt; {@code sql("<statement>").into(<host>) ;}</li>
 *   <li>INTO with an INDICATOR -&gt; {@code sql("<statement>").into(<host>, <ind>) ;}</li>
 *   <li>multiple INTO targets -&gt;
 *       {@code sql("<statement>").into(<a>).into(<b>, <ind>) ;}</li>
 *   <li>fewer indicators than INTO targets -&gt; trailing targets drop the indicator
 *       (the retired backend's {@code if (i < ind.size())} guard)</li>
 *   <li>host parameters -&gt; {@code sql("<statement>").into(<h>).param(1, <a>)
 *       .param(2, <b>) ;}</li>
 *   <li>a {@code null} parameter -&gt; dropped while the remaining parameters keep
 *       their original numbering (the retired backend's {@code if (cs != null)} guard
 *       and {@code (i+1)} indexing)</li>
 *   <li>with a WHENEVER SQLERROR GOTO policy -&gt;
 *       {@code sql("<statement>").into(<host>).onErrorGoto(<label>) ;}</li>
 * </ul>
 * Replaces the legacy {@code generate.java.SQL.CJavaSQLSelectStatement} direct backend.
 */
class SQLSelectStatementRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String render(CEntitySQLSelectStatement select)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(select, JavaTemplateRole.REFERENCE);
    }

    private static CDataEntity ref(String value)
    {
        return new MockDataEntity(1, value);
    }

    private static Vector<CDataEntity> vector(CDataEntity... items)
    {
        Vector<CDataEntity> v = new Vector<>();
        for (CDataEntity item : items)
        {
            v.add(item);
        }
        return v;
    }

    @Test
    @DisplayName("single INTO renders sql(\"<statement>\").into(<host>) ;")
    void singleIntoRendersSql()
    {
        CObjectCatalog catalog = catalog();
        CEntitySQLSelectStatement select = new CEntitySQLSelectStatement(1, catalog,
            "SELECT CUSTID FROM CUSTOMERS", vector(), vector(ref("CUSTID")), vector());
        String output = render(select);
        assertEquals("sql(\"SELECT CUSTID FROM CUSTOMERS\").into(CUSTID) ;", output.trim());
        assertTrue(!output.contains(".onError"), output);
    }

    @Test
    @DisplayName("INTO with an INDICATOR renders .into(<host>, <ind>)")
    void intoWithIndicatorRendersBoth()
    {
        CObjectCatalog catalog = catalog();
        CEntitySQLSelectStatement select = new CEntitySQLSelectStatement(1, catalog,
            "SELECT AMOUNT FROM ORDERS", vector(),
            vector(ref("AMOUNT")), vector(ref("AMOUNT_IND")));
        String output = render(select);
        assertEquals("sql(\"SELECT AMOUNT FROM ORDERS\").into(AMOUNT, AMOUNT_IND) ;",
            output.trim());
    }

    @Test
    @DisplayName("multiple INTO targets render a chained .into(...) per target")
    void multipleIntoTargetsRenderChain()
    {
        CObjectCatalog catalog = catalog();
        CEntitySQLSelectStatement select = new CEntitySQLSelectStatement(1, catalog,
            "SELECT CUSTID, AMOUNT FROM ORDERS", vector(),
            vector(ref("CUSTID"), ref("AMOUNT")), vector(null, ref("AMOUNT_IND")));
        String output = render(select);
        assertEquals(
            "sql(\"SELECT CUSTID, AMOUNT FROM ORDERS\").into(CUSTID).into(AMOUNT, AMOUNT_IND) ;",
            output.trim());
    }

    @Test
    @DisplayName("fewer indicators than INTO targets drops the trailing indicator")
    void fewerIndicatorsThanIntoTargets()
    {
        CObjectCatalog catalog = catalog();
        // Two INTO targets but only one indicator: the second target renders without
        // an indicator (the retired backend's if (i < ind.size()) guard).
        CEntitySQLSelectStatement select = new CEntitySQLSelectStatement(1, catalog,
            "SELECT CUSTID, AMOUNT FROM ORDERS", vector(),
            vector(ref("CUSTID"), ref("AMOUNT")), vector(ref("CUSTID_IND")));
        String output = render(select);
        assertEquals(
            "sql(\"SELECT CUSTID, AMOUNT FROM ORDERS\").into(CUSTID, CUSTID_IND).into(AMOUNT) ;",
            output.trim());
    }

    @Test
    @DisplayName("host parameters chain 1-based .param(N, <ref>) after the INTO targets")
    void parametersChainAfterInto()
    {
        CObjectCatalog catalog = catalog();
        CEntitySQLSelectStatement select = new CEntitySQLSelectStatement(1, catalog,
            "SELECT NAME FROM CUSTOMERS WHERE CUSTID = #1 AND AMOUNT = #2",
            vector(ref("CUSTID"), ref("AMOUNT")), vector(ref("WS-NAME")), vector());
        String output = render(select);
        assertEquals(
            "sql(\"SELECT NAME FROM CUSTOMERS WHERE CUSTID = #1 AND AMOUNT = #2\")"
                + ".into(WS-NAME).param(1, CUSTID).param(2, AMOUNT) ;",
            output.trim());
    }

    @Test
    @DisplayName("a null parameter is dropped while original numbering is preserved")
    void nullParameterIsSkippedKeepingNumbering()
    {
        CObjectCatalog catalog = catalog();
        // First parameter unresolved (null): dropped, and the surviving parameter
        // keeps its original position 2 (the retired backend's if (cs != null) guard
        // with (i+1) numbering).
        CEntitySQLSelectStatement select = new CEntitySQLSelectStatement(1, catalog,
            "SELECT NAME FROM CUSTOMERS WHERE A = #1 AND B = #2",
            vector(null, ref("AMOUNT")), vector(ref("WS-NAME")), vector());
        String output = render(select);
        assertEquals(
            "sql(\"SELECT NAME FROM CUSTOMERS WHERE A = #1 AND B = #2\")"
                + ".into(WS-NAME).param(2, AMOUNT) ;",
            output.trim());
    }

    @Test
    @DisplayName("SELECT under a WHENEVER SQLERROR GOTO policy chains .onErrorGoto(<label>)")
    void selectWithSqlErrorGotoChainsClause()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        // Stage-1 side effect of EXEC SQL WHENEVER SQLERROR GOTO PC-ERR-DB2: registers
        // the policy into the catalog (exactly as pinned by SqlOnErrorGotoRenderTest).
        factory.NewEntitySQLOnErrorGoto(108, "PC-ERR-DB2");

        CEntitySQLSelectStatement select = new CEntitySQLSelectStatement(1, catalog,
            "SELECT CUSTID FROM CUSTOMERS", vector(), vector(ref("CUSTID")), vector());
        String output = render(select);
        assertEquals(
            "sql(\"SELECT CUSTID FROM CUSTOMERS\").into(CUSTID).onErrorGoto(PC_ERR_DB2) ;",
            output.trim());
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        Vector<CDataEntity> into = vector(ref("CUSTID"));
        CEntitySQLSelectStatement select = factory.NewEntitySQLSelectStatement(1,
            "SELECT CUSTID FROM CUSTOMERS", vector(), into, vector());
        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntitySQLSelectStatement.class, select.getClass());
        assertEquals("SELECT CUSTID FROM CUSTOMERS", select.getStatement());
        assertSame(into.get(0), select.getIntoBindings().get(0).getInto());
    }

    @Test
    @DisplayName("the read-only getters expose the statement, INTO/param bindings and catalog clause")
    void gettersExposeStatementBindingsAndClause()
    {
        CObjectCatalog catalog = catalog();
        CDataEntity into = ref("CUSTID");
        CDataEntity indicator = ref("CUSTID-IND");
        CDataEntity param = ref("CUSTID");

        CEntitySQLSelectStatement select = new CEntitySQLSelectStatement(1, catalog,
            "  SELECT CUSTID FROM CUSTOMERS  ", vector(null, param),
            vector(into), vector(indicator));

        assertEquals("SELECT CUSTID FROM CUSTOMERS", select.getStatement());
        assertEquals(1, select.getIntoBindings().size());
        assertSame(into, select.getIntoBindings().get(0).getInto());
        assertSame(indicator, select.getIntoBindings().get(0).getIndicator());
        // The null parameter is dropped; the survivor keeps its original position 2.
        assertEquals(1, select.getParamBindings().size());
        assertEquals(2, select.getParamBindings().get(0).getIndex());
        assertSame(param, select.getParamBindings().get(0).getRef());
        // No WHENEVER policy registered yet: the clause is null.
        assertNull(select.getSqlWarningErrorStatement());

        // A null catalog / null collections must not throw (render-path side-effect freedom).
        CEntitySQLSelectStatement detached =
            new CEntitySQLSelectStatement(1, null, null, null, null, null);
        assertNull(detached.getSqlWarningErrorStatement());
        assertEquals("", detached.getStatement());
        assertTrue(detached.getIntoBindings().isEmpty());
        assertTrue(detached.getParamBindings().isEmpty());
    }
}
