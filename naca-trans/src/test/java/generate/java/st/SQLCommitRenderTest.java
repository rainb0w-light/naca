package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.SQL.CEntitySQLCommit;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Embedded SQL {@code EXEC SQL COMMIT END-EXEC} rendering through the recursive
 * assembler (the production path).
 *
 * <p>SQL COMMIT is rendered as a REFERENCE-role executable child via the
 * {@code recursiveSQLCommitEntity} binding. The template reads only
 * {@code entity.*} properties and emits the {@code sqlCommit()} runtime call;
 * when a WHENEVER SQLWARNING/SQLERROR policy is in effect (a read-only catalog
 * lookup registered by the WHENEVER statement's Stage-1 side effect) its clause
 * chains onto the call. Following the architecture principle, no formatting
 * happens in the semantic node: the clause is exposed through a read-only getter
 * and the template does the concatenation.
 *
 * <p>This test pins byte-for-byte parity with the retired backend's output shapes:
 * <ul>
 *   <li>bare COMMIT -&gt; {@code sqlCommit() ;}</li>
 *   <li>with a WHENEVER SQLERROR GOTO policy -&gt;
 *       {@code sqlCommit().onErrorGoto(<label>) ;}</li>
 * </ul>
 * Replaces the legacy {@code generate.java.SQL.CJavaSQLCommit} direct backend.
 */
class SQLCommitRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String render(CEntitySQLCommit commit)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(commit, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("bare COMMIT renders sqlCommit() ;")
    void bareCommitRendersSqlCommit()
    {
        CEntitySQLCommit commit = new CEntitySQLCommit(1, catalog());
        String output = render(commit);
        assertEquals("sqlCommit() ;", output.trim());
        assertTrue(!output.contains(".onError"), output);
    }

    @Test
    @DisplayName("COMMIT under a WHENEVER SQLERROR GOTO policy chains .onErrorGoto(<label>)")
    void commitWithSqlErrorGotoChainsClause()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        // Stage-1 side effect of EXEC SQL WHENEVER SQLERROR GOTO PC-ERR-DB2: registers
        // the policy into the catalog (exactly as pinned by SqlOnErrorGotoRenderTest).
        factory.NewEntitySQLOnErrorGoto(108, "PC-ERR-DB2");

        CEntitySQLCommit commit = new CEntitySQLCommit(1, catalog);
        String output = render(commit);
        assertEquals("sqlCommit().onErrorGoto(PC_ERR_DB2) ;", output.trim());
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        CEntitySQLCommit commit = factory.NewEntitySQLCommit(1);
        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntitySQLCommit.class, commit.getClass());
        assertEquals("sqlCommit() ;", render(commit).trim());
    }

    @Test
    @DisplayName("the read-only getter exposes the catalog clause and is null-safe")
    void getterExposesClauseAndIsNullSafe()
    {
        CObjectCatalog catalog = catalog();

        CEntitySQLCommit commit = new CEntitySQLCommit(1, catalog);
        // No WHENEVER policy registered yet: the clause is null.
        assertNull(commit.getSqlWarningErrorStatement());

        // A null catalog must not throw (render-path side-effect freedom).
        CEntitySQLCommit detached = new CEntitySQLCommit(1, null);
        assertNull(detached.getSqlWarningErrorStatement());

        // Once a WHENEVER SQLERROR GOTO policy is registered (Stage-1 side effect),
        // the getter surfaces the chainable clause verbatim for the template.
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        factory.NewEntitySQLOnErrorGoto(108, "PC-ERR-DB2");
        assertEquals(".onErrorGoto(PC_ERR_DB2)", commit.getSqlWarningErrorStatement());
    }
}
