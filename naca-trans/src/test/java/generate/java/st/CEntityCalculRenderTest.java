package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import semantic.expression.CEntityExprTerminal;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import java.util.List;
import org.junit.jupiter.api.Test;
import semantic.Verbs.CEntityCalcul;
import semantic.expression.CBaseEntityExpression;

class CEntityCalculRenderTest
{
    private static String render(CEntityCalcul compute)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(compute, JavaTemplateRole.REFERENCE).trim();
    }

    private static CBaseEntityExpression expression()
    {
        CBaseEntityExpression argument = new CEntityExprTerminal(
            new MockDataEntity(1, "source"));
        semantic.expression.CEntityIntrinsicFunction function =
            new semantic.expression.CEntityIntrinsicFunction(
                null, "ORD", List.of(argument));
        return new CEntityExprTerminal(function);
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertInstanceOf(CEntityCalcul.class,
            new CJavaEntityFactory(null, null).NewEntityCalcul(1));
        assertInstanceOf(CEntityCalcul.class,
            new CJavaEntityFactory(null, null).NewEntityCalcul(1));
    }

    @Test
    void rendersNormalAndRoundedDestinations()
    {
        CEntityCalcul compute = new CEntityCalcul(1, null);
        compute.SetCalcul(expression());
        compute.AddDestination(new MockDataEntity(1, "normalResult"));
        compute.AddRoundedDestination(new MockDataEntity(1, "roundedResult"));

        assertEquals(
            "compute(CobolIntrinsicFunctions.ord(source), normalResult) ;\n"
                + "computeRounded(CobolIntrinsicFunctions.ord(source), roundedResult) ;",
            render(compute));
    }
}
