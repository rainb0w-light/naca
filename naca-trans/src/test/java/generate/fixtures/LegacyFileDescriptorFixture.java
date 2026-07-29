package generate.fixtures;

import generate.CBaseLanguageExporter;
import semantic.CEntityFileDescriptor;
import utils.CObjectCatalog;

/** Test-only snapshot of the retired file-descriptor backend. */
public final class LegacyFileDescriptorFixture extends CEntityFileDescriptor
{
    public LegacyFileDescriptorFixture(
        int line, String name, CObjectCatalog catalog, CBaseLanguageExporter output)
    {
        super(line, name, catalog);
        setLanguageExporter(output);
    }

    @Override
    protected void DoExport()
    {
        String file = null;
        if (fileSelect != null && fileSelect.GetFileName() != null)
        {
            file = generate.LegacyDataRenderer.renderReference(
                fileSelect.GetFileName(), getLine());
        }
        if (file == null || file.trim().isEmpty() || file.equals("null"))
        {
            file = "\"" + GetDisplayName() + "\"";
        }
        WriteWord("FileDescriptor " + FormatIdentifier(GetDisplayName())
            + " = declare.file(" + file + ")");
        if (fileSelect != null && fileSelect.getFileStatus() != null)
        {
            WriteWord(".status("
                + generate.LegacyDataRenderer.renderReference(
                    fileSelect.getFileStatus(), getLine())
                + ")");
        }
        WriteWord(" ;");
        WriteEOL();
        ExportChildren();
    }
}
