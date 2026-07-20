package generate.java.st;

import generate.CBaseLanguageExporter;
import utils.COriginalLisiting;
import parser.CGlobalCommentContainer;

import java.io.StringWriter;

/**
 * Mock exporter for testing ST4 templates.
 * Captures all output written via WriteLine() to a StringWriter
 * for validation in unit tests.
 */
public class MockJavaExporter extends CBaseLanguageExporter {
    private final StringWriter outputWriter = new StringWriter();

    public MockJavaExporter() {
        super(null, null);
    }

    public MockJavaExporter(COriginalLisiting cat, CGlobalCommentContainer commCont) {
        super(cat, commCont);
    }

    @Override
    protected void DoWriteLine(String line) {
        outputWriter.append(line).append("\n");
    }

    @Override
    protected void DoWriteLine(String line, int n) {
        outputWriter.append(line).append("\n");
    }

    @Override
    protected void doCloseOutput() {
        // No-op for mock
    }

    @Override
    public void CloseBracket() {
        // No-op for mock
    }

    @Override
    public void OpenBracket() {
        // No-op for mock
    }

    @Override
    public String getOutputDir() {
        return "/tmp";
    }

    @Override
    public boolean isResources() {
        return false;
    }

    /**
     * Get the captured output from all WriteLine() calls.
     * @return The complete output as a string
     */
    public String getCapturedOutput() {
        return outputWriter.toString();
    }

    /**
     * Clear the captured output (useful for multiple test cases).
     */
    public void clearOutput() {
        outputWriter.getBuffer().setLength(0);
    }
}
