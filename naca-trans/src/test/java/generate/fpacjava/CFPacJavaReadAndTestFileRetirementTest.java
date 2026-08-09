package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaFPacEntityFactory;
import generate.java.st.MockDataEntity;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.expression.CEntityFunctionCall;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/** Retirement proof for the FPac ReadAndTestFile reference backend. */
class CFPacJavaReadAndTestFileRetirementTest
{
    @Test
    void factoryReturnsPureSemanticCallAndRendersProbe()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CEntityFunctionCall call = new CJavaFPacEntityFactory(catalog, null)
            .NewEntityFunctionCall("ReadAndTestFile", new MockDataEntity(1, "INPUT_FILE"));

        assertEquals(CEntityFunctionCall.class, call.getClass());
        assertEquals("ReadAndTestFile", call.getFunctionName());
        assertEquals("INPUT_FILE.read().atEnd()",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(call, JavaTemplateRole.FPAC_REFERENCE).trim());
    }
}
