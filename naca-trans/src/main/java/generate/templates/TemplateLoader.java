package generate.templates;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.io.InputStream;
import java.net.URL;

/**
 * StringTemplate4 Template Loader
 * 
 * Provides centralized access to ST4 templates for code generation.
 * Templates are organized in a hierarchy:
 * 
 * templates/
 * ├── base.stg           - Base templates (shared utilities)
 * └── java/
 *     └── java.stg       - Java code generation templates
 * 
 * Design Principle (PUSH Model):
 * - Controllers push entity objects to templates
 * - Templates handle all formatting, conditionals, and nested rendering
 * - No logic in Controller, only: template.add("entity", this)
 * 
 * @see generate.templates
 */
public class TemplateLoader {

    private static STGroup javaGroup;
    private static boolean initialized = false;

    // Template group names
    public static final String GROUP_JAVA = "java";
    public static final String GROUP_BASE = "base";

    /**
     * Initialize template groups lazily
     */
    private static void initialize() {
        if (initialized) {
            return;
        }

        try {
            // Load java.stg which imports base.stg
            URL templateResource = TemplateLoader.class.getResource("/templates/java/java.stg");
            if (templateResource == null) {
                throw new RuntimeException("Cannot find template file: /templates/java/java.stg");
            }
            
            javaGroup = new STGroupFile(templateResource, "UTF-8", '<', '>');
            javaGroup.load();  // Pre-load templates
            
            initialized = true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize ST4 templates", e);
        }
    }

    /**
     * Get a template instance from the Java template group.
     * 
     * Usage:
     * <pre>
     * ST template = TemplateLoader.getTemplate("condition");
     * template.add("entity", this);
     * String output = template.render();
     * </pre>
     * 
     * @param name Template name (e.g., "condition", "assign", "loop")
     * @return ST template instance ready for attribute population
     */
    public static ST getTemplate(String name) {
        initialize();
        ST template = javaGroup.getInstanceOf(name);
        if (template == null) {
            throw new RuntimeException("Template not found: " + name);
        }
        return template;
    }

    /**
     * Get a template for verbs (COBOL verbs → Java methods).
     * Currently same as getTemplate since all templates are in java.stg.
     * 
     * @param name Verb template name (e.g., "assign", "addTo", "readFile")
     * @return ST template instance
     */
    public static ST getVerbsTemplate(String name) {
        return getTemplate(name);
    }

    /**
     * Get a template for expressions.
     * 
     * @param name Expression template name (e.g., "exprSum", "condOr")
     * @return ST template instance
     */
    public static ST getExpressionTemplate(String name) {
        return getTemplate(name);
    }

    /**
     * Get a template for control flow statements.
     * 
     * @param name Control template name (e.g., "condition", "loop", "case")
     * @return ST template instance
     */
    public static ST getControlTemplate(String name) {
        return getTemplate(name);
    }

    /**
     * Check if a template exists.
     * 
     * @param name Template name
     * @return true if template exists
     */
    public static boolean hasTemplate(String name) {
        initialize();
        return javaGroup.isDefined(name);
    }

    /**
     * Get the underlying STGroup for advanced operations.
     * 
     * @return STGroup for Java templates
     */
    public static STGroup getJavaGroup() {
        initialize();
        return javaGroup;
    }
}