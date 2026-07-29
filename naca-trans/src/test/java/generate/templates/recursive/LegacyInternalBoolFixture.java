package generate.templates.recursive;

import generate.CBaseLanguageExporter;
import semantic.expression.CEntityInternalBool;
import utils.CObjectCatalog;

/**
 * Test-only snapshot of the retired direct internal-boolean backend.
 */
final class LegacyInternalBoolFixture extends CEntityInternalBool
{
    LegacyInternalBoolFixture(
        String name,
        CObjectCatalog catalog,
        CBaseLanguageExporter output)
    {
        super(name, catalog);
        setLanguageExporter(output);
    }

        public String ExportReference(int line)
    {
        return FormatIdentifier(GetName());
    }
}
