package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaFPacEntityFactory;
import generate.java.st.MockDataEntity;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.Verbs.CEntityAssignSpecial;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/**
 * Phase 2 (FPAC) — retirement proof for the former {@code CFPacJavaAssignSpecial}
 * direct backend. {@link CJavaFPacEntityFactory#NewEntityAssignSpecial(int)} must hand
 * back the pure, now target-neutral {@link CEntityAssignSpecial} semantic verb (no
 * {@code generate.fpacjava} subclass). Source, destination and arithmeticAssign are
 * precomputed by the parser (the packed branch of {@code CFPacMove.DoCustomSemanticAnalysis}
 * calls {@code setDestination} + {@code setSource} + {@code setArithmeticAssign(true)}),
 * and rendering reaches the recursive ST4 assembler through the declarative
 * {@code semantic.Verbs.CEntityAssignSpecial -> recursiveMovePackedEntity} binding — the
 * exact production path a lowered FPac packed move takes.
 *
 * <p>The deleted backend's {@code DoExport()} emitted {@code movePacked(<source>, <destination>) ;}
 * (a stray space before the semicolon) and only when {@code arithmeticAssign} was set; FPac
 * converges onto the canonical {@code movePacked(<source>, <destination>);} shape. The packed
 * branch is the sole populator of this verb and always sets {@code arithmeticAssign}, so the
 * binding always lowers to the one runtime call. Runtime-legal:
 * {@code nacaLib.fpacPrgEnv.FPacProgram} declares {@code movePacked(Var, Var)} (contract
 * operation {@code data.movePacked}), delegating to
 * {@code nacaLib.basePrgEnv.BaseProgram.move(Var, Var)}.
 */
class CFPacJavaAssignSpecialRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String render(CEntityAssignSpecial assign)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(assign, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        assertEquals(CEntityAssignSpecial.class,
            new CJavaFPacEntityFactory(catalog, null).NewEntityAssignSpecial(1).getClass());
    }

    /**
     * The factory-built entity renders through the recursive assembler (not a legacy
     * {@code DoExport}) as {@code movePacked(<source>, <destination>);} — the canonical
     * shape the retired backend approximated with a stray {@code ") ;"} spacing.
     */
    @Test
    void productionBridgeRendersMovePackedThroughAssembler()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);

        CEntityAssignSpecial assign = factory.NewEntityAssignSpecial(1);
        assign.setSource(new MockDataEntity(2, "SOURCE"));
        assign.setDestination(new MockDataEntity(3, "DEST"));
        assign.setArithmeticAssign(true);
        assertEquals("movePacked(SOURCE, DEST);", render(assign));
    }

    /**
     * Mirrors production lowering end-to-end from the parser-facing population sequence:
     * the packed branch of {@code CFPacMove.DoCustomSemanticAnalysis} builds the verb with
     * {@code factory.NewEntityAssignSpecial(line)} then {@code setDestination(var2)} +
     * {@code setSource(var1)} + {@code setArithmeticAssign(true)}. Fed here with the same
     * calls against the FPac factory, the pure {@link CEntityAssignSpecial} lowers through
     * the recursive ST4 assembler as {@code movePacked(<source>, <destination>);}, proving
     * the packed move the FPac parser emits is production-reachable and compilable.
     */
    @Test
    void parserFacingAssignSpecialLowersThroughFactoryAndAssembler()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);

        CEntityAssignSpecial ass = factory.NewEntityAssignSpecial(7);
        ass.setDestination(new MockDataEntity(7, "WS-TARGET"));
        ass.setSource(new MockDataEntity(7, "WS-SOURCE"));
        ass.setArithmeticAssign(true);

        assertEquals(CEntityAssignSpecial.class, ass.getClass());
        assertEquals("movePacked(WS-SOURCE, WS-TARGET);", render(ass));
    }
}
