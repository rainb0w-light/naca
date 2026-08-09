package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.SQL.CEntitySQLCode;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityExprTerminal;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Embedded SQL {@code SQLCODE} / {@code SQLERRD} data reference rendering through
 * the recursive assembler (the production path).
 *
 * <p>The SQLCODE data reference is rendered as a REFERENCE-role child via the
 * {@code recursiveSQLCodeEntity} binding. The template reads only {@code entity.*}
 * properties and emits the {@code getSQLCode()} BaseProgram call, or
 * {@code getSQLDiagnosticCode(<historyItem>)} when an {@code SQLERRD(n)}
 * history-item expression is present (recursively rendered through its reference
 * binding). Following the architecture principle, the semantic entity carries only
 * a read-only getter (the optional history-item expression) and no string logic;
 * the recursive assembler resolves the pure semantic type through its binding when
 * the reference appears anywhere in an expression tree.
 *
 * <p>This test pins byte-for-byte parity with the retired backend's output shapes:
 * <ul>
 *   <li>plain {@code SQL-CODE} -&gt; {@code getSQLCode()}</li>
 *   <li>{@code SQLERRD(n)} -&gt; {@code getSQLDiagnosticCode(<n>)}</li>
 * </ul>
 * Replaces the legacy {@code generate.java.SQL.CJavaSQLCode} direct backend.
 */
class SQLCodeRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String render(CEntitySQLCode sqlCode)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(sqlCode, JavaTemplateRole.REFERENCE);
    }

    private static CEntitySQLCode plain()
    {
        return new CEntitySQLCode("SQLCODE", catalog());
    }

    /** A renderable history-item expression standing in for an SQLERRD(n) index. */
    private static CBaseEntityExpression historyItem(String referenceValue)
    {
        return new CEntityExprTerminal(new MockDataEntity(1, referenceValue)) {};
    }

    private static CEntitySQLCode withHistoryItem(CBaseEntityExpression historyItem)
    {
        return new CEntitySQLCode("SQLERRD", catalog())
        {
            @Override
            public CBaseEntityExpression getHistoryItem()
            {
                return historyItem;
            }
        };
    }

    @Test
    @DisplayName("a plain SQL-CODE reference renders getSQLCode()")
    void plainSqlCodeRendersGetSqlCode()
    {
        assertEquals("getSQLCode()", render(plain()).trim());
    }

    @Test
    @DisplayName("an SQLERRD(n) reference renders getSQLDiagnosticCode(<historyItem>)")
    void diagnosticReferenceRendersGetSqlDiagnosticCode()
    {
        String output = render(withHistoryItem(historyItem("nIdx")));
        assertEquals("getSQLDiagnosticCode(nIdx)", output.trim());
    }

    @Test
    @DisplayName("the recursive assembler resolves the pure semantic type inside an expression terminal")
    void referenceComposesThroughTheAssembler()
    {
        // The reference reached as the terminal of an expression resolves through
        // the same binding (superclass dispatch on the pure semantic type).
        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(new CEntityExprTerminal(plain()) {}, JavaTemplateRole.REFERENCE);
        assertTrue(output.contains("getSQLCode()"), output);
    }

    @Test
    @DisplayName("the ST4 factory builds the pure semantic entity and it renders")
    void factoryBuildsPureSemanticEntity()
    {
        CJavaEntityFactory factory = new CJavaEntityFactory(catalog(), new MockJavaExporter());
        CEntitySQLCode sqlCode = factory.NewEntitySQLCode("SQLCODE");
        // Exactly the semantic type: no target-backend subclass in between.
        assertEquals(CEntitySQLCode.class, sqlCode.getClass());
        assertEquals("getSQLCode()", render(sqlCode).trim());
    }

    @Test
    @DisplayName("the read-only getter exposes the (absent) history item by default")
    void getterExposesHistoryItem()
    {
        assertNull(plain().getHistoryItem());
    }

    @Test
    @DisplayName("semantic SQLCODE exposes accessor capability without rendered source")
    void semanticAccessorCapabilityPreserved()
    {
        CEntitySQLCode sqlCode = plain();
        assertTrue(sqlCode.HasAccessors());
        assertEquals("", sqlCode.GetConstantValue());
    }
}
