package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaFPacEntityFactory;
import generate.java.st.MockDataEntity;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.Verbs.CEntityMultiply;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/** Retirement proof for the FPac multiply backend. */
class CFPacJavaMultiplyRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void factoryReturnsPureSemanticMultiply()
    {
        assertEquals(CEntityMultiply.class,
            new CJavaFPacEntityFactory(catalog, null).NewEntityMultiply(1).getClass());
    }

    @Test
    void fpacParserShapeRendersThroughSharedTemplate()
    {
        CEntityMultiply multiply = new CJavaFPacEntityFactory(catalog, null)
            .NewEntityMultiply(1);
        multiply.SetMultiply(new MockDataEntity(1, "SOURCE"),
            new MockDataEntity(1, "TARGET"), false);

        assertEquals("multiply(SOURCE, TARGET).to(TARGET) ;",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(multiply, JavaTemplateRole.FPAC_REFERENCE).trim());
    }
}
