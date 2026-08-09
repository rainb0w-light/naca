package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import java.util.List;
import org.junit.jupiter.api.Test;
import semantic.CEntityProcedure;
import semantic.Verbs.CEntityGotoDepending;
import utils.CObjectCatalog;

class CEntityGotoDependingRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private void paragraph(String name)
    {
        new CEntityProcedure(1, name, catalog, null);
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        paragraph("FIRST");
        assertInstanceOf(CEntityGotoDepending.class,
            new CJavaEntityFactory(catalog, null).NewEntityGotoDepending(
                1, List.of("FIRST"), new MockDataEntity(1, "CHOICE"), null));
        assertInstanceOf(CEntityGotoDepending.class,
            new CJavaEntityFactory(catalog, null).NewEntityGotoDepending(
                1, List.of("FIRST"), new MockDataEntity(1, "CHOICE"), null));
    }

    @Test
    void rendersOrderedTargetsAndDependingValue()
    {
        paragraph("FIRST-PARA");
        paragraph("SECOND-PARA");
        CEntityGotoDepending goTo = new CEntityGotoDepending(
            1, catalog, List.of("FIRST-PARA", "SECOND-PARA"),
            new MockDataEntity(1, "CHOICE"), null);

        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(goTo, JavaTemplateRole.REFERENCE).trim();

        assertEquals(
            "goTo(new CJMapRunnable[]{FIRST_PARA,SECOND_PARA,}, CHOICE) ;",
            output);
    }
}
