package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaFPacEntityFactory;
import generate.java.st.MockDataEntity;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityBloc;
import semantic.CEntityCondition;
import semantic.Verbs.CEntityBreak;
import semantic.expression.CEntityCondIsConstant;
import utils.CObjectCatalog;

/**
 * Phase 2 (FPAC) — retirement proof for the former {@code CFPacJavaCondIsConstant}
 * direct backend. FPac shares the target-neutral
 * {@link semantic.expression.CEntityCondIsConstant} with COBOL, so
 * {@link CJavaFPacEntityFactory#NewEntityCondIsConstant()} must hand back a pure
 * {@link CEntityCondIsConstant} (no {@code generate.fpacjava} subclass). The parser
 * populates it via {@code SetIsZero}/{@code SetIsSpace}/{@code SetIsLowValue}/
 * {@code SetIsHighValue} (and {@code SetOpposite} for the negated form) exactly as
 * {@code parser/FPac/CFPacGenericExpression} lowers the {@code IF X = SPACE / LOW-VALUE /
 * HIGH-VALUE / ZERO} figurative-constant test, and rendering reaches the SHARED recursive
 * ST4 assembler binding ({@code semantic.expression.CEntityCondIsConstant ->
 * recursiveCondIsConstantEntity}) — the exact production path a lowered FPac
 * figurative-constant condition takes. The deleted backend only carried
 * {@code GetPriorityLevel}/{@code GetOppositeCondition}/{@code Export} overrides: the
 * priority (7) and the opposite rebuild (copy the reference, flip the flag) now live on
 * the target-neutral semantic entity, and the template emits
 * {@code is[Not](Zero|Space|LowValue|HighValue)(<reference>)} — byte-identical to the
 * deleted {@code Export()}. The {@code is[Not](Zero|Space|LowValue|HighValue)} predicates
 * are {@code nacaLib.basePrgEnv.BaseProgram} runtime calls that {@code FPacProgram}
 * inherits, so no FPac-specific runtime operation is introduced.
 */
class CFPacJavaCondIsConstantRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String render(CEntityCondIsConstant condition)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(condition, JavaTemplateRole.REFERENCE);
    }

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        assertEquals(CEntityCondIsConstant.class,
            new CJavaFPacEntityFactory(catalog, null).NewEntityCondIsConstant().getClass());
    }

    @Test
    void rendersEveryFigurativeConstantThroughRecursiveAssembler()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);

        CEntityCondIsConstant zero = factory.NewEntityCondIsConstant();
        zero.SetIsZero(new MockDataEntity(1, "FIELD"));
        assertEquals("isZero(FIELD)", render(zero));

        CEntityCondIsConstant space = factory.NewEntityCondIsConstant();
        space.SetIsSpace(new MockDataEntity(1, "FIELD"));
        assertEquals("isSpace(FIELD)", render(space));

        CEntityCondIsConstant lowValue = factory.NewEntityCondIsConstant();
        lowValue.SetIsLowValue(new MockDataEntity(1, "FIELD"));
        assertEquals("isLowValue(FIELD)", render(lowValue));

        CEntityCondIsConstant highValue = factory.NewEntityCondIsConstant();
        highValue.SetIsHighValue(new MockDataEntity(1, "FIELD"));
        assertEquals("isHighValue(FIELD)", render(highValue));
    }

    @Test
    void rendersNegatedThroughRecursiveAssembler()
    {
        CEntityCondIsConstant condition =
            new CJavaFPacEntityFactory(catalog, null).NewEntityCondIsConstant();
        condition.SetIsSpace(new MockDataEntity(1, "FIELD"));
        condition.SetOpposite();
        assertEquals("isNotSpace(FIELD)", render(condition));
    }

    @Test
    void oppositeIsAnotherPureSemanticConstantCondition()
    {
        CEntityCondIsConstant condition =
            new CJavaFPacEntityFactory(catalog, null).NewEntityCondIsConstant();
        condition.SetIsZero(new MockDataEntity(1, "FIELD"));

        CEntityCondIsConstant opposite =
            assertInstanceOf(CEntityCondIsConstant.class, condition.GetOppositeCondition());
        assertEquals("isNotZero(FIELD)", render(opposite));
        CEntityCondIsConstant doubleOpposite =
            assertInstanceOf(CEntityCondIsConstant.class, opposite.GetOppositeCondition());
        assertEquals("isZero(FIELD)", render(doubleOpposite));
    }

    /**
     * End-to-end production lowering: the FPac factory builds the figurative-constant
     * condition and the enclosing {@code if} exactly as
     * {@code parser/FPac/CFPacGenericExpression} does for {@code IF X = SPACE}
     * (NewEntityCondIsConstant + SetIsSpace, wrapped by NewEntityCondition with a
     * then-block), the parser-side mutators populate the reference and the keyword flag,
     * and the recursive ST4 assembler renders the whole {@code if} through the shared
     * {@code recursiveIfEntity} binding. The then-block holds a real {@code break;} action
     * so the assertion is deterministic and independent of ST4's empty-line suppression —
     * the nested {@code recursiveCondIsConstantEntity} render is exactly the legacy
     * backend's {@code isSpace(<reference>)} shape.
     */
    @Test
    void fpacIfLowersConstantConditionThroughRecursiveAssembler()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityCondIsConstant isSpace = factory.NewEntityCondIsConstant();
        isSpace.SetIsSpace(new MockDataEntity(1, "FIELD"));
        CEntityBloc thenBloc = factory.NewEntityBloc(0);
        thenBloc.AddChild(new CEntityBreak(0, null));
        CEntityCondition condition = factory.NewEntityCondition(0);
        condition.SetCondition(isSpace, thenBloc, null);

        assertEquals("if (isSpace(FIELD)) {\nbreak;\n}",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(condition, JavaTemplateRole.REFERENCE));
    }
}
