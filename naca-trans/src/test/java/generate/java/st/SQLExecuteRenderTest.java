package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.SQL.CEntitySQLExecute;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Embedded SQL {@code EXEC SQL EXECUTE IMMEDIATE :<host> END-EXEC} rendering
 * through the recursive assembler (the production path).
 *
 * <p>SQL EXECUTE is rendered as a REFERENCE-role executable child via the
 * {@code recursiveSQLExecuteEntity} binding. The template reads only
 * {@code entity.*} properties and emits the
 * {@code sql("EXECUTE IMMEDIATE #1").param(1, <hostRef>)} runtime call with the
 * single host-variable parameter (1-based); when a WHENEVER SQLWARNING/SQLERROR
 * policy is in effect (a read-only catalog lookup registered by the WHENEVER
 * statement's Stage-1 side effect) its clause chains onto the call. Following the
 * architecture principle, the host variable remains a semantic child and is
 * recursively rendered through its reference binding; neither the semantic node
 * nor the factory flattens it to generated text.
 *
 * <p>This test pins byte-for-byte parity with the retired backend's output shapes:
 * <ul>
 *   <li>bare EXECUTE -&gt; {@code sql("EXECUTE IMMEDIATE #1").param(1, <ref>) ;}</li>
 *   <li>with a WHENEVER SQLERROR GOTO policy -&gt;
 *       {@code sql("EXECUTE IMMEDIATE #1").param(1, <ref>).onErrorGoto(<label>) ;}</li>
 * </ul>
 * Replaces the legacy {@code generate.java.SQL.CJavaSQLExecute} direct backend.
 */
class SQLExecuteRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String render(CEntitySQLExecute execute)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(execute, JavaTemplateRole.REFERENCE);
    }

    private static CDataEntity ref(String value)
    {
        return new MockDataEntity(1, value);
    }

    @Test
    @DisplayName("bare EXECUTE renders sql(\"EXECUTE IMMEDIATE #1\").param(1, <ref>) ;")
    void bareExecuteRendersSqlExecuteImmediate()
    {
        CEntitySQLExecute execute = new CEntitySQLExecute(1, catalog());
        execute.setVar(ref("SQLTEXT"));
        String output = render(execute);
        assertEquals("sql(\"EXECUTE IMMEDIATE #1\").param(1, SQLTEXT) ;", output.trim());
        assertTrue(!output.contains(".onError"), output);
    }

    @Test
    @DisplayName("EXECUTE under a WHENEVER SQLERROR GOTO policy chains .onErrorGoto(<label>)")
    void executeWithSqlErrorGotoChainsClause()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        // Stage-1 side effect of EXEC SQL WHENEVER SQLERROR GOTO PC-ERR-DB2: registers
        // the policy into the catalog (exactly as pinned by SqlOnErrorGotoRenderTest).
        factory.NewEntitySQLOnErrorGoto(108, "PC-ERR-DB2");

        CEntitySQLExecute execute = new CEntitySQLExecute(1, catalog);
        execute.setVar(ref("SQLTEXT"));
        String output = render(execute);
        assertEquals("sql(\"EXECUTE IMMEDIATE #1\").param(1, SQLTEXT).onErrorGoto(PC_ERR_DB2) ;",
            output.trim());
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog, new MockJavaExporter());
        CEntitySQLExecute execute = factory.NewEntitySQLExecute(1);
        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntitySQLExecute.class, execute.getClass());
    }

    @Test
    @DisplayName("the read-only getters expose the host variable and catalog clause")
    void gettersExposeVariableAndClause()
    {
        CObjectCatalog catalog = catalog();
        CDataEntity var = ref("SQLTEXT");

        CEntitySQLExecute execute = new CEntitySQLExecute(1, catalog);
        execute.setVar(var);
        assertSame(var, execute.getVariable());
        // No WHENEVER policy registered yet: the clause is null.
        assertNull(execute.getSqlWarningErrorStatement());

        // A null catalog must not throw (render-path side-effect freedom).
        CEntitySQLExecute detached = new CEntitySQLExecute(1, null);
        assertNull(detached.getSqlWarningErrorStatement());
    }
}
