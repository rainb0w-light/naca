package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.SQL.CEntitySQLRollBack;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Embedded SQL {@code EXEC SQL ROLLBACK END-EXEC} rendering through the recursive
 * assembler (the production path).
 *
 * <p>SQL ROLLBACK is rendered as a REFERENCE-role executable child via the
 * {@code recursiveSQLRollBackEntity} binding. The template reads only
 * {@code entity.*} properties and emits the {@code sqlRollback()} runtime call;
 * when a WHENEVER SQLWARNING/SQLERROR policy is in effect (a read-only catalog
 * lookup registered by the WHENEVER statement's Stage-1 side effect) its clause
 * chains onto the call. Following the architecture principle, no formatting
 * happens in the semantic node: the clause is exposed through a read-only getter
 * and the template does the concatenation.
 *
 * <p>This test pins byte-for-byte parity with the retired backend's output shapes:
 * <ul>
 *   <li>bare ROLLBACK -&gt; {@code sqlRollback() ;}</li>
 *   <li>with a WHENEVER SQLERROR GOTO policy -&gt;
 *       {@code sqlRollback().onErrorGoto(<label>) ;}</li>
 * </ul>
 * Replaces the legacy {@code generate.java.SQL.CJavaSQLRollBack} direct backend.
 */
class SQLRollBackRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String render(CEntitySQLRollBack rollback)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(rollback, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("bare ROLLBACK renders sqlRollback() ;")
    void bareRollBackRendersSqlRollback()
    {
        CEntitySQLRollBack rollback = new CEntitySQLRollBack(1, catalog());
        String output = render(rollback);
        assertEquals("sqlRollback() ;", output.trim());
        assertTrue(!output.contains(".onError"), output);
    }

    @Test
    @DisplayName("ROLLBACK under a WHENEVER SQLERROR GOTO policy chains .onErrorGoto(<label>)")
    void rollBackWithSqlErrorGotoChainsClause()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog, new MockJavaExporter());
        // Stage-1 side effect of EXEC SQL WHENEVER SQLERROR GOTO PC-ERR-DB2: registers
        // the policy into the catalog (exactly as pinned by SqlOnErrorGotoRenderTest).
        factory.NewEntitySQLOnErrorGoto(108, "PC-ERR-DB2");

        CEntitySQLRollBack rollback = new CEntitySQLRollBack(1, catalog);
        String output = render(rollback);
        assertEquals("sqlRollback().onErrorGoto(PC_ERR_DB2) ;", output.trim());
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog, new MockJavaExporter());
        CEntitySQLRollBack rollback = factory.NewEntitySQLRollBack(1);
        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntitySQLRollBack.class, rollback.getClass());
        assertEquals("sqlRollback() ;", render(rollback).trim());
    }

    @Test
    @DisplayName("the read-only getter exposes the catalog clause and is null-safe")
    void getterExposesClauseAndIsNullSafe()
    {
        CObjectCatalog catalog = catalog();

        CEntitySQLRollBack rollback = new CEntitySQLRollBack(1, catalog);
        // No WHENEVER policy registered yet: the clause is null.
        assertNull(rollback.getSqlWarningErrorStatement());

        // A null catalog must not throw (render-path side-effect freedom).
        CEntitySQLRollBack detached = new CEntitySQLRollBack(1, null);
        assertNull(detached.getSqlWarningErrorStatement());

        // Once a WHENEVER SQLERROR GOTO policy is registered (Stage-1 side effect),
        // the getter surfaces the chainable clause verbatim for the template.
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog, new MockJavaExporter());
        factory.NewEntitySQLOnErrorGoto(108, "PC-ERR-DB2");
        assertEquals(".onErrorGoto(PC_ERR_DB2)", rollback.getSqlWarningErrorStatement());
    }
}
