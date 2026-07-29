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
        setLanguageExporter(output);
        catalog.RegisterExternalDataStructure(external);
    }

    @Override
    protected void DoExport()
    {
        if (externalData.IsNeedDeclarationInClass())
        {
            String type = externalData.GetTypeDecl();
            String line = type + " " + externalData.ExportReference(getLine())
                + " = " + type + ".Copy(this";
            if (externalData.GetReplaceItem() != 0)
            {
                line += ", replacing(" + externalData.GetReplaceItem()
                    + ", " + externalData.GetReplaceValue() + ")";
            }
            WriteLine(line + ") ;");
        }
        else
        {
            externalData.setLanguageExporter(GetXMLOutput());
            DoExport(externalData);
        }
        StartOutputBloc();
        ExportChildren();
        EndOutputBloc();
    }
}
