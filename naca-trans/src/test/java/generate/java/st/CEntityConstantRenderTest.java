package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.expression.CEntityConstant;

class CEntityConstantRenderTest
{
    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityConstant.class,
            new CJavaEntityFactory(null, null)
                .NewEntityConstant(CEntityConstant.Value.HIGH_VALUE).getClass());
        assertEquals(CEntityConstant.class,
            new CJavaEntityFactoryST(null, null)
                .NewEntityConstant(CEntityConstant.Value.SPACES).getClass());
    }

    @Test
    void rendersBothSupportedFigurativeConstants()
    {
        assertEquals("CobolConstant.HighValue",
            TemplateLoader.getRecursiveAssembler().renderRoot(
                new CEntityConstant(CEntityConstant.Value.HIGH_VALUE),
                JavaTemplateRole.REFERENCE));
        assertEquals("CobolConstant.Spaces",
            TemplateLoader.getRecursiveAssembler().renderRoot(
                new CEntityConstant(CEntityConstant.Value.SPACES),
                JavaTemplateRole.REFERENCE));
    }
}
