package generate.templates.recursive;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.java.CJavaAttribute;
import generate.java.CJavaExporter;
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
import semantic.expression.CEntityAddressOf;
import semantic.expression.CEntityConcat;
import semantic.expression.CEntityCurrentDate;
import semantic.expression.CEntityDigits;
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
        assertEquals("currentDate()",
            assembler.renderRoot(
                new CEntityCurrentDate(catalog),
                JavaTemplateRole.REFERENCE));
        assertEquals("addressOf(source_Field)",
            assembler.renderRoot(
                new CEntityAddressOf(catalog, field),
                JavaTemplateRole.REFERENCE));
        assertEquals("digits(source_Field)",
            assembler.renderRoot(
                new CEntityDigits(catalog, field),
                JavaTemplateRole.REFERENCE));
        assertMatchesDirect(new CJavaInternalBool("INTERNAL-SWITCH", catalog, output));

        CEntityValueReference valueReference = new CEntityValueReference(
            catalog, attribute("VALUE-WRAPPER"));
        assertMatchesDirect(valueReference);
    }

    @Test
    void recursivelyComposesConcatAndTypedLists()
    {
        CEntityConcat concat = new CEntityConcat(
            catalog,
            new CJavaString(catalog, output, "PREFIX".toCharArray()),
            attribute("SOURCE-FIELD"));
        assertEquals("concat(\"PREFIX\", source_Field)",
            assembler.renderRoot(concat, JavaTemplateRole.REFERENCE));

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
        LegacySumFixture nestedSum = new LegacySumFixture();
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

    private LegacyNumberFixture number(String value)
    {
        return new LegacyNumberFixture(catalog, value);
    }

    private CBaseEntityExpression expression(CDataEntity value)
    {
        return new LegacyTerminalFixture(value);
    }

    private void assertMatchesDirect(CDataEntity value)
    {
        assertEquals(value.ExportReference(1), assembler.renderRoot(value, JavaTemplateRole.REFERENCE),
            value.getClass().getName());
    }
}
