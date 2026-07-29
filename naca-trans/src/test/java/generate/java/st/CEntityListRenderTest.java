package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.expression.CEntityList;
import semantic.expression.CEntityNumber;
import utils.CObjectCatalog;

class CEntityListRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityList.class,
            new CJavaEntityFactory(catalog, null).NewEntityList("VALUES").getClass());
        assertEquals(CEntityList.class,
            new CJavaEntityFactoryST(catalog, null).NewEntityList("VALUES").getClass());
    }

    @Test
    void assemblerRendersEmptyAndTypedLists()
    {
        assertEquals("null", render(new CEntityList("EMPTY", catalog)));

        CEntityList numbers = new CEntityList("NUMBERS", catalog);
        numbers.AddData(new CEntityNumber(catalog, "1"));
        numbers.AddData(new CEntityNumber(catalog, "2"));
        assertEquals("new int[] {1, 2}", render(numbers));

        CEntityList variables = new CEntityList("VARIABLES", catalog);
        variables.AddData(new MockDataEntity(1, "FIRST"));
        variables.AddData(new MockDataEntity(1, "SECOND"));
        assertEquals("new Var[] {FIRST, SECOND}", render(variables));
    }

    private String render(CEntityList list)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(list, JavaTemplateRole.REFERENCE);
    }
}
