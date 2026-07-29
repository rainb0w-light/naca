package generate.templates.recursive;

import generate.CBaseLanguageExporter;
import semantic.expression.CEntityNumber;
import utils.CObjectCatalog;

/**
 * Test-only snapshot of the retired direct backend. Parity tests use it to
 * compute the legacy expected text without restoring string generation to the
 * production semantic entity.
 */
final class LegacyNumberFixture extends CEntityNumber
{
    LegacyNumberFixture(CObjectCatalog catalog, String number)
    {
        super(catalog, number);
    }

    LegacyNumberFixture(
        CObjectCatalog catalog,
        CBaseLanguageExporter output,
        String number)
    {
        super(catalog, number);
        generate.LegacyLanguageRenderer.bind(this, output);
    }

        public String ExportReference(int line)
    {
        if (isDecimalLiteral())
        {
            return "\"" + getLiteralValue() + "\"";
        }
        String normalized = getNormalizedLiteralValue();
        return normalized + (isLongLiteral() ? "L" : "");
    }
}
