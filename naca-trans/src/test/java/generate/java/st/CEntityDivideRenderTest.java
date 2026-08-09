package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.Verbs.CEntityDivide;

class CEntityDivideRenderTest
{
    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertInstanceOf(CEntityDivide.class,
            new CJavaEntityFactory(null, null).NewEntityDivide(1));
        assertInstanceOf(CEntityDivide.class,
            new CJavaEntityFactory(null, null).NewEntityDivide(1));
    }

    @Test
    void rendersQuotientAndRemainderFromTheSemanticModel()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CEntityDivide divide = new CEntityDivide(1, null);
        generate.LegacyLanguageRenderer.bind(divide, exporter);
        divide.SetDivide(
            new MockDataEntity(1, "dividend"),
            new MockDataEntity(1, "divisor"),
            new MockDataEntity(1, "quotient"),
            false);
        divide.SetRemainder(new MockDataEntity(1, "remainder"));

        String output = TemplateLoader.getRecursiveAssembler().renderRoot(divide, JavaTemplateRole.REFERENCE);

        assertEquals(
            "divide(dividend, divisor).to(quotient, remainder) ;",
            output.trim());
    }

    @Test
    void rendersRoundedQuotient()
    {
        CEntityDivide divide = new CEntityDivide(1, null);
        divide.SetDivide(
            new MockDataEntity(1, "dividend"),
            new MockDataEntity(1, "divisor"),
            new MockDataEntity(1, "quotient"),
            true);

        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(divide, JavaTemplateRole.REFERENCE);

        assertEquals("divide(dividend, divisor).toRounded(quotient) ;", output.trim());
    }
}
