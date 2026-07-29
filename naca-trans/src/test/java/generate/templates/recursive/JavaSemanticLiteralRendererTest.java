package generate.templates.recursive;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.templates.TemplateLoader;
import java.util.List;
import org.junit.jupiter.api.Test;

class JavaSemanticLiteralRendererTest
{
    @Test
    void rendersActualSemanticNumberNodes()
    {
        JavaTemplateAssembler assembler = TemplateLoader.newRecursiveAssembler();

        assertEquals("42", assembler.renderRoot(number("00042"), JavaTemplateRole.REFERENCE));
        assertEquals("2147483648L", assembler.renderRoot(number("2147483648"), JavaTemplateRole.REFERENCE));
        assertEquals("\"12.50\"", assembler.renderRoot(number("12.50"), JavaTemplateRole.REFERENCE));
    }

    @Test
    void rendersAndEscapesAnActualSemanticStringNode()
    {
        JavaTemplateAssembler assembler = TemplateLoader.newRecursiveAssembler();
        LegacyStringFixture value = new LegacyStringFixture(
            null,
            null,
            "quote=\" slash=\\ line=\n".toCharArray());

        assertEquals(
            "\"quote=\\\" slash=\\\\ line=\\n\"",
            assembler.renderRoot(value, JavaTemplateRole.REFERENCE));
    }

    @Test
    void matchesTheDirectGeneratorForTheMigratedLiteralSurface()
    {
        JavaTemplateAssembler assembler = TemplateLoader.newRecursiveAssembler();
        for (String raw : List.of(
            "0", "00042", "-00042", "2147483648", "12.50",
            "999999999999999999999999"))
        {
            LegacyNumberFixture value = number(raw);
            assertEquals(generate.LegacyDataRenderer.renderReference(value, 1), assembler.renderRoot(value, JavaTemplateRole.REFERENCE), raw);
        }

        for (char[] raw : List.of(
            "plain".toCharArray(),
            "quote=\" slash=\\ line=\n".toCharArray(),
            new char[] {(char) 0x00e9},
            new char[] {(char) 0x4f60}))
        {
            LegacyStringFixture value = new LegacyStringFixture(null, null, raw);
            assertEquals(generate.LegacyDataRenderer.renderReference(value, 1), assembler.renderRoot(value, JavaTemplateRole.REFERENCE));
        }
    }

    private static LegacyNumberFixture number(String value)
    {
        return new LegacyNumberFixture(null, value);
    }
}
