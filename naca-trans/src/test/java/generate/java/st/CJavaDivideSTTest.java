package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.Verbs.CEntityDivide;

class CJavaDivideSTTest
{
    @Test
    void rendersQuotientAndRemainderFromTheSemanticModel()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CEntityDivide divide = new CEntityDivide(1, null);
        divide.setLanguageExporter(exporter);
        divide.SetDivide(
            new MockDataEntity(1, "dividend"),
            new MockDataEntity(1, "divisor"),
            new MockDataEntity(1, "quotient"),
            false);
        divide.SetRemainder(new MockDataEntity(1, "remainder"));

        String output = TemplateLoader.getRecursiveAssembler().renderRoot(divide, JavaTemplateRole.REFERENCE);

        assertTrue(
            output.contains("divide(dividend, divisor).to(quotient, remainder) ;"),
            output);
    }
}
