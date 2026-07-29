package generate.fixtures;

import generate.CBaseLanguageExporter;
import semantic.CDataEntity;
import semantic.CEntityNamedCondition;
import utils.CObjectCatalog;

/** Test-only snapshot of the retired level-88 condition backend. */
public final class LegacyNamedConditionFixture extends CEntityNamedCondition
{
    public LegacyNamedConditionFixture(
        int line, String name, CObjectCatalog catalog, CBaseLanguageExporter output)
    {
        super(line, name, catalog);
        setLanguageExporter(output);
    }

    @Override
    public String ExportReference(int line)
    {
        String prefix = of == null ? "" : of.ExportReference(getLine()) + ".";
        return prefix + FormatIdentifier(GetName());
    }

    @Override
    protected void DoExport()
    {
        WriteWord("Cond " + FormatIdentifier(GetName()) + " = declare.condition()");
        for (CDataEntity value : values)
        {
            WriteWord(value == null
                ? ".value([undefined])"
                : ".value(" + value.ExportReference(getLine()) + ")");
        }
        for (int i = 0; i < startIntervals.size() && i < endIntervals.size(); i++)
        {
            WriteWord(".value(" + startIntervals.get(i).ExportReference(getLine())
                + ", " + endIntervals.get(i).ExportReference(getLine()) + ")");
        }
        WriteWord(".var() ;");
        WriteEOL();
    }
}
