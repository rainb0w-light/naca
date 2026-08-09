package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.SQL.CEntitySQLSingleStatement;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Embedded SQL single statement (a raw {@code EXEC SQL <text> END-EXEC} that
 * has no dedicated statement entity) rendering through the recursive assembler
 * (the production path).
 *
 * <p>SQL single statement is rendered as a REFERENCE-role executable child via
 * the {@code recursiveSQLSingleStatementEntity} binding. The template reads
 * only {@code entity.*} properties and emits the legacy
 * {@code getDBConnection().execSQL("<statement>")} runtime call, the raw
 * statement text being carried by the entity and exposed through a read-only
 * getter ({@link CEntitySQLSingleStatement#getStatement()}). Unlike the
 * cursor-bound SQL statement entities, no WHENEVER SQLWARNING/SQLERROR clause
 * is chained: the retired direct backend never read the policy, and the
 * template mirrors that exactly. Following the architecture principle, no
 * formatting happens in the semantic node: the statement text is exposed
 * through a read-only getter and the template does the wrapping.
 *
 * <p>This test pins byte-for-byte parity with the retired backend's output
 * shape:
 * <ul>
 *   <li>bare single statement -&gt;
 *       {@code getDBConnection().execSQL("<statement>") ;}</li>
 * </ul>
 * Replaces the legacy {@code generate.java.SQL.CJavaSQLSingleStatement} direct
 * backend.
 */
class SQLSingleStatementRenderTest
{
    private static final String SQL_TEXT = "DELETE FROM RS3151 WHERE C1 = 1";

    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String render(CEntitySQLSingleStatement statement)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(statement, JavaTemplateRole.REFERENCE);
    }

    @Test
    @DisplayName("bare single statement renders getDBConnection().execSQL(\"<statement>\") ;")
    void bareSingleStatementRendersExecSqlCall()
    {
        CEntitySQLSingleStatement statement =
            new CEntitySQLSingleStatement(1, catalog(), SQL_TEXT);
        String output = render(statement);
        assertEquals("getDBConnection().execSQL(\"DELETE FROM RS3151 WHERE C1 = 1\") ;",
            output.trim());
        assertTrue(!output.contains(".onError"), output);
    }

    @Test
    @DisplayName("the ST4 factory returns the pure semantic entity (no CJava* backend)")
    void factoryReturnsPureSemanticEntity()
    {
        CObjectCatalog catalog = catalog();
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog, new MockJavaExporter());
        CEntitySQLSingleStatement statement =
            factory.NewEntitySQLSingleStatement(1, SQL_TEXT);
        // Exactly the pure semantic class, not a legacy CJava* controller subclass.
        assertEquals(CEntitySQLSingleStatement.class, statement.getClass());
        assertEquals("getDBConnection().execSQL(\"DELETE FROM RS3151 WHERE C1 = 1\") ;",
            render(statement).trim());
    }

    @Test
    @DisplayName("the read-only getter exposes the raw statement text verbatim")
    void getterExposesStatementVerbatim()
    {
        CEntitySQLSingleStatement statement =
            new CEntitySQLSingleStatement(1, catalog(), SQL_TEXT);
        // The statement text is the raw SQL carried by the entity (constructor
        // argument), exactly what the retired backend concatenated into
        // getDBConnection().execSQL("...").
        assertEquals(SQL_TEXT, statement.getStatement());

        // A null catalog must not affect the render-path getter (no catalog
        // lookup happens: the template chains no WHENEVER clause).
        CEntitySQLSingleStatement detached = new CEntitySQLSingleStatement(1, null, SQL_TEXT);
        assertEquals(SQL_TEXT, detached.getStatement());
        assertEquals("getDBConnection().execSQL(\"DELETE FROM RS3151 WHERE C1 = 1\") ;",
            render(detached).trim());
    }
}
