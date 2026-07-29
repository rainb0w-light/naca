package generate.templates.recursive;

import generate.CBaseLanguageExporter;
import semantic.CDataEntity;
import semantic.expression.CEntityLengthOf;
import utils.CObjectCatalog;

/**
 * Test-only snapshot of the retired direct LENGTH OF backend.
 */
final class LegacyLengthOfFixture extends CEntityLengthOf
{
    LegacyLengthOfFixture(
        CObjectCatalog catalog,
        CBaseLanguageExporter output,
        CDataEntity data)
    {
        super(catalog, data);
        generate.LegacyLanguageRenderer.bind(this, output);
    }

        public String ExportReference(int line)
    {
        return "lengthOf(" + generate.LegacyDataRenderer.renderReference(reference, getLine()) + ")";
    }
}
