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

        public String ExportReference(int line)
    {
        String prefix = of == null ? "" : generate.LegacyDataRenderer.renderReference(of, getLine()) + ".";
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
                : ".value(" + generate.LegacyDataRenderer.renderReference(value, getLine()) + ")");
        }
        for (int i = 0; i < startIntervals.size() && i < endIntervals.size(); i++)
        {
            WriteWord(".value("
                + generate.LegacyDataRenderer.renderReference(
                    startIntervals.get(i), getLine())
                + ", "
                + generate.LegacyDataRenderer.renderReference(
                    endIntervals.get(i), getLine())
                + ")");
        }
        WriteWord(".var() ;");
        WriteEOL();
    }
}
