package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.Verbs.CEntityReplace;
import utils.CObjectCatalog;

class CEntityReplaceRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static MockDataEntity ref(String value)
    {
        return new MockDataEntity(1, value);
    }

    private static String render(CEntityReplace replace)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(replace, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityReplace.class,
            new CJavaEntityFactory(catalog, null).NewEntityReplace(1).getClass());
        assertEquals(CEntityReplace.class,
            new CJavaEntityFactory(catalog, null).NewEntityReplace(1).getClass());
    }

    @Test
    void rendersCustomAndFigurativeReplaceModes()
    {
        CEntityReplace replace = new CEntityReplace(1, null);
        replace.SetReplace(ref("TARGET"));

        replace.AddReplaceAll();
        replace.ReplaceSpaces();
        replace.ByZeros();
        replace.AddReplaceFirst();
        replace.ReplaceZeros();
        replace.ByLowValues();
        replace.AddReplaceLeading();
        replace.ReplaceLowValues();
        replace.ByHighValues();
        replace.AddReplaceAll();
        replace.ReplaceHighValues();
        replace.BySpaces();

        replace.AddReplaceFirst();
        replace.ReplaceSpaces();
        replace.ByData(ref("FIRST_BY"));
        replace.AddReplaceLeading();
        replace.ReplaceHighValues();
        replace.ByData(ref("LEADING_BY"));
        replace.AddReplaceAll();
        replace.ReplaceData(ref("PATTERN"));
        replace.ByData(ref("REPLACEMENT"));

        String output = render(replace);
        assertTrue(output.contains("inspectReplacing(TARGET).allSpaces().byZero() ;"));
        assertTrue(output.contains("inspectReplacing(TARGET).firstZeros().byLowValues() ;"));
        assertTrue(output.contains("inspectReplacing(TARGET).leadingLowValues().byHighValues() ;"));
        assertTrue(output.contains("inspectReplacing(TARGET).allHighValues().bySpaces() ;"));
        assertTrue(output.contains("inspectReplacing(TARGET).firstSpaces().by(FIRST_BY) ;"));
        assertTrue(output.contains(
            "inspectReplacing(TARGET).leadingHighValues().by(LEADING_BY) ;"));
        assertTrue(output.contains(
            "inspectReplacing(TARGET).all(PATTERN).by(REPLACEMENT) ;"));
    }
}
