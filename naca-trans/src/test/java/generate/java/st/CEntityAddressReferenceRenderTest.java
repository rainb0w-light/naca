package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityAddressReference;
import utils.CObjectCatalog;

class CEntityAddressReferenceRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String render(CEntityAddressReference address)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(address, JavaTemplateRole.REFERENCE);
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        MockDataEntity field = new MockDataEntity(1, "FIELD");
        assertEquals(CEntityAddressReference.class,
            new CJavaEntityFactory(catalog, null).NewEntityAddressReference(field).getClass());
        assertEquals(CEntityAddressReference.class,
            new CJavaEntityFactory(catalog, null).NewEntityAddressReference(field).getClass());
    }

    @Test
    void recursivelyRendersTheAddressedReference()
    {
        assertEquals("addressOf(FIELD)",
            render(new CEntityAddressReference(
                catalog, new MockDataEntity(1, "FIELD"))));
    }

    @Test
    void missingReferenceRemainsExplicit()
    {
        assertEquals("addressOf([UNDEFINED])",
            render(new CEntityAddressReference(catalog, null)));
    }
}
