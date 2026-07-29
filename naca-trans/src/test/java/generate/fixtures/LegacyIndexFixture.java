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
        setLanguageExporter(output);
    }

    @Override
    public String ExportReference(int line)
    {
        String reference = "";
        if (of != null)
        {
            reference = of.ExportReference(getLine()) + ".";
        }
        return reference + FormatIdentifier(GetName());
    }

    @Override
    protected void DoExport()
    {
        WriteLine("Var " + FormatIdentifier(GetName()) + " = declare.index() ;");
    }
}
