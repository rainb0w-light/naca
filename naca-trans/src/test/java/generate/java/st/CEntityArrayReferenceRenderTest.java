package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityArrayReference;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityExprTerminal;
import utils.CObjectCatalog;

class CEntityArrayReferenceRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String render(CEntityArrayReference array)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(array, JavaTemplateRole.REFERENCE);
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityArrayReference.class,
            new CJavaEntityFactory(catalog, null).NewEntityArrayReference(1).getClass());
        assertEquals(CEntityArrayReference.class,
            new CJavaEntityFactory(catalog, null).NewEntityArrayReference(1).getClass());
    }

    @Test
    void recursivelyRendersOneAndMultipleIndexes()
    {
        CEntityArrayReference one = array("ITEM", terminal("1"));
        CEntityArrayReference two = array("TABLE", terminal("2"), terminal("3"));

        assertEquals("ITEM.getAt(1)", render(one));
        assertEquals("TABLE.getAt(2, 3)", render(two));
    }

    private CEntityArrayReference array(String name, CBaseEntityExpression... indexes)
    {
        CEntityArrayReference array = new CEntityArrayReference(1, catalog);
        array.SetReference(new MockDataEntity(1, name));
        for (CBaseEntityExpression index : indexes)
        {
            array.AddIndex(index);
        }
        return array;
    }

    private CBaseEntityExpression terminal(String value)
    {
        return new CEntityExprTerminal(new MockDataEntity(1, value));
    }
}
