package generate.templates.recursive;

import semantic.expression.CEntityExprProd;

/**
 * Test-only snapshot of the retired direct product backend.
 */
final class LegacyProductFixture extends CEntityExprProd
{
    @Override
    public String Export()
    {
        String operation = isMultiply() ? "multiply" : isDivide() ? "divide" : "pow";
        return operation + "(" + op1.Export() + ", \n" + op2.Export() + ")";
    }
}
