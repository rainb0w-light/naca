package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityBloc;
import semantic.Verbs.CEntityBreak;
import semantic.Verbs.CEntityContinue;
import semantic.Verbs.CEntitySearch;

class CEntitySearchRenderTest
{
    private static String render(CEntitySearch search)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(search, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntitySearch.class,
            new CJavaEntityFactory(null, null).NewEntitySearch(1).getClass());
        assertInstanceOf(CEntitySearch.class,
            new CJavaEntityFactoryST(null, null).NewEntitySearch(1));
    }

    @Test
    void recursivelyRendersSearchAndElseBodies()
    {
        CEntitySearch search = new CEntitySearch(1, null);
        search.setVariable(
            new MockDataEntity(1, "TABLE"),
            new MockDataEntity(1, "IDX"));
        search.AddChild(new CEntityContinue(1, null));
        CEntityBloc otherwise = new CEntityBloc(1, null);
        otherwise.AddChild(new CEntityBreak(1, null));
        search.setElseBloc(otherwise);

        assertEquals(
            "for (move(false, Search_Found); isNot(Search_Found) "
                + "&& isLessOrEqual(IDX, getNbOccurs(TABLE)); inc(1, IDX)) {\n"
                + "// CONTINUE \n"
                + "}\n"
                + "if (isNot(Search_Found)) {\n"
                + "break;\n"
                + "}",
            render(search));
    }
}
