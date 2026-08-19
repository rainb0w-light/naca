package generate.templates;

import generate.templates.recursive.java.JavaIdentifierAttributeRenderer;
import generate.templates.recursive.java.JavaIdentifierValue;
import generate.templates.recursive.java.JavaIntrinsicFunctionNameAttributeRenderer;
import generate.templates.recursive.java.JavaIntrinsicFunctionNameValue;
import generate.templates.recursive.java.JavaStringLiteralAttributeRenderer;
import generate.templates.recursive.java.JavaStringLiteralValue;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

/** Builds validated, dependency-aware ST4 catalogs from explicit profiles. */
public final class JavaTemplateCatalogFactory
{
    private JavaTemplateCatalogFactory()
    {
    }

    /** Executes the create operation. */
    public static JavaTemplateCatalog create(JavaTemplateProfile profile)
    {
        Map<String, JavaTemplateModule> modulesById = indexModules(profile);
        Map<String, STGroup> loadedGroups = new LinkedHashMap<>();
        Set<String> visiting = new LinkedHashSet<>();
        Map<String, String> templateOwners = new LinkedHashMap<>();
        List<String> resourcePaths = new ArrayList<>();

        for (JavaTemplateModule module : profile.modules())
        {
            loadModule(module, modulesById, loadedGroups, visiting,
                templateOwners, resourcePaths);
        }

        STGroup catalogGroup = new STGroup('<', '>');
        for (STGroup moduleGroup : loadedGroups.values())
        {
            catalogGroup.importTemplates(moduleGroup);
        }
        registerRenderers(catalogGroup);
        return new JavaTemplateCatalog(
            profile, catalogGroup, templateOwners, resourcePaths);
    }

    private static Map<String, JavaTemplateModule> indexModules(JavaTemplateProfile profile)
    {
        Map<String, JavaTemplateModule> modules = new LinkedHashMap<>();
        for (JavaTemplateModule module : profile.modules())
        {
            if (modules.putIfAbsent(module.id(), module) != null)
            {
                throw new IllegalStateException(
                    "Duplicate template module id in profile " + profile.id() + ": "
                        + module.id());
            }
        }
        return modules;
    }

    private static STGroup loadModule(
        JavaTemplateModule module,
        Map<String, JavaTemplateModule> modulesById,
        Map<String, STGroup> loadedGroups,
        Set<String> visiting,
        Map<String, String> templateOwners,
        List<String> resourcePaths)
    {
        STGroup loaded = loadedGroups.get(module.id());
        if (loaded != null)
        {
            return loaded;
        }
        if (!visiting.add(module.id()))
        {
            throw new IllegalStateException(
                "Cyclic template module dependency: " + String.join(" -> ", visiting)
                    + " -> " + module.id());
        }

        URL resource = JavaTemplateCatalogFactory.class.getResource(module.resourcePath());
        if (resource == null)
        {
            throw new IllegalStateException(
                "Missing ST4 module resource " + module.id() + ": " + module.resourcePath());
        }
        STGroupFile group = new STGroupFile(resource, "UTF-8", '<', '>');
        for (String dependencyId : module.dependencies())
        {
            JavaTemplateModule dependency = modulesById.get(dependencyId);
            if (dependency == null)
            {
                throw new IllegalStateException(
                    "Unknown dependency " + dependencyId + " of template module "
                        + module.id());
            }
            group.importTemplates(loadModule(dependency, modulesById, loadedGroups,
                visiting, templateOwners, resourcePaths));
        }
        group.load();
        registerTemplateOwners(module, group, templateOwners);
        registerRenderers(group);
        visiting.remove(module.id());
        loadedGroups.put(module.id(), group);
        resourcePaths.add(module.resourcePath());
        return group;
    }

    private static void registerTemplateOwners(
        JavaTemplateModule module,
        STGroup group,
        Map<String, String> templateOwners)
    {
        for (String rawName : group.getTemplateNames())
        {
            String templateName = JavaTemplateCatalog.normalizeName(rawName);
            String previousOwner = templateOwners.putIfAbsent(templateName, module.id());
            if (previousOwner != null)
            {
                throw new IllegalStateException(
                    "Duplicate ST template " + templateName + " in modules "
                        + previousOwner + " and " + module.id());
            }
        }
    }

    private static void registerRenderers(STGroup group)
    {
        group.registerRenderer(
            JavaStringLiteralValue.class,
            new JavaStringLiteralAttributeRenderer());
        group.registerRenderer(
            String.class,
            new JavaStringLiteralAttributeRenderer());
        group.registerRenderer(
            JavaIdentifierValue.class,
            new JavaIdentifierAttributeRenderer());
        group.registerRenderer(
            JavaIntrinsicFunctionNameValue.class,
            new JavaIntrinsicFunctionNameAttributeRenderer());
    }
}
