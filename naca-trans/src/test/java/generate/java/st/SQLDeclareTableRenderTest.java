package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import java.util.ArrayList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.SQL.CEntitySQLDeclareTable;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Embedded SQL {@code EXEC SQL DECLARE TABLE ... END-EXEC} rendering through the
 * recursive assembler (the production path).
 *
 * <p>SQL DECLARE TABLE is rendered as a REFERENCE-role executable child via the
 * {@code recursiveSQLDeclareTableEntity} binding. The statement emits NO code: the
 * retired {@code CJavaSQLDeclareTable.DoExport} had every {@code WriteLine} commented
 * out (zero output). Its entire effect is a Stage-1 catalog side effect —
 * {@code CObjectCatalog.RegisterSQLTable(viewName, this)} applied in the entity
 * constructor during semantic analysis — so the SELECT/INSERT/UPDATE/DELETE backends
 * can later resolve the declared table's column references. Following the architecture
 * principle, the template reads only {@code entity.*} properties and renders empty.
 * This test pins both halves:
 * <ul>
 *   <li>rendering: the declaration renders to the empty string, byte-for-byte parity
 *       with the retired backend's zero output;</li>
 *   <li>Stage-1 side effect: driving the ST4 factory registers the table (by view
 *       name) into the catalog, and the factory returns the pure semantic entity
 *       (no {@code CJava*} controller).</li>
 * </ul>
 * Replaces the legacy {@code generate.java.SQL.CJavaSQLDeclareTable} direct backend.
 */
class SQLDeclareTableRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String render(CEntitySQLDeclareTable declareTable)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(declareTable, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("DECLARE TABLE renders no code")
    void declareTableRendersEmpty()
    {
        CEntitySQLDeclareTable declareTable =
            new CEntitySQLDeclareTable(1, catalog(), "MYTABLE", "MYVIEW", new ArrayList());
        assertEquals("", render(declareTable));
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity, which renders no code")
    void factoryReturnsPureSemanticEntity()
    {
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntitySQLDeclareTable declareTable =
            factory.NewEntitySQLDeclareTable(12, "MYTABLE", "MYVIEW", new ArrayList());
        assertEquals(CEntitySQLDeclareTable.class, declareTable.getClass());
        assertEquals("", render(declareTable));
    }

    @Test
    @DisplayName("the ST4 factory registers the declared table into the catalog (Stage 1)")
    void factoryRegistersTableInCatalog()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog, new MockJavaExporter());
        CEntitySQLDeclareTable declareTable =
            factory.NewEntitySQLDeclareTable(12, "MYTABLE", "MYVIEW", new ArrayList());
        // The constructor's RegisterSQLTable(viewName, this) side effect keys the table
        // by its view name, exactly as the retired backend path did; the SELECT/INSERT/
        // UPDATE/DELETE backends later resolve columns through this lookup.
        assertSame(declareTable, catalog.GetSQLTable("MYVIEW"));
    }

    @Test
    @DisplayName("the read-only ST4 accessors expose the same state as the legacy getters")
    void accessorsExposeDeclaration()
    {
        CEntitySQLDeclareTable declareTable =
            new CEntitySQLDeclareTable(1, catalog(), "MYTABLE", "MYVIEW", new ArrayList());
        assertEquals("MYTABLE", declareTable.GetTableName());
        assertEquals("MYVIEW", declareTable.GetViewName());
        assertEquals("MYTABLE", declareTable.getTableName());
        assertEquals("MYVIEW", declareTable.getViewName());
        assertEquals("", declareTable.getColumnReferences());
    }
}
