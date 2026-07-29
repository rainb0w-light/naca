package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityAttribute;
import utils.CObjectCatalog;

class CEntityAttributeRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityAttribute.class,
            new CJavaEntityFactory(catalog, null).NewEntityAttribute(1, "FIELD").getClass());
        assertEquals(CEntityAttribute.class,
            new CJavaEntityFactoryST(catalog, null).NewEntityAttribute(1, "FIELD").getClass());
    }

    @Test
    void assemblerSeparatesReferenceFromDeclaration()
    {
        CEntityAttribute attribute = new CEntityAttribute(1, "WS-FIELD", catalog);
        attribute.SetLevel("05");
        attribute.SetTypeString(8);

        assertEquals("WS_FIELD", TemplateLoader.getRecursiveAssembler()
            .renderRoot(attribute, JavaTemplateRole.REFERENCE));
        assertTrue(TemplateLoader.getRecursiveAssembler()
            .renderRoot(attribute, JavaTemplateRole.DECLARATION)
            .contains("Var WS_FIELD = declare.level(05).picX(8).var() ;"));
    }
}
