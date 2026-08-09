package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaFPacEntityFactory;
import generate.LegacyDataRenderer;
import semantic.expression.CEntityString;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/** Retirement proof for FPac string literals. */
class CFPacJavaStringRetirementTest
{
    @Test
    void factoryReturnsPureSemanticStringAndSharedEscapingMatchesLegacy()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CEntityString string = new CJavaFPacEntityFactory(catalog, null)
            .NewEntityString("A\"B\\C\n".toCharArray());

        assertEquals(CEntityString.class, string.getClass());
        assertEquals("\"A\\\"B\\\\C\\n\"", LegacyDataRenderer.renderReference(string, 1));
    }
}
