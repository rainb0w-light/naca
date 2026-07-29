package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import java.util.List;
import org.junit.jupiter.api.Test;
import semantic.Verbs.CEntityBreak;
import semantic.Verbs.CEntitySubtractTo;
import utils.CObjectCatalog;

class CEntitySubtractToRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String render(CEntitySubtractTo subtract)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(subtract, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntitySubtractTo.class,
            new CJavaEntityFactory(catalog, null).NewEntitySubtractTo(1).getClass());
        assertEquals(CEntitySubtractTo.class,
            new CJavaEntityFactoryST(catalog, null).NewEntitySubtractTo(1).getClass());
    }

    @Test
    void rendersValuesAndEveryDestination()
    {
        CEntitySubtractTo subtract = new CEntitySubtractTo(1, catalog);
        subtract.SetSubstract(
            new MockDataEntity(1, "BALANCE"),
            List.of(new MockDataEntity(1, "FEE"), new MockDataEntity(1, "TAX")),
            List.of(new MockDataEntity(1, "NET"), new MockDataEntity(1, "AUDIT")));

        assertEquals(
            "subtract(BALANCE, FEE, TAX).to(NET).to(AUDIT) ;",
            render(subtract));
    }

    @Test
    void rendersIncrementAndDecrementOptimizations()
    {
        CEntitySubtractTo subtract = new CEntitySubtractTo(1, catalog);
        subtract.SetSubstract(
            new MockDataEntity(1, "COUNTER"),
            List.of(new MockDataEntity(1, "1")),
            List.of());
        assertEquals("dec(COUNTER) ;", render(subtract));

        subtract.Clear();
        subtract.SetSubstract(
            new MockDataEntity(1, "COUNTER"),
            List.of(new MockDataEntity(1, "-1")),
            List.of());
        assertEquals("inc(COUNTER) ;", render(subtract));
    }

    @Test
    void recursivelyRendersOnSizeErrorBody()
    {
        CEntitySubtractTo subtract = new CEntitySubtractTo(1, catalog);
        subtract.SetSubstract(
            new MockDataEntity(1, "BALANCE"),
            List.of(new MockDataEntity(1, "FEE")),
            List.of(new MockDataEntity(1, "NET")));
        MockBloc onError = new MockBloc(1);
        onError.AddChild(new CEntityBreak(1, catalog));
        subtract.SetOnErrorBloc(onError);

        String output = render(subtract);
        assertTrue(output.contains(
            "if (subtract(BALANCE, FEE).to(NET).isError())"), output);
        assertTrue(output.contains("break;"), output);
    }
}
