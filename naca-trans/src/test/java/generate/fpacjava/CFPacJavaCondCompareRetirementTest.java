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
import semantic.expression.CEntityCondCompare;
import semantic.expression.CEntityExprTerminal;
import utils.CObjectCatalog;

/**
 * Phase 2 (FPAC) — retirement proof for the former {@code CFPacJavaCondCompare} direct
 * backend. The FPac pipeline shares the target-neutral semantic expression model, so
 * {@link CJavaFPacEntityFactory#NewEntityCondCompare()} must hand back a pure
 * {@link CEntityCondCompare} (no {@code generate.fpacjava} subclass). Its operands
 * ({@code op1}/{@code op2}) and the {@code isisGreater}/{@code isisOrEquals} flags are
 * precomputed by the parser via the {@code SetGreaterThan}/{@code SetLessThan}/...
 * mutators, and rendering reaches the SHARED recursive ST4 assembler binding
 * ({@code semantic.expression.CEntityCondCompare -> recursiveCondCompareEntity}) — the
 * exact production path a lowered FPac ordered comparison takes. No FPac override is
 * needed in {@code semantic-fpac-bindings.properties} because FPac's ordered-comparison
 * lowering is identical in shape to COBOL's. The deleted backend only carried an
 * {@code Export()} override emitting the same {@code isGreater[OrEqual](left, right)} /
 * {@code isLess[OrEqual](left, right)} calls (runtime predicates on
 * {@code nacaLib.basePrgEnv.BaseProgram}, which {@code nacaLib.fpacPrgEnv.FPacProgram}
 * extends) that the shared template now produces.
 */
class CFPacJavaCondCompareRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static CEntityExprTerminal expression(String value)
    {
        return new CEntityExprTerminal(new MockDataEntity(1, value));
    }

    private static String render(CEntityCondCompare condition)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(condition, JavaTemplateRole.REFERENCE);
    }

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        assertEquals(CEntityCondCompare.class,
            new CJavaFPacEntityFactory(catalog, null).NewEntityCondCompare().getClass());
    }

    @Test
    void rendersAllOrderedComparisonsThroughRecursiveAssembler()
    {
        CEntityCondCompare condition =
            new CJavaFPacEntityFactory(catalog, null).NewEntityCondCompare();

        condition.SetLessThan(expression("LEFT"), expression("RIGHT"));
        assertEquals("isLess(LEFT, RIGHT)", render(condition));

        condition.SetLessOrEqualThan(expression("LEFT"), expression("RIGHT"));
        assertEquals("isLessOrEqual(LEFT, RIGHT)", render(condition));

        condition.SetGreaterThan(expression("LEFT"), expression("RIGHT"));
        assertEquals("isGreater(LEFT, RIGHT)", render(condition));

        condition.SetGreaterOrEqualsThan(expression("LEFT"), expression("RIGHT"));
        assertEquals("isGreaterOrEqual(LEFT, RIGHT)", render(condition));
    }

    @Test
    void oppositeIsAnotherPureSemanticComparison()
    {
        CEntityCondCompare condition =
            new CJavaFPacEntityFactory(catalog, null).NewEntityCondCompare();
        condition.SetLessThan(expression("LEFT"), expression("RIGHT"));

        CEntityCondCompare opposite = assertInstanceOf(
            CEntityCondCompare.class, condition.GetOppositeCondition());
        assertEquals("isGreaterOrEqual(LEFT, RIGHT)", render(opposite));
    }

    /**
     * End-to-end production lowering: the FPac factory builds the ordered comparison and
     * the enclosing {@code if} (exactly as {@code parser/FPac/CFPacGenericExpression}
     * does when it lowers a {@code >} operand pair), the parser-side mutators populate the
     * operands, and the recursive ST4 assembler renders the whole {@code if} through the
     * shared {@code recursiveIfEntity} binding. The then-block holds a real {@code break;}
     * action so the assertion is deterministic and independent of ST4's empty-line
     * suppression — the nested {@code recursiveCondCompareEntity} render is exactly the
     * legacy backend's {@code isGreater(left, right)} shape.
     */
    @Test
    void fpacIfLowersComparisonThroughRecursiveAssembler()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityCondCompare compare = factory.NewEntityCondCompare();
        compare.SetGreaterThan(expression("LEFT"), expression("RIGHT"));
        CEntityBloc thenBloc = factory.NewEntityBloc(0);
        thenBloc.AddChild(new CEntityBreak(0, null));
        CEntityCondition condition = factory.NewEntityCondition(0);
        condition.SetCondition(compare, thenBloc, null);

        assertEquals("if (isGreater(LEFT, RIGHT)) {\nbreak;\n}",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(condition, JavaTemplateRole.REFERENCE));
    }
}
