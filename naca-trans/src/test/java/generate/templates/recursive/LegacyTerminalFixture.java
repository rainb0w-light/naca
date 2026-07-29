package generate.templates.recursive;

import semantic.CDataEntity;
import semantic.expression.CEntityExprTerminal;

/**
 * Test-only snapshot of the retired direct terminal backend.
 */
final class LegacyTerminalFixture extends CEntityExprTerminal
{
    LegacyTerminalFixture(CDataEntity term)
    {
        super(term);
    }

    public String Export()
    {
        return term == null ? "[UNDEFINED]" : generate.LegacyDataRenderer.renderReference(term, getLine());
    }
}
