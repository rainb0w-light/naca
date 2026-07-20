package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.java.expressions.CJavaExprTerminal;
import generate.templates.TemplateLoader;
import java.util.List;
import org.junit.jupiter.api.Test;
import semantic.Verbs.CEntityCalcul;
import semantic.expression.CBaseEntityExpression;

class CJavaComputSTTest
{
    @Test
    void rendersComputeUsingTheGenericDestinationModel()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CBaseEntityExpression argument = new CJavaExprTerminal(new MockDataEntity(1, "source"));
        CJavaIntrinsicFunctionST function = new CJavaIntrinsicFunctionST(
            null, exporter, "ORD", List.of(argument));

        CEntityCalcul compute = new CEntityCalcul(1, null);
        compute.setLanguageExporter(exporter);
        compute.SetCalcul(new CJavaExprTerminal(function));
        compute.AddDestination(new MockDataEntity(1, "result"));
        String output = TemplateLoader.getRecursiveAssembler().renderRoot(compute);

        assertTrue(
            output.contains("compute(CobolIntrinsicFunctions.ord(source), result) ;"),
            output);
    }
}
