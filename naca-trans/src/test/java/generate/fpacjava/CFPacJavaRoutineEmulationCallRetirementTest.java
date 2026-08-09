package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaFPacEntityFactory;
import generate.java.st.MockDataEntity;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.Verbs.CEntityRoutineEmulationCall;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/** Retirement proof for FPac routine-emulation calls. */
class CFPacJavaRoutineEmulationCallRetirementTest
{
    @Test
    void factoryReturnsPureSemanticCallAndSharedTemplatePreservesShapes()
    {
        CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
        CEntityRoutineEmulationCall call = new CJavaFPacEntityFactory(catalog, null)
            .NewEntityRoutineEmulationCall(1);
        assertEquals(CEntityRoutineEmulationCall.class, call.getClass());
        assertTrue(call.ignore());

        call.SetDisplay("tools.dynamicAllocation");
        call.AddParameter(new MockDataEntity(1, "AREA"));
        assertEquals("tools.dynamicAllocation(new Var[] {AREA}) ;", render(call));
    }

    private static String render(CEntityRoutineEmulationCall call)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(call, JavaTemplateRole.FPAC_REFERENCE).trim();
    }
}
