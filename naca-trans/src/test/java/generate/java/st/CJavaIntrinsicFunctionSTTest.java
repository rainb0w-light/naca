package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import semantic.expression.CEntityExprTerminal;
import java.util.List;
import org.junit.jupiter.api.Test;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityIntrinsicFunction;

class CJavaIntrinsicFunctionSTTest
{
    @Test
    void rendersGenericIntrinsicFunctionFromSemanticArguments()
    {
        MockDataEntity source = new MockDataEntity(1, "subString(source, index, 1)");
        CBaseEntityExpression argument = new CEntityExprTerminal(source);
        CEntityIntrinsicFunction function =
            new CEntityIntrinsicFunction(null, "ORD", List.of(argument));

        assertEquals(
            "CobolIntrinsicFunctions.ord(subString(source, index, 1))",
            generate.templates.TemplateLoader.getRecursiveAssembler()
                .renderRoot(
                    function,
                    generate.templates.recursive.JavaTemplateRole.REFERENCE));
    }
}
