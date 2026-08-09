package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaFPacEntityFactory;
import generate.java.st.MockDataEntity;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.Verbs.CEntitySubtractTo;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/** Retirement proof for the FPac S/subtract operation. */
class CFPacJavaSubtractToRetirementTest
{
    @Test
    void parserShapeUsesPureSemanticEntityAndSharedTemplate()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CEntitySubtractTo subtract = new CJavaFPacEntityFactory(catalog, null)
            .NewEntitySubtractTo(1);
        MockDataEntity amount = new MockDataEntity(1, "AMOUNT");
        MockDataEntity target = new MockDataEntity(1, "TARGET");
        subtract.SetSubstract(target, amount, target);

        assertEquals(CEntitySubtractTo.class, subtract.getClass());
        assertEquals("subtract(TARGET, AMOUNT).to(TARGET) ;",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(subtract, JavaTemplateRole.FPAC_REFERENCE).trim());
    }
}
