package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.SQL.CEntitySqlOnErrorGoto;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Embedded SQL {@code EXEC SQL WHENEVER SQLERROR|SQLWARNING ...} rendering through
 * the recursive assembler (the production path).
 *
 * <p>SQL WHENEVER is rendered as a REFERENCE-role executable child via the
 * {@code recursiveSqlOnErrorGotoEntity} binding. The statement emits NO code: the
 * retired {@code CJavaSqlOnErrorGoto.DoExport} had every {@code WriteLine} commented
 * out and only registered the WHENEVER policy into {@link CObjectCatalog}. Following
 * the architecture principle, that registration moved to Stage 1
 * ({@code CJavaEntityFactory.registerSqlWheneverPolicy}, applied during semantic
 * analysis in program order); the template reads only {@code entity.*} properties and
 * renders empty. This test pins both halves:
 * <ul>
 *   <li>rendering: all four policy shapes (error/warning &times; goto/continue) render
 *       to the empty string, byte-for-byte parity with the retired backend's zero
 *       output;</li>
 *   <li>Stage-1 side effect: driving the ST4 factory registers the policy so the SQL
 *       statement backends later append {@code .onErrorGoto(<label>)}/
 *       {@code .onWarningGoto(<label>)}/{@code .onErrorContinue()}.</li>
 * </ul>
 * Replaces the legacy {@code generate.java.SQL.CJavaSqlOnErrorGoto} direct backend.
 */
class SqlOnErrorGotoRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String render(String ref, boolean onWarning)
    {
        // Null catalog: the render path must be side-effect-free (the registration is
        // a factory/Stage-1 concern), mirroring the READ/CICS RETURN render exemplars.
        CEntitySqlOnErrorGoto whenever = new CEntitySqlOnErrorGoto(1, null, ref, onWarning);
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(whenever, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("WHENEVER SQLERROR GOTO <label> renders no code")
    void sqlErrorGotoRendersEmpty()
    {
        assertEquals("", render("PC-ERR-DB2", false));
    }

    @Test
    @DisplayName("WHENEVER SQLERROR CONTINUE renders no code")
    void sqlErrorContinueRendersEmpty()
    {
        assertEquals("", render("", false));
    }

    @Test
    @DisplayName("WHENEVER SQLWARNING GOTO <label> renders no code")
    void sqlWarningGotoRendersEmpty()
    {
        assertEquals("", render("PC-WARN-DB2", true));
    }

    @Test
    @DisplayName("WHENEVER SQLWARNING CONTINUE renders no code")
    void sqlWarningContinueRendersEmpty()
    {
        assertEquals("", render("", true));
    }

    @Test
    @DisplayName("the ST4 factory registers the SQLERROR GOTO policy into the catalog (Stage 1)")
    void factoryRegistersSqlErrorGoto()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog, new MockJavaExporter());
        factory.NewEntitySQLOnErrorGoto(108, "PC-ERR-DB2");
        // MockJavaExporter.FormatIdentifier maps '-' -> '_'; the registered label is the
        // exporter-formatted identifier, exactly as the retired backend produced.
        assertEquals(".onErrorGoto(PC_ERR_DB2)", catalog.getSQLWarningErrorStatement());
    }

    @Test
    @DisplayName("the ST4 factory registers the SQLERROR CONTINUE policy into the catalog (Stage 1)")
    void factoryRegistersSqlErrorContinue()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog, new MockJavaExporter());
        factory.NewEntitySQLOnErrorGoto(108, "");
        assertEquals(".onErrorContinue()", catalog.getSQLWarningErrorStatement());
    }

    @Test
    @DisplayName("the ST4 factory registers the SQLWARNING GOTO policy into the catalog (Stage 1)")
    void factoryRegistersSqlWarningGoto()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog, new MockJavaExporter());
        factory.NewEntitySQLOnWarningGoto(108, "PC-WARN-DB2");
        String statement = catalog.getSQLWarningErrorStatement();
        assertNotNull(statement);
        assertTrue(statement.contains(".onWarningGoto(PC_WARN_DB2)"), statement);
    }

    @Test
    @DisplayName("the read-only getters expose the parsed WHENEVER policy")
    void gettersExposePolicy()
    {
        CEntitySqlOnErrorGoto whenever = new CEntitySqlOnErrorGoto(1, null, "PC-ERR-DB2", false);
        assertEquals("PC-ERR-DB2", whenever.getReference());
        assertEquals(false, whenever.isOnWarning());
    }
}
