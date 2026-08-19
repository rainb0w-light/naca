package generate.templates.recursive;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/** Declarative child-role transitions shared by Java template profiles. */
public final class JavaTemplateRolePolicy
{
    private static final Map<String, JavaTemplateRole> ABSOLUTE_TRANSITIONS = Map.of(
        "declarationChildren", JavaTemplateRole.DECLARATION,
        "executableChildren", JavaTemplateRole.REFERENCE,
        "commentChildren", JavaTemplateRole.REFERENCE,
        "forms", JavaTemplateRole.DECLARATION,
        "fields", JavaTemplateRole.DECLARATION);

    private static final Map<JavaTemplateRole, Map<String, JavaTemplateRole>>
        ROLE_TRANSITIONS = Map.of(
            JavaTemplateRole.DECLARATION, transitions(
                JavaTemplateRole.DECLARATION,
                "children", "activeChildren", "attributeChildren"),
            JavaTemplateRole.FPAC_ROOT, fpacTransitions(),
            JavaTemplateRole.FPAC_REFERENCE, fpacTransitions());

    private static final Set<JavaTemplateRole> FPAC_ROLES = Set.of(
        JavaTemplateRole.FPAC_ROOT, JavaTemplateRole.FPAC_REFERENCE);

    /** Executes the child role operation. */
    public JavaTemplateRole childRole(JavaTemplateRole parentRole, String propertyName)
    {
        JavaTemplateRole absolute = ABSOLUTE_TRANSITIONS.get(propertyName);
        if (absolute != null)
        {
            return absolute;
        }

        Map<String, JavaTemplateRole> transitions = parentRole == null
            ? null
            : ROLE_TRANSITIONS.get(parentRole);
        if (transitions != null)
        {
            JavaTemplateRole transition = transitions.get(propertyName);
            if (transition != null)
            {
                return transition;
            }
        }
        if (parentRole != null
            && FPAC_ROLES.contains(parentRole)
            && propertyName.endsWith("Bloc"))
        {
            return JavaTemplateRole.FPAC_REFERENCE;
        }
        return JavaTemplateRole.REFERENCE;
    }

    private static Map<String, JavaTemplateRole> fpacTransitions()
    {
        return transitions(
            JavaTemplateRole.FPAC_REFERENCE,
            "children", "activeChildren", "actions", "activeActions",
            "displayItems", "reference", "dividend", "divisor", "result");
    }

    private static Map<String, JavaTemplateRole> transitions(
        JavaTemplateRole role, String... propertyNames)
    {
        LinkedHashMap<String, JavaTemplateRole> transitions = new LinkedHashMap<>();
        for (String propertyName : propertyNames)
        {
            transitions.put(propertyName, role);
        }
        return Map.copyOf(transitions);
    }
}
