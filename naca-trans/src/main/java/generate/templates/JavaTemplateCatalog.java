package generate.templates;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/** Immutable, fail-closed view of one composed Java ST4 template profile. */
public final class JavaTemplateCatalog
{
    private final JavaTemplateProfile profile;
    private final STGroup group;
    private final Map<String, String> templateOwners;
    private final List<String> resourcePaths;

    JavaTemplateCatalog(
        JavaTemplateProfile profile,
        STGroup group,
        Map<String, String> templateOwners,
        List<String> resourcePaths)
    {
        this.profile = Objects.requireNonNull(profile, "profile");
        this.group = Objects.requireNonNull(group, "group");
        this.templateOwners = Map.copyOf(templateOwners);
        this.resourcePaths = List.copyOf(resourcePaths);
    }

    public JavaTemplateProfile profile()
    {
        return profile;
    }

    public ST requireTemplate(String templateName)
    {
        ST template = group.getInstanceOf(templateName);
        if (template == null)
        {
            throw new IllegalArgumentException(
                "ST template not found in profile " + profile.id() + ": " + templateName);
        }
        return template;
    }

    public boolean hasTemplate(String templateName)
    {
        return group.isDefined(templateName);
    }

    public Set<String> templateNames()
    {
        return templateOwners.keySet();
    }

    public String moduleOf(String templateName)
    {
        return templateOwners.get(normalizeName(templateName));
    }

    public List<String> resourcePaths()
    {
        return resourcePaths;
    }

    public STGroup group()
    {
        return group;
    }

    static String normalizeName(String name)
    {
        Objects.requireNonNull(name, "name");
        return name.startsWith("/") ? name.substring(1) : name;
    }
}
