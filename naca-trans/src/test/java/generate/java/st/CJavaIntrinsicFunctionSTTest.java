package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.java.expressions.CJavaExprTerminal;
import java.util.List;
import org.junit.jupiter.api.Test;
import semantic.expression.CBaseEntityExpression;

class CJavaIntrinsicFunctionSTTest
{
    @Test
    void rendersGenericIntrinsicFunctionFromSemanticArguments()
    {
        MockDataEntity source = new MockDataEntity(1, "subString(source, index, 1)");
        CBaseEntityExpression argument = new CJavaExprTerminal(source);
        CJavaIntrinsicFunctionST function = new CJavaIntrinsicFunctionST(
            null, new MockJavaExporter(), "ORD", List.of(argument));

        assertEquals(
            "CobolIntrinsicFunctions.ord(subString(source, index, 1))",
            function.ExportReference(1));
    }
}
