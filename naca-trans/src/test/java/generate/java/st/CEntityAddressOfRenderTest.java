package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.expression.CEntityAddressOf;
import utils.CObjectCatalog;

class CEntityAddressOfRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String render(CEntityAddressOf address)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(address, JavaTemplateRole.REFERENCE);
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        MockDataEntity field = new MockDataEntity(1, "FIELD");
        assertEquals(CEntityAddressOf.class,
            new CJavaEntityFactory(catalog, null).NewEntityAddressOf(field).getClass());
        assertEquals(CEntityAddressOf.class,
            new CJavaEntityFactoryST(catalog, null).NewEntityAddressOf(field).getClass());
    }

    @Test
    void recursivelyRendersTheAddressedReference()
    {
        assertEquals("addressOf(FIELD)",
            render(new CEntityAddressOf(
                catalog, new MockDataEntity(1, "FIELD"))));
    }

    @Test
    void missingReferenceRemainsExplicit()
    {
        assertEquals("addressOf([UNDEFINED])",
            render(new CEntityAddressOf(catalog, null)));
    }
}
