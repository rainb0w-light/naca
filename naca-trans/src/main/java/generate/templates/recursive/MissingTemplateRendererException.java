package generate.templates.recursive;

/** Raised in strict mode when a semantic node has no registered ST renderer. */
public final class MissingTemplateRendererException extends RuntimeException
{
    public MissingTemplateRendererException(Class<?> modelType)
    {
        super("missing ST binding for " + semanticName(modelType)
            + " (concrete " + modelType.getName() + ")");
    }

    /**
     * The most specific {@code semantic.*} supertype of the rendered node — the
     * type a backend manifest binding is keyed on — so the error points at the
     * semantic entity that needs a binding, not just the concrete backend class.
     */
    private static String semanticName(Class<?> modelType)
    {
        for (Class<?> c = modelType; c != null; c = c.getSuperclass())
        {
            if (c.getName().startsWith("semantic."))
            {
                return c.getName();
            }
        }
        return modelType.getName();
    }
}
