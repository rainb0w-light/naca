package generate.templates.recursive;

import generate.templates.recursive.java.JavaSemanticTemplateBindings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * Builds a lazy tree of ST instances from a semantic model tree.
 * Only renderRoot is allowed to flatten that tree to source text.
 */
public final class JavaTemplateAssembler
{
    private final STGroup templateGroup;
    private final JavaSemanticTemplateBindings bindings;

    public JavaTemplateAssembler(STGroup templateGroup)
    {
        this.templateGroup = Objects.requireNonNull(templateGroup, "templateGroup");
        this.bindings = JavaSemanticTemplateBindings.loadDefault();
        this.templateGroup.registerModelAdaptor(
            Object.class, new RecursiveSemanticModelAdaptor(this));
        // Fail closed: a missing binding or property must abort generation
        // instead of being swallowed by ST4's default listener (empty output).
        this.templateGroup.setListener(new RecursiveTemplateErrorListener());
    }

    public ST renderNode(Object model)
    {
        Objects.requireNonNull(model, "model");
        String templateName = bindings.findTemplateName(model.getClass());
        if (templateName != null)
        {
            return template(templateName).add("entity", model);
        }
        throw new MissingTemplateRendererException(model.getClass());
    }

    public ST renderOptional(Object model)
    {
        return model == null ? null : renderNode(model);
    }

    public List<ST> renderNodes(Collection<?> models)
    {
        Objects.requireNonNull(models, "models");
        List<ST> templates = new ArrayList<>(models.size());
        for (Object model : models)
        {
            templates.add(renderNode(model));
        }
        return Collections.unmodifiableList(templates);
    }

    public ST template(String templateName)
    {
        ST template = templateGroup.getInstanceOf(templateName);
        if (template == null)
        {
            throw new IllegalArgumentException("ST template not found: " + templateName);
        }
        return template;
    }

    public String renderRoot(Object rootModel)
    {
        return renderNode(rootModel).render();
    }
}
