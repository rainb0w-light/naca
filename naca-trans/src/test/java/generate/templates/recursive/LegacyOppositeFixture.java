package generate.templates.recursive;

import semantic.expression.CEntityExprOpposite;

/**
 * Test-only snapshot of the retired direct backend for parity assertions.
 */
final class LegacyOppositeFixture extends CEntityExprOpposite
{
    public String Export()
    {
        return "opposite(" + generate.LegacyExpressionRenderer.render(data) + ")";
    }
}
