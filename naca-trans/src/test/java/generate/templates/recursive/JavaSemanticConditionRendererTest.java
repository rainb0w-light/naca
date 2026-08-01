package generate.templates.recursive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.java.st.MockJavaExporter;
import generate.fixtures.LegacyNamedConditionFixture;
import generate.templates.TemplateLoader;
import org.junit.jupiter.api.Test;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityCondAnd;
import semantic.expression.CEntityCondCompare;
import semantic.expression.CEntityCondEquals;
import semantic.expression.CEntityCondIsBoolean;
import semantic.expression.CEntityCondIsConstant;
import semantic.expression.CEntityCondNot;
import semantic.expression.CEntityCondOr;
import utils.CObjectCatalog;

class JavaSemanticConditionRendererTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
    private final MockJavaExporter output = new MockJavaExporter();
    private final JavaTemplateAssembler assembler = TemplateLoader.newRecursiveAssembler();

    @Test
    void rendersEqualityAndEveryOrderedComparisonLikeTheDirectGenerator()
    {
        CEntityCondEquals equal = new CEntityCondEquals();
        equal.SetEqualCondition(number("1"), number("2"));
        assertEquals("isEqual(1, 2)",
            assembler.renderRoot(equal, JavaTemplateRole.REFERENCE));

        CEntityCondEquals different = new CEntityCondEquals();
        different.SetDifferentCondition(number("3"), number("4"));
        assertEquals("isDifferent(3, 4)",
            assembler.renderRoot(different, JavaTemplateRole.REFERENCE));

        assertEquals("isLess(7, 8)",
            assembler.renderRoot(compare(Comparison.LESS), JavaTemplateRole.REFERENCE));
        assertEquals("isLessOrEqual(7, 8)",
            assembler.renderRoot(compare(Comparison.LESS_OR_EQUAL), JavaTemplateRole.REFERENCE));
        assertEquals("isGreater(7, 8)",
            assembler.renderRoot(compare(Comparison.GREATER), JavaTemplateRole.REFERENCE));
        assertEquals("isGreaterOrEqual(7, 8)",
            assembler.renderRoot(compare(Comparison.GREATER_OR_EQUAL), JavaTemplateRole.REFERENCE));
    }

    @Test
    void recursivelyComposesLogicalConditionsWithDirectGeneratorPrecedence()
    {
        CEntityCondAnd and = new CEntityCondAnd();
        and.SetCondition(equals("1", "1"), compare(Comparison.GREATER));

        CEntityCondOr or = new CEntityCondOr();
        or.SetCondition(and, equals("2", "3"));

        CEntityCondNot not = new CEntityCondNot();
        not.SetCondition(or);

        String andOutput = assembler.renderRoot(and, JavaTemplateRole.REFERENCE);
        String orOutput = assembler.renderRoot(or, JavaTemplateRole.REFERENCE);
        String notOutput = assembler.renderRoot(not, JavaTemplateRole.REFERENCE);
        assertTrue(andOutput.contains("&&"), andOutput);
        assertTrue(orOutput.contains("||"), orOutput);
        assertTrue(notOutput.startsWith("!("), notOutput);
    }

    @Test
    void parenthesizesOrNestedInsideAndLikeTheLegacyPrecedenceRule()
    {
        CEntityCondOr or = new CEntityCondOr();
        or.SetCondition(equals("1", "2"), equals("3", "4"));
        CEntityCondAnd and = new CEntityCondAnd();
        and.SetCondition(or, equals("5", "6"));

        String output = assembler.renderRoot(and, JavaTemplateRole.REFERENCE);
        assertTrue(output.startsWith("("), output);
        assertTrue(output.contains(") \n&&"), output);
    }

    @Test
    void recursivelyRendersNamedConditionAndItsOpposite()
    {
        LegacyNamedConditionFixture named = new LegacyNamedConditionFixture(
            1, "VALID-STATUS", catalog, output);
        named.AddValue(new LegacyNumberFixture(catalog, "1"));

        assertEquals(generate.LegacyDataRenderer.renderReference(named, 1), assembler.renderRoot(named, JavaTemplateRole.REFERENCE));

        CBaseEntityCondition condition = named.GetAssociatedCondition(
            new generate.CJavaEntityFactory(catalog, output));
        String conditionOutput = assembler.renderRoot(condition, JavaTemplateRole.REFERENCE);
        String oppositeOutput = assembler.renderRoot(
            condition.GetOppositeCondition(), JavaTemplateRole.REFERENCE);
        assertEquals("is(" + generate.LegacyDataRenderer.renderReference(named, 1) + ")", conditionOutput);
        assertEquals("isNot(" + generate.LegacyDataRenderer.renderReference(named, 1) + ")", oppositeOutput);
    }

    @Test
    void rendersConstantAndBooleanPredicatesLikeTheirDirectGenerators()
    {
        LegacyInternalBoolFixture value = new LegacyInternalBoolFixture("BOOLEAN-VALUE", catalog, output);

        CEntityCondIsConstant zero = new CEntityCondIsConstant();
        zero.SetIsZero(value);
        assertEquals("isZero(BOOLEAN_VALUE)",
            assembler.renderRoot(zero, JavaTemplateRole.REFERENCE));
        assertEquals("isNotZero(BOOLEAN_VALUE)",
            assembler.renderRoot(zero.GetOppositeCondition(), JavaTemplateRole.REFERENCE));

        CEntityCondIsConstant space = new CEntityCondIsConstant();
        space.SetIsSpace(value);
        assertEquals("isSpace(BOOLEAN_VALUE)",
            assembler.renderRoot(space, JavaTemplateRole.REFERENCE));

        CEntityCondIsConstant low = new CEntityCondIsConstant();
        low.SetIsLowValue(value);
        assertEquals("isLowValue(BOOLEAN_VALUE)",
            assembler.renderRoot(low, JavaTemplateRole.REFERENCE));

        CEntityCondIsConstant high = new CEntityCondIsConstant();
        high.SetIsHighValue(value);
        assertEquals("isHighValue(BOOLEAN_VALUE)",
            assembler.renderRoot(high, JavaTemplateRole.REFERENCE));

        // The retired CFPacJavaCondIsBoolean backend is replaced by the pure
        // semantic.expression.CEntityCondIsBoolean rendered through the shared
        // recursiveCondIsBooleanEntity binding: the bare reference, "!"-prefixed
        // when the condition is negated — exactly the legacy Export() shape.
        CEntityCondIsBoolean trueCondition = new CEntityCondIsBoolean();
        trueCondition.setIsTrue(value);
        assertEquals("BOOLEAN_VALUE",
            assembler.renderRoot(trueCondition, JavaTemplateRole.REFERENCE));
        assertEquals("!BOOLEAN_VALUE",
            assembler.renderRoot(trueCondition.GetOppositeCondition(), JavaTemplateRole.REFERENCE));

        CEntityCondIsBoolean falseCondition = new CEntityCondIsBoolean();
        falseCondition.setIsFalse(value);
        assertEquals("!BOOLEAN_VALUE",
            assembler.renderRoot(falseCondition, JavaTemplateRole.REFERENCE));
        assertEquals("BOOLEAN_VALUE",
            assembler.renderRoot(falseCondition.GetOppositeCondition(), JavaTemplateRole.REFERENCE));
    }

    private CEntityCondEquals equals(String left, String right)
    {
        CEntityCondEquals condition = new CEntityCondEquals();
        condition.SetEqualCondition(number(left), number(right));
        return condition;
    }

    private CEntityCondCompare compare(Comparison comparison)
    {
        CEntityCondCompare condition = new CEntityCondCompare();
        switch (comparison)
        {
            case LESS -> condition.SetLessThan(number("7"), number("8"));
            case LESS_OR_EQUAL -> condition.SetLessOrEqualThan(number("7"), number("8"));
            case GREATER -> condition.SetGreaterThan(number("7"), number("8"));
            case GREATER_OR_EQUAL -> condition.SetGreaterOrEqualsThan(number("7"), number("8"));
        }
        return condition;
    }

    private CBaseEntityExpression number(String value)
    {
        return new LegacyTerminalFixture(new LegacyNumberFixture(catalog, value));
    }

    private enum Comparison
    {
        LESS,
        LESS_OR_EQUAL,
        GREATER,
        GREATER_OR_EQUAL
    }
}
