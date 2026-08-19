package generate.fixtures;

import generate.CBaseLanguageExporter;
import semantic.CEntityIndex;
import utils.CObjectCatalog;

/** Test-only snapshot of the retired index backend. */
public final class LegacyIndexFixture extends CEntityIndex
{
    public LegacyIndexFixture(
        String name, CObjectCatalog catalog, CBaseLanguageExporter output)
    {
        super(name, catalog);
        generate.LegacyLanguageRenderer.bind(this, output);
    }

        public String ExportReference(int line)
    {
        String reference = "";
        if (of != null)
        {
            reference = generate.LegacyDataRenderer.renderReference(of, getLine()) + ".";
        }
        return reference + generate.LegacyLanguageRenderer.formatIdentifier(this, GetName());
    }
    protected void DoExport()
    {
        generate.LegacyLanguageRenderer.writeLine(
            this,
            "Var " + generate.LegacyLanguageRenderer.formatIdentifier(this, GetName()) + " = declare.index() ;");
    }
}
