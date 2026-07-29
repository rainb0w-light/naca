package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityFileDescriptor;
import semantic.CEntityProcedure;
import semantic.Verbs.CEntitySort;
import utils.CObjectCatalog;

class CEntitySortRenderTest
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

    private static String render(CEntitySort sort)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(sort, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntitySort.class,
            new CJavaEntityFactory(catalog, null).NewEntitySort(1).getClass());
        assertInstanceOf(CEntitySort.class,
            new CJavaEntityFactoryST(catalog, null).NewEntitySort(1));
    }

    @Test
    void rendersKeysInputProcedureAndOutputFile()
    {
        CEntitySort sort = new CEntitySort(1, catalog);
        sort.setFileDesriptor(file("SORT-FILE"));
        sort.AddKey(true, new MockDataEntity(1, "KEY_A"));
        sort.AddKey(false, null);
        sort.setInputProcedure(new CEntityProcedure(
            1, "INPUT-PROC", catalog, null));
        sort.setOutputFile(file("OUTPUT-FILE"));

        assertEquals(
            "sort(SORT_FILE).ascKey(KEY_A).descKey([Undefined])"
                + ".usingInput(INPUT_PROC).giving(OUTPUT_FILE).exec() ;",
            render(sort));
    }

    @Test
    void unresolvedProcedureNameRemainsExplicit()
    {
        CEntitySort sort = new CEntitySort(1, null);
        sort.setFileDesriptor(file("SORT-FILE"));
        sort.setInputProcedure("MISSING-PROC");

        assertEquals(
            "sort(SORT_FILE).usingInput([MISSING-PROC]).exec() ;",
            render(sort));
    }
}
