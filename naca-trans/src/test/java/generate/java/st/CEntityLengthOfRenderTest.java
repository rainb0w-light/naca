package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.expression.CEntityLengthOf;
import utils.CObjectCatalog;

class CEntityLengthOfRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        MockDataEntity field = new MockDataEntity(1, "FIELD");
        assertEquals(CEntityLengthOf.class,
            new CJavaEntityFactory(catalog, null).NewEntityLengthOf(field).getClass());
        assertEquals(CEntityLengthOf.class,
            new CJavaEntityFactory(catalog, null).NewEntityLengthOf(field).getClass());
    }

    @Test
    void assemblerRecursivelyRendersTheReference()
    {
        assertEquals("lengthOf(FIELD)",
            TemplateLoader.getRecursiveAssembler().renderRoot(
                new CEntityLengthOf(catalog, new MockDataEntity(1, "FIELD")),
                JavaTemplateRole.REFERENCE));
    }
}
