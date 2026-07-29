package generate.templates.recursive;

import semantic.expression.CEntityExprProd;

/**
 * Test-only snapshot of the retired direct product backend.
 */
final class LegacyProductFixture extends CEntityExprProd
{
    public String Export()
    {
        String operation = isMultiply() ? "multiply" : isDivide() ? "divide" : "pow";
        return operation + "(" + generate.LegacyExpressionRenderer.render(op1)
            + ", \n" + generate.LegacyExpressionRenderer.render(op2) + ")";
    }
}
