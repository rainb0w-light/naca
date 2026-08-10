package generate.templates.recursive;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class JavaTemplateRolePolicyTest
{
    private final JavaTemplateRolePolicy policy = new JavaTemplateRolePolicy();

    @Test
    void rootNamedCollectionsHaveAbsoluteRoles()
    {
        assertEquals(JavaTemplateRole.DECLARATION,
            policy.childRole(JavaTemplateRole.ROOT, "declarationChildren"));
        assertEquals(JavaTemplateRole.REFERENCE,
            policy.childRole(JavaTemplateRole.ROOT, "executableChildren"));
        assertEquals(JavaTemplateRole.DECLARATION,
            policy.childRole(JavaTemplateRole.ROOT, "forms"));
    }

    @Test
    void declarationContainersPropagateDeclarationRole()
    {
        assertEquals(JavaTemplateRole.DECLARATION,
            policy.childRole(JavaTemplateRole.DECLARATION, "children"));
        assertEquals(JavaTemplateRole.DECLARATION,
            policy.childRole(JavaTemplateRole.DECLARATION, "attributeChildren"));
    }

    @Test
    void fpacContainersAndOperandsPropagateFpacReferenceRole()
    {
        assertEquals(JavaTemplateRole.FPAC_REFERENCE,
            policy.childRole(JavaTemplateRole.FPAC_ROOT, "children"));
        assertEquals(JavaTemplateRole.FPAC_REFERENCE,
            policy.childRole(JavaTemplateRole.FPAC_REFERENCE, "onErrorBloc"));
        assertEquals(JavaTemplateRole.FPAC_REFERENCE,
            policy.childRole(JavaTemplateRole.FPAC_REFERENCE, "dividend"));
    }

    @Test
    void unknownPropertiesFailClosedToOrdinaryReferenceRole()
    {
        assertEquals(JavaTemplateRole.REFERENCE,
            policy.childRole(JavaTemplateRole.ROOT, "unknown"));
    }
}
