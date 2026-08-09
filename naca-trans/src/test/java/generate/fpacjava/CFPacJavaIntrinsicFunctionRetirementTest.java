package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaFPacEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import java.util.List;
import semantic.expression.CEntityExprTerminal;
import semantic.expression.CEntityIntrinsicFunction;
import semantic.expression.CEntityNumber;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/** Retirement proof for the dormant FPac intrinsic-function backend. */
class CFPacJavaIntrinsicFunctionRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        CEntityIntrinsicFunction function = new CJavaFPacEntityFactory(catalog, null)
            .NewEntityIntrinsicFunction("ORD",
                List.of(new CEntityExprTerminal(new CEntityNumber(catalog, "65"))));

        assertEquals(CEntityIntrinsicFunction.class, function.getClass());
    }

    @Test
    void sharedRecursiveTemplatePreservesLegacyOutputUnderFpacRole()
    {
        CEntityIntrinsicFunction function = new CJavaFPacEntityFactory(catalog, null)
            .NewEntityIntrinsicFunction("ORD-MAX",
                List.of(
                    new CEntityExprTerminal(new CEntityNumber(catalog, "65")),
                    new CEntityExprTerminal(new CEntityNumber(catalog, "66"))));

        assertEquals("CobolIntrinsicFunctions.ord_max(65, 66)",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(function, JavaTemplateRole.FPAC_REFERENCE).trim());
    }
}
