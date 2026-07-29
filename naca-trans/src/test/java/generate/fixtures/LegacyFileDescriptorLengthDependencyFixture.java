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
        generate.LegacyLanguageRenderer.bind(this, output);
    }
    protected void DoExport()
    {
        generate.LegacyLanguageRenderer.writeWord(this, "FileDescriptorDepending " + generate.LegacyLanguageRenderer.formatIdentifier(this, GetName())
            + " = declare.fileDescriptorDepending(");
        generate.LegacyLanguageRenderer.writeWord(this, fileDescriptor.getFormattedName() + ", ");
        generate.LegacyLanguageRenderer.writeWord(this, generate.LegacyDataRenderer.renderReference(lenghtDep, getLine()) + ") ;");
        generate.LegacyLanguageRenderer.writeEol(this);
    }
}
