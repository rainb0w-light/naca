package generate.templates;

import generate.templates.recursive.JavaTemplateAssembler;
import org.stringtemplate.v4.ST;

/**
 * StringTemplate4 Template Loader
 *
 * Provides centralized access to ST4 templates for code generation.
 * The current full profile is exposed through a validated template catalog. Template
 * modules can be split without changing callers or the recursive rendering protocol.
 *
 * Design Principle (PUSH Model):
 * - Controllers push entity objects to templates
 * - Templates handle all formatting, conditionals, and nested rendering
 * - No logic in Controller, only: template.add("entity", this)
 *
 * @see generate.templates
 */
public class TemplateLoader {

    private static final JavaTemplateProfileController PROFILES =
        JavaTemplateProfileController.instance();

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
        return PROFILES.catalog(JavaTemplatePipeline.FULL).requireTemplate(name);
    }

    /**
     * Check if a template exists.
     * 
     * @param name Template name
     * @return true if template exists
     */
    public static boolean hasTemplate(String name) {
        return PROFILES.catalog(JavaTemplatePipeline.FULL).hasTemplate(name);
    }

    /** Creates a strict recursive assembler with all migrated Java renderers. */
    public static JavaTemplateAssembler newRecursiveAssembler() {
        return newRecursiveAssembler(JavaTemplatePipeline.FULL);
    }

    /** Creates a strict recursive assembler for one explicitly selected pipeline. */
    public static JavaTemplateAssembler newRecursiveAssembler(JavaTemplatePipeline pipeline) {
        return PROFILES.newAssembler(pipeline);
    }

    /**
     * Shared recursive assembler used by the production export driver. The
     * underlying STGroup is reusable and thread-safe for {@code getInstanceOf};
     * each {@code renderNode} builds fresh ST instances, so a single assembler
     * can serve concurrent transpilations without re-parsing the module catalog.
     */
    public static JavaTemplateAssembler getRecursiveAssembler() {
        return getRecursiveAssembler(JavaTemplatePipeline.FULL);
    }

    /** Returns the cached assembler for one pipeline-level template profile. */
    public static JavaTemplateAssembler getRecursiveAssembler(JavaTemplatePipeline pipeline) {
        return PROFILES.sharedAssembler(pipeline);
    }
}
