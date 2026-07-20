package generate.templates.recursive;

import org.stringtemplate.v4.STErrorListener;
import org.stringtemplate.v4.misc.STMessage;

/**
 * Fail-closed error listener for the recursive assembler. The architecture
 * contract requires a missing declarative binding to fail immediately rather
 * than fall back or silently emit nothing. ST4's default listener swallows
 * in-template exceptions (e.g. {@link MissingTemplateRendererException}) and
 * renders an empty string, which would reproduce the exact "silent empty
 * body" defect the migration removes. This listener re-throws so an
 * incompletely migrated program fails the transpilation loudly.
 */
public final class RecursiveTemplateErrorListener implements STErrorListener
{
    @Override
    public void compileTimeError(STMessage msg)
    {
        throw fail(msg);
    }

    @Override
    public void runTimeError(STMessage msg)
    {
        throw fail(msg);
    }

    @Override
    public void IOError(STMessage msg)
    {
        throw fail(msg);
    }

    @Override
    public void internalError(STMessage msg)
    {
        throw fail(msg);
    }

    private static RuntimeException fail(STMessage msg)
    {
        Throwable cause = msg.cause;
        if (cause instanceof RuntimeException runtime)
        {
            return runtime;
        }
        return new IllegalStateException("Recursive ST generation error: " + msg, cause);
    }
}
