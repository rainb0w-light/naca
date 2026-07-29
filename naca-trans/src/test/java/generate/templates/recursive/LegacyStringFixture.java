package generate.templates.recursive;

import generate.CBaseLanguageExporter;
import semantic.expression.CEntityString;
import utils.CObjectCatalog;

/**
 * Test-only snapshot of the retired direct string backend.
 */
final class LegacyStringFixture extends CEntityString
{
    LegacyStringFixture(
        CObjectCatalog catalog,
        CBaseLanguageExporter output,
        char[] value)
    {
        super(catalog, value);
        generate.LegacyLanguageRenderer.bind(this, output);
    }

        public String ExportReference(int line)
    {
        return generate.templates.TemplateLoader.getRecursiveAssembler()
            .renderRoot(this, JavaTemplateRole.REFERENCE);
    }
}
