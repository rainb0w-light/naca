package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.SQL.CEntitySQLSessionDrop;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Embedded SQL session DROP (the {@code DROP ...} statement the parser
 * assembles token-by-token in {@code CExecSQLSessionDrop}) rendering through
 * the recursive assembler (the production path).
 *
 * <p>SQL session DROP is rendered as a REFERENCE-role executable child via
 * the {@code recursiveSQLSessionDropEntity} binding. The template reads only
 * {@code entity.*} properties and emits the {@code sql("<statement>")} runtime
 * call, the full statement text being carried by the entity and exposed through
 * a read-only getter ({@link CEntitySQLSessionDrop#getStatement()}); when a
 * WHENEVER SQLWARNING/SQLERROR policy is in effect (a read-only catalog lookup
 * registered by the WHENEVER statement's Stage-1 side effect) its clause chains
 * onto the call. Following the architecture principle, no formatting happens in
 * the semantic node: the clause is exposed through a read-only getter and the
 * template does the concatenation.
 *
 * <p>This test pins byte-for-byte parity with the retired backend's output
 * shapes:
 * <ul>
 *   <li>bare DROP -&gt; {@code sql("DROP TABLE SESSION.T1") ;}</li>
 *   <li>with a WHENEVER SQLERROR GOTO policy -&gt;
 *       {@code sql("DROP TABLE SESSION.T1").onErrorGoto(<label>) ;}</li>
 * </ul>
 * Replaces the legacy {@code generate.java.SQL.CJavaSQLSessionDrop} direct
 * backend.
 */
class SQLSessionDropRenderTest
{
    private static final String DROP_TEXT = "DROP TABLE SESSION.T1";

    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String render(CEntitySQLSessionDrop session)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(session, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("bare session DROP renders sql(\"<statement>\") ;")
    void bareSessionDropRendersSqlCall()
    {
        CEntitySQLSessionDrop session = new CEntitySQLSessionDrop(1, catalog());
        session.setSql(DROP_TEXT);
        String output = render(session);
        assertEquals("sql(\"DROP TABLE SESSION.T1\") ;", output.trim());
        assertTrue(!output.contains(".onError"), output);
    }

    @Test
    @DisplayName("session DROP under a WHENEVER SQLERROR GOTO policy chains .onErrorGoto(<label>)")
    void sessionDropWithSqlErrorGotoChainsClause()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog, new MockJavaExporter());
        // Stage-1 side effect of EXEC SQL WHENEVER SQLERROR GOTO PC-ERR-DB2: registers
        // the policy into the catalog (exactly as pinned by SqlOnErrorGotoRenderTest).
        factory.NewEntitySQLOnErrorGoto(108, "PC-ERR-DB2");

        CEntitySQLSessionDrop session = new CEntitySQLSessionDrop(1, catalog);
        session.setSql(DROP_TEXT);
        String output = render(session);
        assertEquals("sql(\"DROP TABLE SESSION.T1\").onErrorGoto(PC_ERR_DB2) ;",
            output.trim());
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog, new MockJavaExporter());
        CEntitySQLSessionDrop session = factory.NewEntitySQLSessionDrop(1);
        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntitySQLSessionDrop.class, session.getClass());
        session.setSql(DROP_TEXT);
        assertEquals("sql(\"DROP TABLE SESSION.T1\") ;", render(session).trim());
    }

    @Test
    @DisplayName("the read-only getters expose the statement text and the catalog clause, null-safe")
    void gettersExposeStatementAndClauseAndAreNullSafe()
    {
        CObjectCatalog catalog = catalog();

        CEntitySQLSessionDrop session = new CEntitySQLSessionDrop(1, catalog);
        // The statement text is the raw SQL carried by the entity (set by the
        // parser via setSql), exactly what the retired backend's
        // WriteWord("sql(\"" + csSql + "\")") wrapped as a Java string literal.
        assertEquals("", session.getStatement());
        session.setSql(DROP_TEXT);
        assertEquals(DROP_TEXT, session.getStatement());

        // No WHENEVER policy registered yet: the clause is null.
        assertNull(session.getSqlWarningErrorStatement());

        // A null catalog must not throw (render-path side-effect freedom).
        CEntitySQLSessionDrop detached = new CEntitySQLSessionDrop(1, null);
        assertNull(detached.getSqlWarningErrorStatement());
        // The statement getter is catalog-independent: only the carried text matters.
        detached.setSql(DROP_TEXT);
        assertEquals(DROP_TEXT, detached.getStatement());

        // Once a WHENEVER SQLERROR GOTO policy is registered (Stage-1 side effect),
        // the getter surfaces the chainable clause verbatim for the template.
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog, new MockJavaExporter());
        factory.NewEntitySQLOnErrorGoto(108, "PC-ERR-DB2");
        assertEquals(".onErrorGoto(PC_ERR_DB2)", session.getSqlWarningErrorStatement());
    }
}
