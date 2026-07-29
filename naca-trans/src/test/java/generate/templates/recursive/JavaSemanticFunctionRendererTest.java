package generate.templates.recursive;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.fixtures.LegacyAttributeFixture;
import generate.java.CJavaExporter;
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
        LegacyAttributeFixture field = attribute("SOURCE-FIELD");

        assertMatchesDirect(new LegacyLengthOfFixture(catalog, output, field));
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
        assertMatchesDirect(new LegacyInternalBoolFixture("INTERNAL-SWITCH", catalog, output));

        CEntityValueReference valueReference = new CEntityValueReference(
            catalog, attribute("VALUE-WRAPPER"));
        assertMatchesDirect(valueReference);
    }

    @Test
    void recursivelyComposesConcatAndTypedLists()
    {
        CEntityConcat concat = new CEntityConcat(
            catalog,
            new LegacyStringFixture(catalog, output, "PREFIX".toCharArray()),
            attribute("SOURCE-FIELD"));
        assertEquals("concat(\"PREFIX\", source_Field)",
            assembler.renderRoot(concat, JavaTemplateRole.REFERENCE));

        LegacyListFixture empty = new LegacyListFixture("EMPTY", catalog, output);
        assertMatchesDirect(empty);

        LegacyListFixture strings = new LegacyListFixture("STRINGS", catalog, output);
        strings.AddData(new LegacyStringFixture(catalog, output, "A".toCharArray()));
        strings.AddData(new LegacyStringFixture(catalog, output, "B".toCharArray()));
        assertMatchesDirect(strings);

        LegacyListFixture numbers = new LegacyListFixture("NUMBERS", catalog, output);
        numbers.AddData(number("1"));
        numbers.AddData(number("2"));
        assertMatchesDirect(numbers);

        LegacyListFixture variables = new LegacyListFixture("VARIABLES", catalog, output);
        variables.AddData(new LegacyInternalBoolFixture("FIRST-SWITCH", catalog, output));
        variables.AddData(new LegacyInternalBoolFixture("SECOND-SWITCH", catalog, output));
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
        LegacyIntrinsicFunctionFixture function =
            new LegacyIntrinsicFunctionFixture(
                catalog,
                output,
                "INTEGER-OF-DATE",
                List.of(expression(attribute("DATE-VALUE")), nestedSum));

        assertMatchesDirect(function);
    }

    private LegacyAttributeFixture attribute(String name)
    {
        return new LegacyAttributeFixture(1, name, catalog, output);
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
