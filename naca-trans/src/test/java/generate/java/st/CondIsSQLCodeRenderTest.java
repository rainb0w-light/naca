package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactoryST;
import generate.java.expressions.CJavaCondAnd;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.SQL.CEntityCondIsSQLCode;
import semantic.expression.CBaseEntityCondition;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.COriginalLisiting;
import utils.CTransApplicationGroup;

/**
 * Embedded SQL {@code SQLCODE} condition ({@code IF SQL-CODE = n / <> n})
 * rendering through the recursive assembler (the production path).
 *
 * <p>The SQLCODE condition is rendered as a REFERENCE-role condition child via
 * the {@code recursiveCondIsSQLCodeEntity} binding. The template reads only
 * {@code entity.*} properties and emits the {@code isSQLCode(<value>)} /
 * {@code isNotSQLCode(<value>)} BaseProgram condition call; well-known SQLCODE
 * values render as {@code SQLCode.*} constants, any other value as a raw
 * numeric literal. Following the architecture principle, the semantic entity
 * carries only read-only getters (which value is tested, whether the test is
 * negated) and no string logic; the recursive assembler resolves the pure
 * semantic type through its binding when the condition appears anywhere in a
 * condition tree.
 * This test pins byte-for-byte parity with the retired backend's output shapes:
 * <ul>
 *   <li>{@code SQL-CODE = 0} -&gt; {@code isSQLCode(SQLCode.SQL_OK)}</li>
 *   <li>{@code SQL-CODE <> 100} -&gt; {@code isNotSQLCode(SQLCode.SQL_NOT_FOUND)}</li>
 *   <li>an unmapped code -&gt; a raw literal, e.g. {@code isSQLCode(-123)}</li>
 * </ul>
 * Replaces the legacy {@code generate.java.SQL.CJavaCondIsSQLCode} direct backend.
 */
class CondIsSQLCodeRenderTest
{
    private static CObjectCatalog catalog()
    {
        CGlobalCatalog global = new CGlobalCatalog(null, "", "", "");
        return new CObjectCatalog(global, new COriginalLisiting(),
            CTransApplicationGroup.EProgramType.TYPE_BATCH, null);
    }

    private static String render(CBaseEntityCondition condition)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(condition, JavaTemplateRole.REFERENCE);
    }

    private static CEntityCondIsSQLCode isEqual(int value)
    {
        CEntityCondIsSQLCode condition = new CEntityCondIsSQLCode();
        condition.setIsEqual(value);
        return condition;
    }

    private static CEntityCondIsSQLCode isNotEqual(int value)
    {
        CEntityCondIsSQLCode condition = new CEntityCondIsSQLCode();
        condition.setIsNotEqual(value);
        return condition;
    }

    @Test
    @DisplayName("well-known SQLCODE values render as SQLCode.* constants")
    void wellKnownCodesRenderAsConstants()
    {
        assertEquals("isSQLCode(SQLCode.SQL_OK)", render(isEqual(0)).trim());
        assertEquals("isSQLCode(SQLCode.SQL_NOT_FOUND)", render(isEqual(100)).trim());
        assertEquals("isSQLCode(SQLCode.SQL_MORE_THAN_ONE_ROW)", render(isEqual(-811)).trim());
        assertEquals("isSQLCode(SQLCode.SQL_DUPLICATE_INDEX_KEY)", render(isEqual(-803)).trim());
        assertEquals("isSQLCode(SQLCode.SQL_CURSOR_ALREADY_OPENED)", render(isEqual(-502)).trim());
        assertEquals("isSQLCode(SQLCode.SQL_CURSOR_NOT_OPEN)", render(isEqual(-501)).trim());
        assertEquals("isSQLCode(SQLCode.SQL_VALUE_NULL)", render(isEqual(-305)).trim());
    }

    @Test
    @DisplayName("unmapped SQLCODE values render as raw numeric literals")
    void unmappedCodesRenderAsRawLiterals()
    {
        assertEquals("isSQLCode(-123)", render(isEqual(-123)).trim());
        assertEquals("isSQLCode(42)", render(isEqual(42)).trim());
        assertEquals("isNotSQLCode(-999)", render(isNotEqual(-999)).trim());
    }

    @Test
    @DisplayName("a negated condition renders isNotSQLCode(...) for the same value")
    void negatedConditionRendersIsNotSqlCode()
    {
        assertEquals("isNotSQLCode(SQLCode.SQL_OK)", render(isNotEqual(0)).trim());
        assertEquals("isNotSQLCode(SQLCode.SQL_NOT_FOUND)", render(isNotEqual(100)).trim());
    }

    @Test
    @DisplayName("GetOppositeCondition flips equality and preserves the tested value")
    void oppositeConditionFlipsEqualityOnly()
    {
        CBaseEntityCondition oppositeOfEqual = isEqual(0).GetOppositeCondition();
        assertEquals("isNotSQLCode(SQLCode.SQL_OK)", render(oppositeOfEqual).trim());

        CBaseEntityCondition oppositeOfNotEqual = isNotEqual(-811).GetOppositeCondition();
        assertEquals("isSQLCode(SQLCode.SQL_MORE_THAN_ONE_ROW)",
            render(oppositeOfNotEqual).trim());

        // The opposite of the opposite is the original condition shape.
        CBaseEntityCondition twice = oppositeOfNotEqual.GetOppositeCondition();
        assertEquals("isNotSQLCode(SQLCode.SQL_MORE_THAN_ONE_ROW)", render(twice).trim());
    }

    @Test
    @DisplayName("the recursive assembler resolves the pure semantic type inside a condition tree")
    void conditionComposesThroughTheAssembler()
    {
        CJavaCondAnd and = new CJavaCondAnd();
        and.SetCondition(isEqual(0), isNotEqual(100));
        String output = render(and);
        assertTrue(output.contains("isSQLCode(SQLCode.SQL_OK)"), output);
        assertTrue(output.contains("&&"), output);
        assertTrue(output.contains("isNotSQLCode(SQLCode.SQL_NOT_FOUND)"), output);
    }

    @Test
    @DisplayName("the ST4 factory builds the pure semantic entity and it renders")
    void factoryBuildsPureSemanticEntity()
    {
        CJavaEntityFactoryST factory = new CJavaEntityFactoryST(catalog(), new MockJavaExporter());
        CEntityCondIsSQLCode condition = factory.NewEntityCondIsSQLCode();
        // Exactly the semantic type: no target-backend subclass in between.
        assertEquals(CEntityCondIsSQLCode.class, condition.getClass());
        condition.setIsEqual(0);
        assertEquals("isSQLCode(SQLCode.SQL_OK)", render(condition).trim());
    }

    @Test
    @DisplayName("the read-only getters expose the tested value and the negation flag")
    void gettersExposeValueAndNegation()
    {
        CEntityCondIsSQLCode equal = isEqual(0);
        assertEquals("SQL_OK", equal.getSqlCodeConstantName());
        assertEquals(0, equal.getSqlCodeNumber());
        assertTrue(!equal.isOpposite());

        CEntityCondIsSQLCode unmapped = isNotEqual(-123);
        assertNull(unmapped.getSqlCodeConstantName());
        assertEquals(-123, unmapped.getSqlCodeNumber());
        assertTrue(unmapped.isOpposite());
    }
}
