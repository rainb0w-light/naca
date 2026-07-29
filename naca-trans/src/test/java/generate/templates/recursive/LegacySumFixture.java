package generate.templates.recursive;

import semantic.expression.CEntityExprSum;

/**
 * Test-only snapshot of the retired direct sum backend.
 */
final class LegacySumFixture extends CEntityExprSum
{
    @Override
    public String Export()
    {
        String operation = isAdd() ? "add" : "subtract";
        return operation + "(" + op1.Export() + ", \n" + op2.Export() + ")";
    }
}
