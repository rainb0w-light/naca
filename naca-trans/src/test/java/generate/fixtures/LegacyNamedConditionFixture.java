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
        generate.LegacyLanguageRenderer.bind(this, output);
    }

        public String ExportReference(int line)
    {
        String prefix = of == null ? "" : generate.LegacyDataRenderer.renderReference(of, getLine()) + ".";
        return prefix + generate.LegacyLanguageRenderer.formatIdentifier(this, GetName());
    }
    protected void DoExport()
    {
        generate.LegacyLanguageRenderer.writeWord(this, "Cond " + generate.LegacyLanguageRenderer.formatIdentifier(this, GetName()) + " = declare.condition()");
        for (CDataEntity value : values)
        {
            generate.LegacyLanguageRenderer.writeWord(this, value == null
                ? ".value([undefined])"
                : ".value(" + generate.LegacyDataRenderer.renderReference(value, getLine()) + ")");
        }
        for (int i = 0; i < startIntervals.size() && i < endIntervals.size(); i++)
        {
            generate.LegacyLanguageRenderer.writeWord(this, ".value("
                + generate.LegacyDataRenderer.renderReference(
                    startIntervals.get(i), getLine())
                + ", "
                + generate.LegacyDataRenderer.renderReference(
                    endIntervals.get(i), getLine())
                + ")");
        }
        generate.LegacyLanguageRenderer.writeWord(this, ".var() ;");
        generate.LegacyLanguageRenderer.writeEol(this);
    }
}
