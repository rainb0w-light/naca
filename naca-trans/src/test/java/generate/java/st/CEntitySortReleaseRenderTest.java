package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.Verbs.CEntitySortRelease;
import utils.CObjectCatalog;

class CEntitySortReleaseRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String render(CEntitySortRelease release)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(release, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntitySortRelease.class,
            new CJavaEntityFactory(catalog, null).NewEntitySortRelease(1).getClass());
        assertEquals(CEntitySortRelease.class,
            new CJavaEntityFactory(catalog, null).NewEntitySortRelease(1).getClass());
    }

    @Test
    void rendersReleaseWithAndWithoutFromReference()
    {
        CEntitySortRelease release = new CEntitySortRelease(1, catalog);
        release.setDataReference(new MockDataEntity(1, "SORT_RECORD"));
        assertEquals("release(SORT_RECORD) ;", render(release));

        release.setDataReference(
            new MockDataEntity(1, "SORT_RECORD"),
            new MockDataEntity(1, "SOURCE_RECORD"));
        assertEquals("release(SORT_RECORD, SOURCE_RECORD) ;", render(release));
    }

    @Test
    void missingSortFieldRemainsExplicit()
    {
        assertEquals("release([Undefined]) ;",
            render(new CEntitySortRelease(1, catalog)));
    }
}
