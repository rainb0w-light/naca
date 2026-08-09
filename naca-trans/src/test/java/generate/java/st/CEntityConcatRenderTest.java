package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.expression.CEntityConcat;
import utils.CObjectCatalog;

class CEntityConcatRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String render(CEntityConcat concat)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(concat, JavaTemplateRole.REFERENCE);
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        MockDataEntity left = new MockDataEntity(1, "LEFT");
        MockDataEntity right = new MockDataEntity(1, "RIGHT");
        assertEquals(CEntityConcat.class,
            new CJavaEntityFactory(catalog, null).NewEntityConcat(left, right).getClass());
        assertEquals(CEntityConcat.class,
            new CJavaEntityFactory(catalog, null).NewEntityConcat(left, right).getClass());
    }

    @Test
    void recursivelyRendersBothOperands()
    {
        assertEquals("concat(LEFT, RIGHT)",
            render(new CEntityConcat(
                catalog,
                new MockDataEntity(1, "LEFT"),
                new MockDataEntity(1, "RIGHT"))));
    }
}
