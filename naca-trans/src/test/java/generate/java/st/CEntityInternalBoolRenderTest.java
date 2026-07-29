package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.expression.CEntityInternalBool;
import utils.CObjectCatalog;

class CEntityInternalBoolRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityInternalBool.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityInternalBool("Search-Found").getClass());
        assertEquals(CEntityInternalBool.class,
            new CJavaEntityFactoryST(catalog, null)
                .NewEntityInternalBool("Search-Found").getClass());
    }

    @Test
    void assemblerSeparatesDeclarationFromReference()
    {
        CEntityInternalBool value =
            new CJavaEntityFactory(catalog, null)
                .NewEntityInternalBool("Search-Found");
        assertEquals("Var Search_Found = declare.bool() ;",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(value, JavaTemplateRole.DECLARATION));
        assertEquals("Search_Found",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(value, JavaTemplateRole.REFERENCE));
    }
}
