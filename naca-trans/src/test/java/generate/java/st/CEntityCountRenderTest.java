package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.Verbs.CEntityCount;
import utils.CObjectCatalog;

class CEntityCountRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String render(CEntityCount count)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(count, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertInstanceOf(CEntityCount.class,
            new CJavaEntityFactory(catalog, null).NewEntityCount(1));
        assertInstanceOf(CEntityCount.class,
            new CJavaEntityFactory(catalog, null).NewEntityCount(1));
    }

    @Test
    void rendersAllAndLeadingTallying()
    {
        CEntityCount count = new CEntityCount(1, catalog);
        count.SetCount(new MockDataEntity(1, "SOURCE"));
        count.CountAll(new MockDataEntity(1, "ALL_TOKEN"));
        count.CountLeading(new MockDataEntity(1, "LEADING_TOKEN"));
        count.SetToVar(new MockDataEntity(1, "RESULT"));

        assertEquals(
            "inspectTallying(SOURCE).countAll(ALL_TOKEN)"
                + ".countLeading(LEADING_TOKEN).to(RESULT) ;",
            render(count));
    }

    @Test
    void rendersCharacterRangeTallying()
    {
        CEntityCount count = new CEntityCount(1, catalog);
        count.SetCount(new MockDataEntity(1, "SOURCE"));
        count.CountAfter(new MockDataEntity(1, "AFTER_TOKEN"));
        count.CountBefore(new MockDataEntity(1, "BEFORE_TOKEN"));
        count.SetToVar(new MockDataEntity(1, "RESULT"));

        assertEquals(
            "inspectTallying(SOURCE).forChars().after(AFTER_TOKEN)"
                + ".before(BEFORE_TOKEN).to(RESULT) ;",
            render(count));
    }
}
