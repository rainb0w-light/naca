package generate;


/**
 * String-capturing exporter for ST4 two-stage pipeline.
 * Intercepts DoExport() calls that normally write to a file,
 * and captures the output as a String for template property access.
 */
public class CStringExporter extends CBaseLanguageExporter
{
    private StringBuilder buffer = new StringBuilder();

    /** Creates a new cstring exporter instance. */
    public CStringExporter()
    {
        super(null, null);
    }

    public String getCapturedString()
    {
        return buffer.toString();
    }

    @Override
    public void WriteWord(String word, int n)
    {
        buffer.append(word);
    }

    @Override
    public void WriteEOL(int n)
    {
        buffer.append("\n");
    }

    @Override
    public void WriteLine(String line, int n)
    {
        buffer.append(indent).append(line).append("\n");
    }

    @Override
    public void DoWriteLine(String line)
    {
        buffer.append(indent).append(line).append("\n");
    }

    @Override
    public void WriteComment(String line, int n)
    {
        buffer.append(indent).append(line).append("\n");
    }

    @Override
    public void WriteLongString(String text, int n)
    {
        buffer.append(text);
    }

    @Override
    public void CloseBracket()
    {
        buffer.append(")");
    }

    @Override
    public void OpenBracket()
    {
        buffer.append("(");
    }

    @Override
    protected void doCloseOutput()
    {
        // no-op: we don't write to files
    }

    @Override
    public void closeOutput()
    {
        // Skip comment container processing (commentContainer is null in string mode)
        doCloseOutput();
    }

    @Override
    public String getOutputDir()
    {
        return "";
    }

    @Override
    public boolean isResources()
    {
        return false;
    }
}
