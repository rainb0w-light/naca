package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.expression.CEntityConstantValue;
import utils.CObjectCatalog;

class CEntityConstantValueRenderTest
{
    @Test
    void bothFactoriesCachePureSemanticConstants()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CJavaEntityFactory directFactory = new CJavaEntityFactory(catalog, null);
        directFactory.addSpecialConstantValue("source", "LanguageCode.EN");
        assertInstanceOf(CEntityConstantValue.class,
            directFactory.getSpecialConstantValue("source"));

        CJavaEntityFactoryST stFactory = new CJavaEntityFactoryST(catalog, null);
        stFactory.addSpecialConstantValue("source", "LanguageCode.FR");
        assertInstanceOf(CEntityConstantValue.class,
            stFactory.getSpecialConstantValue("source"));
    }

    @Test
    void rendersConfiguredSymbolWithoutPreRenderingItInTheEntity()
    {
        CDataEntity constant = new CEntityConstantValue("LanguageCode.EN");
        assertEquals("LanguageCode.EN",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(constant, JavaTemplateRole.REFERENCE));
        assertEquals("LanguageCode.EN", constant.GetConstantValue());
    }
}
