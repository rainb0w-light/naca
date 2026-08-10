package generate.templates;

import java.util.List;
import java.util.Objects;

/** One explicitly configured ST4 resource and its module dependencies. */
public record JavaTemplateModule(String id, String resourcePath, List<String> dependencies)
{
    public JavaTemplateModule
    {
        id = requireText(id, "id");
        resourcePath = requireText(resourcePath, "resourcePath");
        if (!resourcePath.startsWith("/"))
        {
            throw new IllegalArgumentException(
                "Template resource path must be absolute: " + resourcePath);
        }
        dependencies = List.copyOf(Objects.requireNonNull(dependencies, "dependencies"));
    }

    public JavaTemplateModule(String id, String resourcePath)
    {
        this(id, resourcePath, List.of());
    }

    private static String requireText(String value, String name)
    {
        Objects.requireNonNull(value, name);
        if (value.isBlank())
        {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }
}
