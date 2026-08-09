package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaFPacEntityFactory;
import generate.LegacyDataRenderer;
import semantic.CEntityStructure;
import semantic.CPositionedBufferReference;
import semantic.CSubStringAttributReference;
import semantic.Verbs.CEntityConvertReference;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/** Retirement proof for FPac positioned-buffer references. */
class CFPacJavaSubStringAttributeReferenceRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void plainBufferAndOpenConversionComposeThroughPureSemanticNode()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityStructure file = new CEntityStructure(1, "INPUT-FILE", catalog, "01");
        CSubStringAttributReference plain = factory.NewEntitySubString(1);
        plain.SetReference(file, factory.NewEntityExprTerminal(factory.NewEntityNumber("2")),
            factory.NewEntityExprTerminal(factory.NewEntityNumber("5")));

        assertEquals(CPositionedBufferReference.class, plain.getClass());
        assertEquals("buffer(INPUT_FILE, 2, 5)", LegacyDataRenderer.renderReference(plain, 1));

        CEntityConvertReference conversion = factory.NewEntityConvert(1);
        conversion.convertToPacked(file);
        CSubStringAttributReference converted = factory.NewEntitySubString(1);
        converted.SetReference(conversion,
            factory.NewEntityExprTerminal(factory.NewEntityNumber("2")),
            factory.NewEntityExprTerminal(factory.NewEntityNumber("5")));
        assertEquals("bufferP(INPUT_FILE, 2, 5)",
            LegacyDataRenderer.renderReference(converted, 1));
    }
}
