package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.SQL.CEntitySQLLock;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Embedded SQL {@code EXEC SQL LOCK TABLE <table> IN EXCLUSIVE MODE END-EXEC}
 * rendering through the recursive assembler (the production path).
 *
 * <p>SQL LOCK TABLE is rendered as a REFERENCE-role executable child via the
 * {@code recursiveSQLLockEntity} binding. The template reads only
 * {@code entity.*} properties and emits the
 * {@code sql("LOCK TABLE <table> IN EXCLUSIVE MODE")} runtime call, the statement
 * text being assembled in Stage 1 ({@link CEntitySQLLock#getStatement()}); when a
 * WHENEVER SQLWARNING/SQLERROR policy is in effect (a read-only catalog lookup
 * registered by the WHENEVER statement's Stage-1 side effect) its clause chains
 * onto the call. Following the architecture principle, no formatting happens in
 * the semantic node: the clause is exposed through a read-only getter and the
 * template does the concatenation.
 *
 * <p>This test pins byte-for-byte parity with the retired backend's output shapes:
 * <ul>
 *   <li>bare LOCK -&gt; {@code sql("LOCK TABLE RS3151 IN EXCLUSIVE MODE") ;}</li>
 *   <li>with a WHENEVER SQLERROR GOTO policy -&gt;
 *       {@code sql("LOCK TABLE RS3151 IN EXCLUSIVE MODE").onErrorGoto(<label>) ;}</li>
 * </ul>
 * Replaces the legacy {@code generate.java.SQL.CJavaSQLLock} direct backend.
 */
class SQLLockRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String render(CEntitySQLLock lock)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(lock, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("bare LOCK TABLE renders sql(\"LOCK TABLE <table> IN EXCLUSIVE MODE\") ;")
    void bareLockRendersSqlCall()
    {
        CEntitySQLLock lock = new CEntitySQLLock(1, catalog());
        lock.setTable("RS3151");
        String output = render(lock);
        assertEquals("sql(\"LOCK TABLE RS3151 IN EXCLUSIVE MODE\") ;", output.trim());
        assertTrue(!output.contains(".onError"), output);
    }

    @Test
    @DisplayName("LOCK TABLE under a WHENEVER SQLERROR GOTO policy chains .onErrorGoto(<label>)")
    void lockWithSqlErrorGotoChainsClause()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        // Stage-1 side effect of EXEC SQL WHENEVER SQLERROR GOTO PC-ERR-DB2: registers
        // the policy into the catalog (exactly as pinned by SqlOnErrorGotoRenderTest).
        factory.NewEntitySQLOnErrorGoto(108, "PC-ERR-DB2");

        CEntitySQLLock lock = new CEntitySQLLock(1, catalog);
        lock.setTable("RS3151");
        String output = render(lock);
        assertEquals("sql(\"LOCK TABLE RS3151 IN EXCLUSIVE MODE\").onErrorGoto(PC_ERR_DB2) ;",
            output.trim());
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        CEntitySQLLock lock = factory.NewEntitySQLLock(1);
        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntitySQLLock.class, lock.getClass());
        lock.setTable("RS3151");
        assertEquals("sql(\"LOCK TABLE RS3151 IN EXCLUSIVE MODE\") ;", render(lock).trim());
    }

    @Test
    @DisplayName("the read-only getters expose the statement text and the catalog clause, null-safe")
    void gettersExposeStatementAndClauseAndAreNullSafe()
    {
        CObjectCatalog catalog = catalog();

        CEntitySQLLock lock = new CEntitySQLLock(1, catalog);
        // The statement text is assembled in Stage 1 from the table name, exactly
        // as the retired backend concatenated it into sql("LOCK TABLE ...").
        assertEquals("LOCK TABLE  IN EXCLUSIVE MODE", lock.getStatement());
        lock.setTable("RS3151");
        assertEquals("LOCK TABLE RS3151 IN EXCLUSIVE MODE", lock.getStatement());

        // No WHENEVER policy registered yet: the clause is null.
        assertNull(lock.getSqlWarningErrorStatement());

        // A null catalog must not throw (render-path side-effect freedom).
        CEntitySQLLock detached = new CEntitySQLLock(1, null);
        assertNull(detached.getSqlWarningErrorStatement());
        // The statement getter is catalog-independent: only the table name matters.
        detached.setTable("RS3151");
        assertEquals("LOCK TABLE RS3151 IN EXCLUSIVE MODE", detached.getStatement());

        // Once a WHENEVER SQLERROR GOTO policy is registered (Stage-1 side effect),
        // the getter surfaces the chainable clause verbatim for the template.
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        factory.NewEntitySQLOnErrorGoto(108, "PC-ERR-DB2");
        assertEquals(".onErrorGoto(PC_ERR_DB2)", lock.getSqlWarningErrorStatement());
    }
}
