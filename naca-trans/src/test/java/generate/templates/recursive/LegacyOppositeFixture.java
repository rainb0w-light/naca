package generate.templates.recursive;

import semantic.expression.CEntityExprOpposite;

/**
 * Test-only snapshot of the retired direct backend for parity assertions.
 */
final class LegacyOppositeFixture extends CEntityExprOpposite
{
    @Override
    public String Export()
    {
        return "opposite(" + data.Export() + ")";
    }
}
