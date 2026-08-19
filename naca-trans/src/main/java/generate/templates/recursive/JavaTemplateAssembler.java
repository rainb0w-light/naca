package generate.templates.recursive;

import generate.templates.recursive.java.JavaSemanticTemplateBindings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * Builds a lazy tree of ST instances from a semantic model tree.
 * Only renderRoot is allowed to flatten that tree to source text.
 */
public final class JavaTemplateAssembler
{
    private final STGroup templateGroup;
    private final JavaSemanticTemplateBindings defaultBindings;
    private final JavaSemanticTemplateBindings declarationBindings;
    private final JavaSemanticTemplateBindings rootBindings;
    private final JavaSemanticTemplateBindings fpacBindings;
    private final JavaSemanticTemplateBindings fpacRootBindings;
    private final JavaTemplateRolePolicy rolePolicy;
    private final Map<ST, JavaTemplateRole> templateRoles =
        Collections.synchronizedMap(new WeakHashMap<>());
    private final Map<ST, Boolean> artifactContexts =
        Collections.synchronizedMap(new WeakHashMap<>());
    private final ThreadLocal<Boolean> artifactRendering =
        ThreadLocal.withInitial(() -> Boolean.FALSE);

    /** Creates a new java template assembler instance. */
    public JavaTemplateAssembler(STGroup templateGroup)
    {
        this(templateGroup, new JavaTemplateRolePolicy());
    }

    /** Creates a new java template assembler instance. */
    public JavaTemplateAssembler(
        STGroup templateGroup, JavaTemplateRolePolicy rolePolicy)
    {
        this.templateGroup = Objects.requireNonNull(templateGroup, "templateGroup");
        this.rolePolicy = Objects.requireNonNull(rolePolicy, "rolePolicy");
        this.defaultBindings = JavaSemanticTemplateBindings.loadDefault();
        this.declarationBindings = JavaSemanticTemplateBindings.loadDeclarations();
        this.rootBindings = JavaSemanticTemplateBindings.loadRoots();
        this.fpacBindings = JavaSemanticTemplateBindings.loadFpac();
        this.fpacRootBindings = JavaSemanticTemplateBindings.loadFpacRoots();
        this.templateGroup.registerModelAdaptor(
            Object.class, new RecursiveSemanticModelAdaptor(this));
        // Fail closed: a missing binding or property must abort generation
        // instead of being swallowed by ST4's default listener (empty output).
        this.templateGroup.setListener(new RecursiveTemplateErrorListener());
    }

    /** Renders the node. */
    public ST renderNode(Object model)
    {
        return renderNode(model, JavaTemplateRole.REFERENCE);
    }

    /** Renders the node. */
    public ST renderNode(Object model, JavaTemplateRole role)
    {
        return renderNode(model, role, false);
    }

    ST renderNode(Object model, JavaTemplateRole role, boolean artifactContext)
    {
        Objects.requireNonNull(model, "model");
        Objects.requireNonNull(role, "role");
        String templateName = bindingsFor(role).findTemplateName(model.getClass());
        String rootTemplateName = rootBindings.findTemplateName(model.getClass());
        if (role != JavaTemplateRole.ROOT
            && rootTemplateName != null
            && rootTemplateName.equals(templateName))
        {
            // The complete concrete inventory includes root-only semantic types.
            // An identical default/root binding marks such a type as requiring an
            // explicit ROOT role; it must never silently emit an artifact while
            // being visited as a reference child.
            throw new MissingTemplateRendererException(model.getClass());
        }
        if (templateName != null)
        {
            ST renderedNode = template(templateName).add("entity", model);
            templateRoles.put(renderedNode, role);
            artifactContexts.put(renderedNode, artifactContext);
            return renderedNode;
        }
        throw new MissingTemplateRendererException(model.getClass());
    }

    /** Renders the optional. */
    public ST renderOptional(Object model)
    {
        return model == null ? null : renderNode(model);
    }

    /** Renders the nodes. */
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

    /** Executes the template operation. */
    public ST template(String templateName)
    {
        ST template = templateGroup.getInstanceOf(templateName);
        if (template == null)
        {
            throw new IllegalArgumentException("ST template not found: " + templateName);
        }
        return template;
    }

    /**
     * Flattens a subtree to source text with an explicit render role. This is the
     * only flattening point in the recursive assembler. Callers must pass the
     * role explicitly: verb/procedure subtrees use {@link JavaTemplateRole#REFERENCE},
     * data declarations use {@link JavaTemplateRole#DECLARATION}, and program /
     * artifact roots use {@link JavaTemplateRole#ROOT}. There is deliberately no
     * no-arg overload, so forgetting the role is a compile-time error and a root
     * can never be silently rendered as a reference.
     */
    public String renderRoot(Object rootModel, JavaTemplateRole role)
    {
        boolean artifactContext = role == JavaTemplateRole.ROOT;
        Boolean previous = artifactRendering.get();
        artifactRendering.set(artifactContext);
        try
        {
            return renderNode(rootModel, role, artifactContext).render();
        }
        finally
        {
            artifactRendering.set(previous);
        }
    }

    private JavaSemanticTemplateBindings bindingsFor(JavaTemplateRole role)
    {
        switch (role)
        {
            case DECLARATION:
                return declarationBindings;
            case ROOT:
                // Root roles consult ONLY the root manifest; there is deliberately
                // no fallback to the default manifest, so a root type without an
                // explicit binding fails closed in renderNode.
                return rootBindings;
            case FPAC_REFERENCE:
                // Independent FPac pipeline: shared reference bindings plus the FPac
                // overrides (semantic-fpac-bindings.properties). Never falls back to the
                // COBOL PERFORM binding for a type FPac redirects.
                return fpacBindings;
            case FPAC_ROOT:
                // Independent FPac pipeline root: consults ONLY the FPac root manifest
                // (semantic-fpac-root-bindings.properties), never the COBOL root manifest,
                // so the shared semantic.CEntityClass maps to the FPac FPacProgram wrapper
                // and the frozen COBOL javaProgramRoot binding is left untouched. No
                // fallback: an FPac root type without an explicit binding fails closed.
                return fpacRootBindings;
            case REFERENCE:
            default:
                return defaultBindings;
        }
    }

    JavaTemplateRole childRole(ST parentTemplate, String propertyName)
    {
        JavaTemplateRole parentRole = templateRoles.get(parentTemplate);
        return rolePolicy.childRole(parentRole, propertyName);
    }

    boolean isArtifactContext(ST template)
    {
        return Boolean.TRUE.equals(artifactRendering.get())
            || Boolean.TRUE.equals(artifactContexts.get(template));
    }
}
