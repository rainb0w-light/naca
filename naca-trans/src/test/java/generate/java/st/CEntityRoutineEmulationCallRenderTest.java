package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.Verbs.CEntityRoutineEmulationCall;
import utils.CObjectCatalog;

class CEntityRoutineEmulationCallRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String render(CEntityRoutineEmulationCall call)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(call, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void bothCobolFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityRoutineEmulationCall.class,
            new CJavaEntityFactory(catalog, null).NewEntityRoutineEmulationCall(1).getClass());
        assertEquals(CEntityRoutineEmulationCall.class,
            new CJavaEntityFactory(catalog, null).NewEntityRoutineEmulationCall(1).getClass());
    }

    @Test
    void rendersStaticAndDynamicEmulationCalls()
    {
        CEntityRoutineEmulationCall staticCall = new CEntityRoutineEmulationCall(1, null);
        staticCall.SetDisplay("Pub2000Routines.fillDefaultValueFromDB");
        staticCall.AddParameter(new MockDataEntity(1, "PARAM"));
        assertEquals(
            "Pub2000Routines.fillDefaultValueFromDB(PARAM) ;", render(staticCall));

        CEntityRoutineEmulationCall dynamicCall = new CEntityRoutineEmulationCall(1, null);
        dynamicCall.SetDisplay("tools.dynamicAllocation");
        dynamicCall.AddParameter(new MockDataEntity(1, "REQUEST"));
        assertEquals(
            "tools.dynamicAllocation(new Var[] {REQUEST}) ;", render(dynamicCall));
    }
}
