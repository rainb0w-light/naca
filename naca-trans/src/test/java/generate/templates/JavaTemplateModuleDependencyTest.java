package generate.templates;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class JavaTemplateModuleDependencyTest
{
    private static final Pattern TEMPLATE_CALL = Pattern.compile(
        "<([A-Za-z_][A-Za-z0-9_]*)\\s*\\(");
    private static final int MAX_MODULE_LINES = 500;
    private static final String[] BINDING_RESOURCES = {
        "/templates/java/semantic-bindings.properties",
        "/templates/java/semantic-runtime-bindings.properties",
        "/templates/java/semantic-declaration-bindings.properties",
        "/templates/java/semantic-root-bindings.properties",
        "/templates/java/semantic-fpac-bindings.properties",
        "/templates/java/semantic-fpac-root-bindings.properties"
    };

    @Test
    void crossModuleCallsFollowDeclaredDependencies() throws IOException
    {
        JavaTemplateProfile profile = JavaTemplateProfile.full();
        JavaTemplateCatalog catalog = JavaTemplateCatalogFactory.create(profile);
        Map<String, JavaTemplateModule> modules = modulesById(profile);

        for (JavaTemplateModule module : profile.modules())
        {
            String source = read(module.resourcePath());
            Set<String> allowedOwners = dependencyClosure(module, modules);
            Matcher calls = TEMPLATE_CALL.matcher(source);
            while (calls.find())
            {
                String templateName = calls.group(1);
                String owner = catalog.moduleOf(templateName);
                assertTrue(owner == null || owner.equals(module.id())
                        || allowedOwners.contains(owner),
                    () -> module.id() + " calls " + templateName + " from " + owner
                        + " without declaring that module dependency");
            }
        }
    }

    @Test
    void everyModuleStaysWithinTheMaintainabilityLimit() throws IOException
    {
        for (JavaTemplateModule module : JavaTemplateProfile.full().modules())
        {
            long lines = read(module.resourcePath()).lines().count();
            assertTrue(lines <= MAX_MODULE_LINES,
                () -> module.id() + " has " + lines + " lines; split it below "
                    + MAX_MODULE_LINES);
        }
    }

    @Test
    void everyBindingResolvesInTheFullProfile() throws IOException
    {
        JavaTemplateCatalog catalog =
            JavaTemplateCatalogFactory.create(JavaTemplateProfile.full());
        for (String resource : BINDING_RESOURCES)
        {
            Properties bindings = new Properties();
            try (InputStream input = getClass().getResourceAsStream(resource))
            {
                if (input == null)
                {
                    throw new IllegalStateException("Missing binding resource: " + resource);
                }
                bindings.load(input);
            }
            for (String semanticType : bindings.stringPropertyNames())
            {
                String templateName = bindings.getProperty(semanticType).trim();
                assertTrue(catalog.hasTemplate(templateName),
                    () -> resource + " maps " + semanticType
                        + " to missing template " + templateName);
            }
        }
    }

    private static Map<String, JavaTemplateModule> modulesById(JavaTemplateProfile profile)
    {
        Map<String, JavaTemplateModule> modules = new HashMap<>();
        for (JavaTemplateModule module : profile.modules())
        {
            modules.put(module.id(), module);
        }
        return modules;
    }

    private static Set<String> dependencyClosure(
        JavaTemplateModule module, Map<String, JavaTemplateModule> modules)
    {
        Set<String> dependencies = new HashSet<>();
        collectDependencies(module, modules, dependencies);
        return dependencies;
    }

    private static void collectDependencies(
        JavaTemplateModule module,
        Map<String, JavaTemplateModule> modules,
        Set<String> dependencies)
    {
        for (String dependencyId : module.dependencies())
        {
            if (dependencies.add(dependencyId))
            {
                collectDependencies(modules.get(dependencyId), modules, dependencies);
            }
        }
    }

    private static String read(String resourcePath) throws IOException
    {
        try (InputStream input = JavaTemplateModuleDependencyTest.class
            .getResourceAsStream(resourcePath))
        {
            if (input == null)
            {
                throw new IllegalStateException("Missing template resource: " + resourcePath);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
