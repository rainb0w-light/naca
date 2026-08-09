package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.SQL.CEntitySQLCall;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Embedded SQL {@code EXEC SQL CALL <program> [USING (:host, ...)]} rendering
 * through the recursive assembler (the production path).
 *
 * <p>SQL CALL is rendered as a REFERENCE-role executable child via the
 * {@code recursiveSQLCallEntity} binding. The template reads only {@code entity.*}
 * properties and emits the {@code sqlCall(<programRef>)} runtime call with one
 * {@code .param(N, <hostRef>)} per host-variable parameter (1-based); when a
 * WHENEVER SQLWARNING/SQLERROR policy is in effect (a read-only catalog lookup
 * registered by the WHENEVER statement's Stage-1 side effect) its clause chains
 * onto the call. Following the architecture principle, the called program reference
 * and each parameter remain semantic children and are recursively rendered through
 * their reference bindings; neither the semantic node nor the factory flattens them
 * to generated text.
 *
 * <p>This test pins byte-for-byte parity with the retired backend's output shapes:
 * <ul>
 *   <li>bare CALL -&gt; {@code sqlCall(<ref>) ;}</li>
 *   <li>with parameters -&gt; {@code sqlCall(<ref>).param(1, <a>).param(2, <b>) ;}</li>
 *   <li>with a WHENEVER SQLERROR GOTO policy -&gt;
 *       {@code sqlCall(<ref>).onErrorGoto(<label>) ;}</li>
 * </ul>
 * Replaces the legacy {@code generate.java.SQL.CJavaSQLCall} direct backend.
 */
class SQLCallRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String render(CEntitySQLCall call)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(call, JavaTemplateRole.REFERENCE);
    }

    private static CDataEntity ref(String value)
    {
        return new MockDataEntity(1, value);
    }

    @Test
    @DisplayName("bare CALL renders sqlCall(<ref>) ;")
    void bareCallRendersSqlCall()
    {
        CEntitySQLCall call = new CEntitySQLCall(1, catalog());
        call.setReference(ref("MYPROG"));
        String output = render(call);
        assertEquals("sqlCall(MYPROG) ;", output.trim());
        assertTrue(!output.contains(".param"), output);
        assertTrue(!output.contains(".onError"), output);
    }

    @Test
    @DisplayName("CALL with host parameters renders a 1-based .param(N, <ref>) chain")
    void callWithParametersRendersParamChain()
    {
        CEntitySQLCall call = new CEntitySQLCall(1, catalog());
        call.setReference(ref("MYPROG"));
        call.addParameter(ref("CUSTID"));
        call.addParameter(ref("AMOUNT"));
        String output = render(call);
        assertEquals("sqlCall(MYPROG).param(1, CUSTID).param(2, AMOUNT) ;", output.trim());
    }

    @Test
    @DisplayName("CALL under a WHENEVER SQLERROR GOTO policy chains .onErrorGoto(<label>)")
    void callWithSqlErrorGotoChainsClause()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog, new MockJavaExporter());
        // Stage-1 side effect of EXEC SQL WHENEVER SQLERROR GOTO PC-ERR-DB2: registers
        // the policy into the catalog (exactly as pinned by SqlOnErrorGotoRenderTest).
        factory.NewEntitySQLOnErrorGoto(108, "PC-ERR-DB2");

        CEntitySQLCall call = new CEntitySQLCall(1, catalog);
        call.setReference(ref("MYPROG"));
        call.addParameter(ref("CUSTID"));
        String output = render(call);
        assertEquals("sqlCall(MYPROG).param(1, CUSTID).onErrorGoto(PC_ERR_DB2) ;", output.trim());
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog, new MockJavaExporter());
        CEntitySQLCall call = factory.NewEntitySQLCall(1);
        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntitySQLCall.class, call.getClass());
    }

    @Test
    @DisplayName("the read-only getters expose the reference, parameters and catalog clause")
    void gettersExposeReferenceParametersAndClause()
    {
        CObjectCatalog catalog = catalog();
        CDataEntity prog = ref("MYPROG");
        CDataEntity param = ref("CUSTID");

        CEntitySQLCall call = new CEntitySQLCall(1, catalog);
        call.setReference(prog);
        call.addParameter(param);
        assertSame(prog, call.getProgramReference());
        assertEquals(1, call.getParameters().size());
        assertSame(param, call.getParameters().get(0));
        // No WHENEVER policy registered yet: the clause is null.
        assertNull(call.getSqlWarningErrorStatement());

        // A null catalog must not throw (render-path side-effect freedom).
        CEntitySQLCall detached = new CEntitySQLCall(1, null);
        assertNull(detached.getSqlWarningErrorStatement());
    }
}
