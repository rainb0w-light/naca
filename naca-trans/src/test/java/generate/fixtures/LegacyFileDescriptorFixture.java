package generate.fixtures;

import generate.CBaseLanguageExporter;
import semantic.CEntityFileDescriptor;
import utils.CObjectCatalog;

/** Test-only snapshot of the retired file-descriptor backend. */
public final class LegacyFileDescriptorFixture extends CEntityFileDescriptor
{
    /** Creates a new legacy file descriptor fixture instance. */
    public LegacyFileDescriptorFixture(
        int line, String name, CObjectCatalog catalog, CBaseLanguageExporter output)
    {
        super(line, name, catalog);
        generate.LegacyLanguageRenderer.bind(this, output);
    }
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
        generate.LegacyLanguageRenderer.writeWord(this, "FileDescriptor "
            + generate.LegacyLanguageRenderer.formatIdentifier(this, GetDisplayName())
            + " = declare.file(" + file + ")");
        if (fileSelect != null && fileSelect.getFileStatus() != null)
        {
            generate.LegacyLanguageRenderer.writeWord(this, ".status("
                + generate.LegacyDataRenderer.renderReference(
                    fileSelect.getFileStatus(), getLine())
                + ")");
        }
        generate.LegacyLanguageRenderer.writeWord(this, " ;");
        generate.LegacyLanguageRenderer.writeEol(this);
        generate.LegacyLanguageRenderer.exportChildren(this, false);
    }
}
