package generate.fixtures;

import generate.CBaseLanguageExporter;
import semantic.CEntityDataSection;
import utils.CObjectCatalog;

/**
 * Test-only snapshot of the retired data-section backend.
 */
public final class LegacyDataSectionFixture extends CEntityDataSection
{
    public LegacyDataSectionFixture(
        int line, String name, CObjectCatalog catalog, CBaseLanguageExporter output)
    {
        super(line, name, catalog);
        generate.LegacyLanguageRenderer.bind(this, output);
    }
    protected void DoExport()
    {
        String type;
        boolean exportAllChildren = false;
        if (isWorkingStorageSection())
        {
            type = "workingStorageSection";
        }
        else if (isLinkageSection())
        {
            type = "linkageSection";
        }
        else if (isFileSection())
        {
            type = "fileSection";
            exportAllChildren = true;
        }
        else if (isVariableSection())
        {
            type = "variableSection";
        }
        else
        {
            generate.LegacyLanguageRenderer.exportChildren(this, false);
            return;
        }
        generate.LegacyLanguageRenderer.writeLine(this, "DataSection " + generate.LegacyLanguageRenderer.formatIdentifier(this, GetName())
            + " = declare." + type + "() ;");
        if (exportAllChildren)
        {
            generate.LegacyLanguageRenderer.exportChildren(this, true);
        }
        else
        {
            generate.LegacyLanguageRenderer.exportChildren(this, false);
        }
    }
}
