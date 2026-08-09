package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.SQL.CEntitySQLSessionDeclare;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Embedded SQL session DECLARE (the {@code DECLARE GLOBAL TEMPORARY TABLE ...}
 * statement the parser assembles token-by-token in
 * {@code CExecSQLSessionDeclare}) rendering through the recursive assembler
 * (the production path).
 *
 * <p>SQL session DECLARE is rendered as a REFERENCE-role executable child via
 * the {@code recursiveSQLSessionDeclareEntity} binding. The template reads only
 * {@code entity.*} properties and emits the {@code sql("<statement>")} runtime
 * call, the full statement text being carried by the entity and exposed through
 * a read-only getter ({@link CEntitySQLSessionDeclare#getStatement()}); when a
 * WHENEVER SQLWARNING/SQLERROR policy is in effect (a read-only catalog lookup
 * registered by the WHENEVER statement's Stage-1 side effect) its clause chains
 * onto the call. Following the architecture principle, no formatting happens in
 * the semantic node: the clause is exposed through a read-only getter and the
 * template does the concatenation.
 *
 * <p>This test pins byte-for-byte parity with the retired backend's output
 * shapes:
 * <ul>
 *   <li>bare DECLARE -&gt; {@code sql("DECLARE GLOBAL TEMPORARY TABLE
 *       SESSION.T1 (C1 INTEGER)") ;}</li>
 *   <li>with a WHENEVER SQLERROR GOTO policy -&gt;
 *       {@code sql("DECLARE GLOBAL ...").onErrorGoto(<label>) ;}</li>
 * </ul>
 * Replaces the legacy {@code generate.java.SQL.CJavaSQLSessionDeclare} direct
 * backend.
 */
class SQLSessionDeclareRenderTest
{
    private static final String DECLARE_TEXT =
        "DECLARE GLOBAL TEMPORARY TABLE SESSION.T1 (C1 INTEGER)";

    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String render(CEntitySQLSessionDeclare session)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(session, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("bare session DECLARE renders sql(\"<statement>\") ;")
    void bareSessionDeclareRendersSqlCall()
    {
        CEntitySQLSessionDeclare session = new CEntitySQLSessionDeclare(1, catalog());
        session.setSql(DECLARE_TEXT);
        String output = render(session);
        assertEquals("sql(\"DECLARE GLOBAL TEMPORARY TABLE SESSION.T1 (C1 INTEGER)\") ;",
            output.trim());
        assertTrue(!output.contains(".onError"), output);
    }

    @Test
    @DisplayName("session DECLARE under a WHENEVER SQLERROR GOTO policy chains .onErrorGoto(<label>)")
    void sessionDeclareWithSqlErrorGotoChainsClause()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog, new MockJavaExporter());
        // Stage-1 side effect of EXEC SQL WHENEVER SQLERROR GOTO PC-ERR-DB2: registers
        // the policy into the catalog (exactly as pinned by SqlOnErrorGotoRenderTest).
        factory.NewEntitySQLOnErrorGoto(108, "PC-ERR-DB2");

        CEntitySQLSessionDeclare session = new CEntitySQLSessionDeclare(1, catalog);
        session.setSql(DECLARE_TEXT);
        String output = render(session);
        assertEquals(
            "sql(\"DECLARE GLOBAL TEMPORARY TABLE SESSION.T1 (C1 INTEGER)\").onErrorGoto(PC_ERR_DB2) ;",
            output.trim());
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog, new MockJavaExporter());
        CEntitySQLSessionDeclare session = factory.NewEntitySQLSessionDeclare(1);
        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntitySQLSessionDeclare.class, session.getClass());
        session.setSql(DECLARE_TEXT);
        assertEquals("sql(\"DECLARE GLOBAL TEMPORARY TABLE SESSION.T1 (C1 INTEGER)\") ;",
            render(session).trim());
    }

    @Test
    @DisplayName("the read-only getters expose the statement text and the catalog clause, null-safe")
    void gettersExposeStatementAndClauseAndAreNullSafe()
    {
        CObjectCatalog catalog = catalog();

        CEntitySQLSessionDeclare session = new CEntitySQLSessionDeclare(1, catalog);
        // The statement text is the raw SQL carried by the entity (set by the
        // parser via setSql), exactly what the retired backend's
        // WriteLongString(csSql) wrapped as a Java string literal.
        assertEquals("", session.getStatement());
        session.setSql(DECLARE_TEXT);
        assertEquals(DECLARE_TEXT, session.getStatement());

        // No WHENEVER policy registered yet: the clause is null.
        assertNull(session.getSqlWarningErrorStatement());

        // A null catalog must not throw (render-path side-effect freedom).
        CEntitySQLSessionDeclare detached = new CEntitySQLSessionDeclare(1, null);
        assertNull(detached.getSqlWarningErrorStatement());
        // The statement getter is catalog-independent: only the carried text matters.
        detached.setSql(DECLARE_TEXT);
        assertEquals(DECLARE_TEXT, detached.getStatement());

        // Once a WHENEVER SQLERROR GOTO policy is registered (Stage-1 side effect),
        // the getter surfaces the chainable clause verbatim for the template.
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog, new MockJavaExporter());
        factory.NewEntitySQLOnErrorGoto(108, "PC-ERR-DB2");
        assertEquals(".onErrorGoto(PC_ERR_DB2)", session.getSqlWarningErrorStatement());
    }
}
