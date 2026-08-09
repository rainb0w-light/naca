package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityFileDescriptor;
import semantic.Verbs.CEntityBreak;
import semantic.Verbs.CEntitySortReturn;
import utils.CObjectCatalog;

class CEntitySortReturnRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static CEntityFileDescriptor file(String name)
    {
        return new CEntityFileDescriptor(1, name, null)
        {
            @Override
            protected void RegisterMySelfToCatalog()
            {
                // No-op test descriptor.
            }
        };
    }

    private static String render(CEntitySortReturn sortReturn)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(sortReturn, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntitySortReturn.class,
            new CJavaEntityFactory(catalog, null).NewEntitySortReturn(1).getClass());
        assertEquals(CEntitySortReturn.class,
            new CJavaEntityFactory(catalog, null).NewEntitySortReturn(1).getClass());
    }

    @Test
    void rendersReturnWithAndWithoutIntoReference()
    {
        CEntitySortReturn sortReturn = new CEntitySortReturn(1, catalog);
        sortReturn.setDataReference(file("SORT-FILE"));
        assertEquals("returnSort(SORT_FILE) ;", render(sortReturn));

        sortReturn.setDataReference(
            file("SORT-FILE"), new MockDataEntity(1, "OUTPUT_RECORD"));
        assertEquals("returnSort(SORT_FILE, OUTPUT_RECORD) ;", render(sortReturn));
    }

    @Test
    void recursivelyRendersAtEndAndNotAtEndBodies()
    {
        CEntitySortReturn sortReturn = new CEntitySortReturn(1, catalog);
        sortReturn.setDataReference(file("SORT-FILE"));
        MockBloc atEnd = new MockBloc(1);
        atEnd.addChild(new CEntityBreak(1, catalog));
        MockBloc notAtEnd = new MockBloc(1);
        notAtEnd.addChild(new CEntityBreak(1, catalog));
        sortReturn.SetAtEndBloc(atEnd);
        sortReturn.SetNotAtEndBloc(notAtEnd);

        String output = render(sortReturn);
        assertTrue(output.contains("if (returnSort(SORT_FILE).atEnd())"), output);
        assertTrue(output.contains("else"), output);
        assertEquals(2, output.split("break;", -1).length - 1, output);
    }

    @Test
    void missingSortFileRemainsExplicit()
    {
        assertEquals("returnSort([Undefined]) ;",
            render(new CEntitySortReturn(1, catalog)));
    }
}
