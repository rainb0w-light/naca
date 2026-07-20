package generate.templates.recursive;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.java.CJavaAttribute;
import generate.java.CJavaExporter;
import generate.java.expressions.CJavaAddressOf;
import generate.java.expressions.CJavaConcat;
import generate.java.expressions.CJavaCurrentDate;
import generate.java.expressions.CJavaDigits;
import generate.java.expressions.CJavaEntityNumber;
import generate.java.expressions.CJavaExprSum;
import generate.java.expressions.CJavaExprTerminal;
import generate.java.expressions.CJavaInternalBool;
import generate.java.expressions.CJavaIntrinsicFunction;
import generate.java.expressions.CJavaLengthOf;
import generate.java.expressions.CJavaList;
import generate.java.expressions.CJavaString;
import generate.templates.TemplateLoader;
import java.util.List;
import org.junit.jupiter.api.Test;
import parser.expression.CSumExpression;
import semantic.CDataEntity;
import semantic.CEntityValueReference;
import semantic.expression.CBaseEntityExpression;
import utils.CObjectCatalog;

class JavaSemanticFunctionRendererTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
    private final CJavaExporter output = new CJavaExporter(null, "/tmp/unused.java", null, false);
    private final JavaTemplateAssembler assembler = TemplateLoader.newRecursiveAssembler();

    @Test
    void rendersUnaryDataFunctionsAndInternalBooleanLikeTheDirectGenerator()
    {
        CJavaAttribute field = attribute("SOURCE-FIELD");

        assertMatchesDirect(new CJavaLengthOf(catalog, output, field));
        assertMatchesDirect(new CJavaCurrentDate(catalog, output));
        assertMatchesDirect(new CJavaAddressOf(catalog, output, field));
        assertMatchesDirect(new CJavaDigits(catalog, output, field));
        assertMatchesDirect(new CJavaInternalBool("INTERNAL-SWITCH", catalog, output));

        CEntityValueReference valueReference = new CEntityValueReference(
            catalog, attribute("VALUE-WRAPPER"));
        assertMatchesDirect(valueReference);
    }

    @Test
    void recursivelyComposesConcatAndTypedLists()
    {
        CJavaConcat concat = new CJavaConcat(
            catalog,
            output,
            new CJavaString(catalog, output, "PREFIX".toCharArray()),
            attribute("SOURCE-FIELD"));
        assertMatchesDirect(concat);

        CJavaList empty = new CJavaList("EMPTY", catalog, output);
        assertMatchesDirect(empty);

        CJavaList strings = new CJavaList("STRINGS", catalog, output);
        strings.AddData(new CJavaString(catalog, output, "A".toCharArray()));
        strings.AddData(new CJavaString(catalog, output, "B".toCharArray()));
        assertMatchesDirect(strings);

        CJavaList numbers = new CJavaList("NUMBERS", catalog, output);
        numbers.AddData(number("1"));
        numbers.AddData(number("2"));
        assertMatchesDirect(numbers);

        CJavaList variables = new CJavaList("VARIABLES", catalog, output);
        variables.AddData(new CJavaInternalBool("FIRST-SWITCH", catalog, output));
        variables.AddData(new CJavaInternalBool("SECOND-SWITCH", catalog, output));
        assertMatchesDirect(variables);
    }

    @Test
    void recursivelyRendersGenericIntrinsicArgumentsAndRuntimeName()
    {
        CJavaExprSum nestedSum = new CJavaExprSum();
        nestedSum.SetSumExpression(
            expression(number("1")),
            expression(number("2")),
            CSumExpression.CSumType.ADD);
        CJavaIntrinsicFunction function = new CJavaIntrinsicFunction(
            catalog,
            output,
            "INTEGER-OF-DATE",
            List.of(expression(attribute("DATE-VALUE")), nestedSum));

        assertMatchesDirect(function);
    }

    private CJavaAttribute attribute(String name)
    {
        return new CJavaAttribute(1, name, catalog, output);
    }

    private CJavaEntityNumber number(String value)
    {
        return new CJavaEntityNumber(catalog, output, value);
    }

    private CBaseEntityExpression expression(CDataEntity value)
    {
        return new CJavaExprTerminal(value);
    }

    private void assertMatchesDirect(CDataEntity value)
    {
        assertEquals(value.ExportReference(1), assembler.renderRoot(value),
            value.getClass().getName());
    }
}
