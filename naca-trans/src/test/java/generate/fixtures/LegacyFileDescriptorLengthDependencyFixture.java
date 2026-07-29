package generate.fixtures;

import generate.CBaseLanguageExporter;
import semantic.CEntityFileDescriptorLengthDependency;
import utils.CObjectCatalog;

/** Test-only snapshot of the retired file-descriptor dependency backend. */
public final class LegacyFileDescriptorLengthDependencyFixture
    extends CEntityFileDescriptorLengthDependency
{
    public LegacyFileDescriptorLengthDependencyFixture(
        String name, CObjectCatalog catalog, CBaseLanguageExporter output)
    {
        super(name, catalog);
        setLanguageExporter(output);
    }

    @Override
    protected void DoExport()
    {
        WriteWord("FileDescriptorDepending " + FormatIdentifier(GetName())
            + " = declare.fileDescriptorDepending(");
        WriteWord(fileDescriptor.getFormattedName() + ", ");
        WriteWord(generate.LegacyDataRenderer.renderReference(lenghtDep, getLine()) + ") ;");
        WriteEOL();
    }
}
