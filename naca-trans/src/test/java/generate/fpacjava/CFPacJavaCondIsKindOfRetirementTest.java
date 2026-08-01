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
import semantic.expression.CEntityCondIsKindOf;
import utils.CObjectCatalog;

/**
 * Phase 2 (FPAC) — retirement proof for the former {@code CFPacJavaCondIsKindOf}
 * direct backend. FPac shares the target-neutral
 * {@link semantic.expression.CEntityCondIsKindOf} with COBOL, so
 * {@link CJavaFPacEntityFactory#NewEntityCondIsKindOf()} must hand back a pure
 * {@link CEntityCondIsKindOf} (no {@code generate.fpacjava} subclass). The FPac
 * parser populates it via {@code SetIsNumeric} exactly as
 * {@code parser/FPac/CFPacGenericExpression.AnalyseSingleOperand} lowers the
 * {@code IF X NUMERIC} class test (and the shared {@code parser/condition/CCondIsNumeric}
 * adds {@code setOpposite} for the negated form), and rendering reaches the SHARED
 * recursive ST4 assembler binding ({@code semantic.expression.CEntityCondIsKindOf ->
 * recursiveCondIsKindOfEntity}) — the exact production path a lowered FPac class
 * condition takes. The deleted backend only carried
 * {@code GetPriorityLevel}/{@code GetOppositeCondition}/{@code Export} overrides: the
 * priority (7) and the opposite rebuild (copy the reference, flip the flag) now live on
 * the target-neutral semantic entity, and the template emits
 * {@code is[Not](Numeric|Alphabetic|AlphabeticLower|AlphabeticUpper)(<reference>)} —
 * byte-identical to the deleted {@code Export()} for the numeric/alphabetic kinds FPac
 * actually lowers. (For the lower/upper kinds — which the FPac parser never produces —
 * the deleted backend emitted a bare {@code is(<reference>)}, invalid Java; the shared
 * canonical template emits the compilable {@code isAlphabeticLower}/{@code isAlphabeticUpper}
 * calls COBOL already uses for this same entity.) The
 * {@code is[Not]Numeric}/{@code is[Not]Alphabetic[Lower|Upper]} predicates are
 * {@code nacaLib.basePrgEnv.BaseProgram} runtime calls that {@code FPacProgram}
 * inherits, so no FPac-specific runtime operation is introduced.
 */
class CFPacJavaCondIsKindOfRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String render(CEntityCondIsKindOf condition)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(condition, JavaTemplateRole.REFERENCE);
    }

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        assertEquals(CEntityCondIsKindOf.class,
            new CJavaFPacEntityFactory(catalog, null).NewEntityCondIsKindOf().getClass());
    }

    @Test
    void rendersEveryClassTestThroughRecursiveAssembler()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);

        CEntityCondIsKindOf numeric = factory.NewEntityCondIsKindOf();
        numeric.SetIsNumeric(new MockDataEntity(1, "FIELD"));
        assertEquals("isNumeric(FIELD)", render(numeric));

        CEntityCondIsKindOf alphabetic = factory.NewEntityCondIsKindOf();
        alphabetic.SetIsAlphabetic(new MockDataEntity(1, "FIELD"));
        assertEquals("isAlphabetic(FIELD)", render(alphabetic));

        CEntityCondIsKindOf lower = factory.NewEntityCondIsKindOf();
        lower.SetIsLower(new MockDataEntity(1, "FIELD"));
        assertEquals("isAlphabeticLower(FIELD)", render(lower));

        CEntityCondIsKindOf upper = factory.NewEntityCondIsKindOf();
        upper.SetIsUpper(new MockDataEntity(1, "FIELD"));
        assertEquals("isAlphabeticUpper(FIELD)", render(upper));
    }

    @Test
    void rendersNegatedThroughRecursiveAssembler()
    {
        CEntityCondIsKindOf condition =
            new CJavaFPacEntityFactory(catalog, null).NewEntityCondIsKindOf();
        condition.SetIsNumeric(new MockDataEntity(1, "FIELD"));
        condition.setOpposite();
        assertEquals("isNotNumeric(FIELD)", render(condition));
    }

    @Test
    void oppositeIsAnotherPureSemanticClassCondition()
    {
        CEntityCondIsKindOf condition =
            new CJavaFPacEntityFactory(catalog, null).NewEntityCondIsKindOf();
        condition.SetIsNumeric(new MockDataEntity(1, "FIELD"));

        CEntityCondIsKindOf opposite =
            assertInstanceOf(CEntityCondIsKindOf.class, condition.GetOppositeCondition());
        assertEquals("isNotNumeric(FIELD)", render(opposite));
        CEntityCondIsKindOf doubleOpposite =
            assertInstanceOf(CEntityCondIsKindOf.class, opposite.GetOppositeCondition());
        assertEquals("isNumeric(FIELD)", render(doubleOpposite));
    }

    /**
     * End-to-end production lowering: the FPac factory builds the class condition and the
     * enclosing {@code if} exactly as
     * {@code parser/FPac/CFPacGenericExpression.AnalyseSingleOperand} does for
     * {@code IF X NUMERIC} (NewEntityCondIsKindOf + SetIsNumeric, wrapped by
     * NewEntityCondition with a then-block), the parser-side mutators populate the
     * reference and the kind flag, and the recursive ST4 assembler renders the whole
     * {@code if} through the shared {@code recursiveIfEntity} binding. The then-block
     * holds a real {@code break;} action so the assertion is deterministic and
     * independent of ST4's empty-line suppression — the nested
     * {@code recursiveCondIsKindOfEntity} render is exactly the legacy backend's
     * {@code isNumeric(<reference>)} shape.
     */
    @Test
    void fpacIfLowersNumericClassConditionThroughRecursiveAssembler()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityCondIsKindOf isNumeric = factory.NewEntityCondIsKindOf();
        isNumeric.SetIsNumeric(new MockDataEntity(1, "FIELD"));
        CEntityBloc thenBloc = factory.NewEntityBloc(0);
        thenBloc.AddChild(new CEntityBreak(0, null));
        CEntityCondition condition = factory.NewEntityCondition(0);
        condition.SetCondition(isNumeric, thenBloc, null);

        assertEquals("if (isNumeric(FIELD)) {\nbreak;\n}",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(condition, JavaTemplateRole.REFERENCE));
    }
}
