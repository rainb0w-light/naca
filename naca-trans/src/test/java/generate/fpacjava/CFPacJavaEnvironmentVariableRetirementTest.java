package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaFPacEntityFactory;
import generate.java.st.MockDataEntity;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.CEntityEnvironmentVariable;
import semantic.Verbs.CEntityAssignWithAccessor;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/**
 * Phase 2 (FPAC) — retirement proof for the former {@code CFPacJavaEnvironmentVariable}
 * direct backend. The FPac pipeline shares the target-neutral
 * {@link CEntityEnvironmentVariable} semantic data entity with COBOL, so
 * {@link CJavaFPacEntityFactory#NewEntityEnvironmentVariable(String, String, String, boolean)}
 * must hand back the same pure {@code CEntityEnvironmentVariable} the COBOL factories build
 * (no {@code generate.fpacjava} subclass). The accessor/writer/numeric flag are precomputed at
 * construction by the rules engine (e.g. {@code RETCD}, reader {@code "getReturnCode()"},
 * writer {@code "setReturnCode("}).
 *
 * <p>The read path renders through the frozen shared binding
 * {@code semantic.CEntityEnvironmentVariable -> environmentVariableEntity}
 * ({@code "<entity.readAccessor>"}), which the independent FPac reference role inherits verbatim
 * ({@code JavaSemanticTemplateBindings.loadFpac()} layers the shared concrete manifest under
 * {@link JavaTemplateRole#FPAC_REFERENCE}); no FPac override is needed because the retired
 * backend's {@code ExportReference} returned {@code csAccessor} — exactly what
 * {@code getReadAccessor()} exposes — and its {@code HasAccessors()}/{@code isValNeeded()}
 * overrides were identical to the shared base. The write path lowers through the already-migrated
 * {@code CEntityAssignWithAccessor -> recursiveAssignWithAccessorEntity} binding to
 * {@code setReturnCode(<value>);} (asserted in {@code CFPacJavaAssignWithAccessorRetirementTest}).
 */
class CFPacJavaEnvironmentVariableRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String renderReference(CEntityEnvironmentVariable variable)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(variable, JavaTemplateRole.FPAC_REFERENCE).trim();
    }

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        assertEquals(CEntityEnvironmentVariable.class,
            new CJavaFPacEntityFactory(catalog, null)
                .NewEntityEnvironmentVariable("RETCD", "getReturnCode()",
                    "setReturnCode(", true).getClass());
    }

    /**
     * The factory-built entity renders its read accessor through the recursive assembler under
     * the independent FPac reference role (not a legacy {@code ExportReference}) as
     * {@code getReturnCode()} — byte-equivalent to the retired backend's {@code ExportReference}.
     */
    @Test
    void factoryBuiltReferenceRendersThroughFpacAssembler()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);

        CEntityEnvironmentVariable variable =
            factory.NewEntityEnvironmentVariable("RETCD", "getReturnCode()",
                "setReturnCode(", true);

        assertEquals("getReturnCode()", renderReference(variable));
    }

    /**
     * Mirrors production lowering end-to-end: the rules engine builds the environment variable
     * through {@code factory.NewEntityEnvironmentVariable(...)}, and a MOVE to it builds a
     * {@code CEntityAssignWithAccessor} whose reference is that factory-built entity. Fed through
     * the FPac factory and rendered under {@link JavaTemplateRole#FPAC_REFERENCE}, the read path
     * yields {@code getReturnCode()} and the write path {@code setReturnCode(<value>);}, proving
     * the environment variable the FPac parser emits is production-reachable and compilable
     * without any {@code generate.fpacjava} subclass.
     */
    @Test
    void parserFacingEnvironmentVariableLowersThroughFactoryAndAssembler()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);

        CEntityEnvironmentVariable variable =
            factory.NewEntityEnvironmentVariable("RETCD", "getReturnCode()",
                "setReturnCode(", true);
        assertEquals(CEntityEnvironmentVariable.class, variable.getClass());
        assertEquals("getReturnCode()", renderReference(variable));

        CEntityAssignWithAccessor assign = factory.NewEntityAssignWithAccessor(3);
        assign.SetRefTo(variable);
        assign.SetValue(new MockDataEntity(3, "WS-RETCD"));
        assertEquals("setReturnCode(WS-RETCD);", TemplateLoader.getRecursiveAssembler()
            .renderRoot(assign, JavaTemplateRole.FPAC_REFERENCE).trim());
    }
}
