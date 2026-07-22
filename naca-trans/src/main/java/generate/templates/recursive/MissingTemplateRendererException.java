package generate.templates.recursive;

/** Raised in strict mode when a semantic node has no registered ST renderer. */
public final class MissingTemplateRendererException extends RuntimeException
{
    public MissingTemplateRendererException(Class<?> modelType)
    {
        super("No recursive ST renderer registered for " + modelType.getName());
    }
}
