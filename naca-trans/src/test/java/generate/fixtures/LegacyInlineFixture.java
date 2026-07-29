package generate.fixtures;

import generate.CBaseLanguageExporter;
import semantic.CBaseExternalEntity;
import semantic.CEntityInline;
import utils.CObjectCatalog;

/** Test-only snapshot of the retired inline-copybook backend. */
public final class LegacyInlineFixture extends CEntityInline
{
    public LegacyInlineFixture(int line, CObjectCatalog catalog,
        CBaseLanguageExporter output, CBaseExternalEntity external)
    {
        super(line, catalog, external);
        generate.LegacyLanguageRenderer.bind(this, output);
        catalog.RegisterExternalDataStructure(external);
    }
    protected void DoExport()
    {
        if (externalData.IsNeedDeclarationInClass())
        {
            String type = externalData.GetTypeDecl();
            String line = type + " " + generate.LegacyDataRenderer.renderReference(externalData, getLine())
                + " = " + type + ".Copy(this";
            if (externalData.GetReplaceItem() != 0)
            {
                line += ", replacing(" + externalData.GetReplaceItem()
                    + ", " + externalData.GetReplaceValue() + ")";
            }
            generate.LegacyLanguageRenderer.writeLine(this, line + ") ;");
        }
        else
        {
            generate.LegacyLanguageRenderer.bind(externalData, generate.LegacyLanguageRenderer.output(this));
            generate.LegacyLanguageRenderer.invokeExport(externalData);
        }
        generate.LegacyLanguageRenderer.startBlock(this);
        generate.LegacyLanguageRenderer.exportChildren(this, false);
        generate.LegacyLanguageRenderer.endBlock(this);
    }
}
