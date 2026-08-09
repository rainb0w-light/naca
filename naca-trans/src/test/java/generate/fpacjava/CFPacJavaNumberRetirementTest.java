package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import generate.CJavaFPacEntityFactory;
import generate.LegacyDataRenderer;
import semantic.expression.CEntityNumber;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/** Retirement proof for the FPac numeric literal backend. */
class CFPacJavaNumberRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void factoryReturnsPureSemanticNumberAndPreservesRawLexeme()
    {
        CEntityNumber number = new CJavaFPacEntityFactory(catalog, null)
            .NewEntityNumber("00050");

        assertEquals(CEntityNumber.class, number.getClass());
        assertEquals("00050", LegacyDataRenderer.renderReference(number, 1));
        assertFalse(number.isValNeeded());
    }

    @Test
    void fpacHexLexemeUsesRuntimeHexHelper()
    {
        CEntityNumber number = new CJavaFPacEntityFactory(catalog, null)
            .NewEntityNumber("0x1F");

        assertEquals("hexa(\"1F\")", LegacyDataRenderer.renderReference(number, 1));
    }
}
