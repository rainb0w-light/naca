package generate.templates.recursive;

import semantic.expression.CEntityExprSum;

/**
 * Test-only snapshot of the retired direct sum backend.
 */
final class LegacySumFixture extends CEntityExprSum
{
    public String Export()
    {
        String operation = isAdd() ? "add" : "subtract";
        return operation + "(" + generate.LegacyExpressionRenderer.render(op1)
            + ", \n" + generate.LegacyExpressionRenderer.render(op2) + ")";
    }
}
