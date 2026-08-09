package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.Verbs.CEntityInspectConverting;

class CEntityInspectConvertingRenderTest
{
    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertInstanceOf(CEntityInspectConverting.class,
            new CJavaEntityFactory(null, null).NewEntityInspectConverting(1));
        assertInstanceOf(CEntityInspectConverting.class,
            new CJavaEntityFactory(null, null).NewEntityInspectConverting(1));
    }

    @Test
    void rendersSemanticOperandsThroughTheRuntimeChain()
    {
        CEntityInspectConverting converting = new CEntityInspectConverting(1, null);
        converting.SetConvert(new MockDataEntity(1, "SOURCE"));
        converting.SetFrom(new MockDataEntity(1, "FROM_VALUE"));
        converting.SetTo(new MockDataEntity(1, "TO_VALUE"));

        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(converting, JavaTemplateRole.REFERENCE).trim();

        assertEquals(
            "inspectConverting(SOURCE).to(FROM_VALUE, TO_VALUE) ;",
            output);
    }
}
