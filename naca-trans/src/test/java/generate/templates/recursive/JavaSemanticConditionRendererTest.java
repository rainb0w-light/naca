package generate.templates.recursive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.java.CJavaExporter;
import generate.java.CJavaNamedCondition;
import generate.java.expressions.CJavaCondCompare;
import generate.java.expressions.CJavaCondEquals;
import generate.java.expressions.CJavaCondNot;
import generate.java.expressions.CJavaCondIsConstant;
import generate.java.expressions.CJavaEntityNumber;
import generate.java.expressions.CJavaExprTerminal;
import generate.java.expressions.CJavaInternalBool;
import generate.fpacjava.CFPacJavaCondIsBoolean;
import generate.templates.TemplateLoader;
import org.junit.jupiter.api.Test;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityCondAnd;
import semantic.expression.CEntityCondOr;
import utils.CObjectCatalog;

class JavaSemanticConditionRendererTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
    private final CJavaExporter output = new CJavaExporter(null, "/tmp/unused.java", null, false);
    private final JavaTemplateAssembler assembler = TemplateLoader.newRecursiveAssembler();

    @Test
    void rendersEqualityAndEveryOrderedComparisonLikeTheDirectGenerator()
    {
        CJavaCondEquals equal = new CJavaCondEquals();
        equal.SetEqualCondition(number("1"), number("2"));
        assertMatchesDirect(equal);

        CJavaCondEquals different = new CJavaCondEquals();
        different.SetDifferentCondition(number("3"), number("4"));
        assertMatchesDirect(different);

        assertMatchesDirect(compare(Comparison.LESS));
        assertMatchesDirect(compare(Comparison.LESS_OR_EQUAL));
        assertMatchesDirect(compare(Comparison.GREATER));
        assertMatchesDirect(compare(Comparison.GREATER_OR_EQUAL));
    }

    @Test
    void recursivelyComposesLogicalConditionsWithDirectGeneratorPrecedence()
    {
        CEntityCondAnd and = new CEntityCondAnd();
        and.SetCondition(equals("1", "1"), compare(Comparison.GREATER));

        CEntityCondOr or = new CEntityCondOr();
        or.SetCondition(and, equals("2", "3"));

        CJavaCondNot not = new CJavaCondNot();
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
        CJavaNamedCondition named = new CJavaNamedCondition(
            1, "VALID-STATUS", catalog, output);
        named.AddValue(new CJavaEntityNumber(catalog, output, "1"));

        assertEquals(named.ExportReference(1), assembler.renderRoot(named, JavaTemplateRole.REFERENCE));

        CBaseEntityCondition condition = named.GetAssociatedCondition(
            new generate.CJavaEntityFactory(catalog, output));
        assertMatchesDirect(condition);
        assertMatchesDirect(condition.GetOppositeCondition());
    }

    @Test
    void rendersConstantAndBooleanPredicatesLikeTheirDirectGenerators()
    {
        CJavaInternalBool value = new CJavaInternalBool("BOOLEAN-VALUE", catalog, output);

        CJavaCondIsConstant zero = new CJavaCondIsConstant();
        zero.SetIsZero(value);
        assertMatchesDirect(zero);
        assertMatchesDirect(zero.GetOppositeCondition());

        CJavaCondIsConstant space = new CJavaCondIsConstant();
        space.SetIsSpace(value);
        assertMatchesDirect(space);

        CJavaCondIsConstant low = new CJavaCondIsConstant();
        low.SetIsLowValue(value);
        assertMatchesDirect(low);

        CJavaCondIsConstant high = new CJavaCondIsConstant();
        high.SetIsHighValue(value);
        assertMatchesDirect(high);

        CFPacJavaCondIsBoolean trueCondition = new CFPacJavaCondIsBoolean();
        trueCondition.setIsTrue(value);
        assertMatchesDirect(trueCondition);

        CFPacJavaCondIsBoolean falseCondition = new CFPacJavaCondIsBoolean();
        falseCondition.setIsFalse(value);
        assertMatchesDirect(falseCondition);
    }

    private CJavaCondEquals equals(String left, String right)
    {
        CJavaCondEquals condition = new CJavaCondEquals();
        condition.SetEqualCondition(number(left), number(right));
        return condition;
    }

    private CJavaCondCompare compare(Comparison comparison)
    {
        CJavaCondCompare condition = new CJavaCondCompare();
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
        return new CJavaExprTerminal(new CJavaEntityNumber(catalog, output, value));
    }

    private void assertMatchesDirect(CBaseEntityCondition condition)
    {
        assertEquals(condition.Export(), assembler.renderRoot(condition, JavaTemplateRole.REFERENCE),
            condition.getClass().getName());
    }

    private enum Comparison
    {
        LESS,
        LESS_OR_EQUAL,
        GREATER,
        GREATER_OR_EQUAL
    }
}
