package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import java.util.ArrayList;
import java.util.Vector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.SQL.CEntitySQLInsertStatement;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Embedded SQL {@code EXEC SQL INSERT INTO ... END-EXEC} rendering through the
 * recursive assembler (the production path) after the retirement of the
 * {@code generate.java.SQL.CJavaSQLInsertStatement} direct backend.
 *
 * <p>SQL INSERT is rendered as a REFERENCE-role executable child via the
 * {@code recursiveSQLInsertStatementEntity} binding. The template reads only
 * {@code entity.*} properties and emits the {@code sql("<statement>")} runtime call,
 * where the full {@code INSERT INTO ...} text is assembled in Stage 1 by the semantic
 * node's {@code getStatement()}: an optional {@code SESSION.} prefix, the target table
 * (raw name + explicit column list), then either {@code VALUES (...)} (NUMBER/STRING
 * values inlined as SQL literals via {@code ExportReference(getLine()).replace('"','\'')},
 * every other value a positional {@code #N} marker) or the {@code INSERT ... SELECT}
 * clause. Each non-inlined VALUES entry chains a 1-based {@code .value(N, <hostRef>)}
 * (the holder's index matches the {@code #N} marker numbering) and each INSERT...SELECT
 * host parameter chains a 1-based {@code .value(N, <hostRef>)}; a WHENEVER
 * SQLWARNING/SQLERROR policy (a read-only catalog lookup registered by the WHENEVER
 * statement's Stage-1 side effect) chains its clause before the {@code " ;"} terminator.
 * Following the architecture principle, the non-inlined VALUES entries and the SELECT
 * host parameters remain semantic children rendered through their reference bindings;
 * neither the semantic node nor the factory flattens them to generated text. The retired
 * backend split the {@code .value(...)} chain across lines via WriteWord/WriteEOL; the
 * template emits the equivalent single-line Java builder chain (exactly as the SQL FETCH
 * migration did).
 *
 * <p>This test pins byte-for-byte parity with the retired backend's output shapes:
 * <ul>
 *   <li>column form, host variables -&gt;
 *       {@code sql("INSERT INTO T (A, B) VALUES (#1, #2)").value(1, <a>).value(2, <b>) ;}</li>
 *   <li>column form, inlined literals -&gt;
 *       {@code sql("INSERT INTO T (A, B, C) VALUES (#1, 'SMITH', 42)").value(1, <a>) ;}</li>
 *   <li>INSERT ... SELECT form -&gt;
 *       {@code sql("INSERT INTO T SELECT ...").value(1, <p>) ;}</li>
 *   <li>with a WHENEVER SQLERROR GOTO policy -&gt;
 *       {@code sql("...").onErrorGoto(<label>) ;}</li>
 * </ul>
 * Replaces the legacy {@code generate.java.SQL.CJavaSQLInsertStatement} direct backend.
 */
class SQLInsertStatementRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String render(CEntitySQLInsertStatement insert)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(insert, JavaTemplateRole.REFERENCE);
    }

    private static CDataEntity ref(String value)
    {
        return new MockDataEntity(1, value);
    }

    /**
     * A mock whose data type is forced to NUMBER/STRING so it exercises the inlined
     * SQL-literal path of {@code getStatement()} (the retired backend inlined exactly
     * the NUMBER/STRING VALUES via {@code ExportReference(getLine()).replace('"','\'')}).
     */
    private static final class LiteralMock extends MockDataEntity
    {
        private final CDataEntityType type ;

        LiteralMock(int line, String value, CDataEntityType type)
        {
            super(line, value);
            this.type = type ;
        }

        @Override
        public CDataEntityType GetDataType()
        {
            return type ;
        }
    }

    @Test
    @DisplayName("column form with host variables renders #N markers + a 1-based .value(N, <ref>) chain")
    void columnFormHostVariablesRenderValueChain()
    {
        ArrayList<String> columns = new ArrayList<>();
        columns.add("CUST_ID");
        columns.add("AMOUNT");
        Vector values = new Vector();
        values.add(ref("CUSTID"));
        values.add(ref("AMT"));

        CEntitySQLInsertStatement insert = new CEntitySQLInsertStatement(1, catalog());
        insert.SetInsert("CUSTOMER", columns, values);
        String output = render(insert);
        assertEquals(
            "sql(\"INSERT INTO CUSTOMER (CUST_ID, AMOUNT) VALUES (#1, #2)\")"
                + ".value(1, CUSTID).value(2, AMT) ;",
            output.trim());
        assertTrue(!output.contains(".onError"), output);
    }

    @Test
    @DisplayName("session-table flag adds the SESSION qualifier")
    void sessionTableAddsQualifier()
    {
        ArrayList<String> columns = new ArrayList<>();
        columns.add("CUST_ID");
        Vector values = new Vector();
        values.add(ref("CUSTID"));

        CEntitySQLInsertStatement insert = new CEntitySQLInsertStatement(1, catalog());
        insert.SetInsert("CUSTOMER", columns, values);
        insert.setSessionTable(true);

        assertEquals(
            "sql(\"INSERT INTO SESSION.CUSTOMER (CUST_ID) VALUES (#1)\")"
                + ".value(1, CUSTID) ;",
            render(insert).trim());
    }

    @Test
    @DisplayName("column form inlines NUMBER/STRING VALUES as SQL literals and only chains the non-literals")
    void columnFormInlinesLiterals()
    {
        ArrayList<String> columns = new ArrayList<>();
        columns.add("CUST_ID");
        columns.add("NAME");
        columns.add("AGE");
        Vector values = new Vector();
        values.add(ref("CUSTID"));                                   // #1 (host variable)
        values.add(new LiteralMock(1, "\"SMITH\"", CDataEntity.CDataEntityType.STRING)); // inlined 'SMITH'
        values.add(new LiteralMock(1, "42", CDataEntity.CDataEntityType.NUMBER));        // inlined 42

        CEntitySQLInsertStatement insert = new CEntitySQLInsertStatement(1, catalog());
        insert.SetInsert("CUSTOMER", columns, values);
        String output = render(insert);
        // The STRING literal's double quotes become single quotes (the retired
        // backend's .replace('"','\'')); the NUMBER literal is inlined verbatim; only
        // the host variable becomes a #1 marker + .value(1, <ref>) call.
        assertEquals(
            "sql(\"INSERT INTO CUSTOMER (CUST_ID, NAME, AGE) VALUES (#1, 'SMITH', 42)\")"
                + ".value(1, CUSTID) ;",
            output.trim());
    }

    @Test
    @DisplayName("INSERT ... SELECT form renders the clause + a 1-based .value(N, <ref>) parameter chain")
    void selectFormRendersClauseAndParameterChain()
    {
        Vector params = new Vector();
        params.add(ref("CUSTID"));

        CEntitySQLInsertStatement insert = new CEntitySQLInsertStatement(1, catalog());
        insert.SetInsert("ARCHIVE", "SELECT CUST_ID, NAME FROM CUSTOMER", params);
        String output = render(insert);
        assertEquals(
            "sql(\"INSERT INTO ARCHIVE SELECT CUST_ID, NAME FROM CUSTOMER\").value(1, CUSTID) ;",
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

        ArrayList<String> columns = new ArrayList<>();
        columns.add("CUST_ID");
        Vector values = new Vector();
        values.add(ref("CUSTID"));
        CEntitySQLInsertStatement insert = new CEntitySQLInsertStatement(1, catalog);
        insert.SetInsert("CUSTOMER", columns, values);
        String output = render(insert);
        assertEquals(
            "sql(\"INSERT INTO CUSTOMER (CUST_ID) VALUES (#1)\").value(1, CUSTID).onErrorGoto(PC_ERR_DB2) ;",
            output.trim());
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog, new MockJavaExporter());
        CEntitySQLInsertStatement insert = factory.NewEntitySQLInsertStatement(1);
        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntitySQLInsertStatement.class, insert.getClass());
    }

    @Test
    @DisplayName("the read-only getters expose the statement, value params, select params and clause")
    void gettersExposeStatementValueParamsSelectParamsAndClause()
    {
        CObjectCatalog catalog = catalog();
        ArrayList<String> columns = new ArrayList<>();
        columns.add("CUST_ID");
        columns.add("NAME");
        Vector values = new Vector();
        values.add(ref("CUSTID"));                                   // non-literal -> #1 + .value(1, ...)
        values.add(new LiteralMock(1, "42", CDataEntity.CDataEntityType.NUMBER)); // inlined literal

        CEntitySQLInsertStatement insert = new CEntitySQLInsertStatement(1, catalog);
        insert.SetInsert("CUSTOMER", columns, values);

        assertEquals("INSERT INTO CUSTOMER (CUST_ID, NAME) VALUES (#1, 42)",
            insert.getStatement());
        // Only the non-literal value becomes a .value(N, <ref>) param, keeping its
        // 1-based position in the full VALUES list.
        assertEquals(1, insert.getValueParams().size());
        assertEquals(1, insert.getValueParams().get(0).getIndex());
        assertSame(values.get(0), insert.getValueParams().get(0).getRef());
        assertTrue(insert.getSelectParams().isEmpty());
        // No WHENEVER policy registered yet: the clause is null.
        assertNull(insert.getSqlWarningErrorStatement());

        // The INSERT...SELECT form exposes its host parameters instead.
        Vector params = new Vector();
        params.add(ref("CUSTID"));
        CEntitySQLInsertStatement selectInsert = new CEntitySQLInsertStatement(1, catalog);
        selectInsert.SetInsert("ARCHIVE", "SELECT CUST_ID FROM CUSTOMER", params);
        assertEquals("INSERT INTO ARCHIVE SELECT CUST_ID FROM CUSTOMER",
            selectInsert.getStatement());
        assertTrue(selectInsert.getValueParams().isEmpty());
        assertEquals(1, selectInsert.getSelectParams().size());
        assertEquals(1, selectInsert.getSelectParams().get(0).getIndex());
        assertSame(params.get(0), selectInsert.getSelectParams().get(0).getRef());

        // A null catalog must not throw (render-path side-effect freedom).
        CEntitySQLInsertStatement detached = new CEntitySQLInsertStatement(1, null);
        assertNull(detached.getSqlWarningErrorStatement());
    }
}
