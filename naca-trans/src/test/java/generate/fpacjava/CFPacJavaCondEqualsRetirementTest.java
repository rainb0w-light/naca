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
import semantic.expression.CEntityCondEquals;
import semantic.expression.CEntityExprTerminal;
import utils.CObjectCatalog;

/**
 * Phase 2 (FPAC) — retirement proof for the former {@code CFPacJavaCondEquals} direct
 * backend. The FPac pipeline shares the target-neutral semantic expression model, so
 * {@link CJavaFPacEntityFactory#NewEntityCondEquals()} must hand back a pure
 * {@link CEntityCondEquals} (no {@code generate.fpacjava} subclass). Its operands
 * ({@code op1}/{@code op2}) and the {@code bIsDifferent} flag are precomputed by the
 * parser via {@code SetEqualCondition}/{@code SetDifferentCondition} (exactly as
 * {@code parser/FPac/CFPacGenericExpression} lowers the {@code EQ}/{@code NE} keywords),
 * and rendering reaches the SHARED recursive ST4 assembler binding
 * ({@code semantic.expression.CEntityCondEquals -> recursiveCondEqualsEntity}) — the
 * exact production path a lowered FPac equality comparison takes. No FPac override is
 * needed in {@code semantic-fpac-bindings.properties} because FPac's equality lowering
 * is identical in shape to COBOL's. The deleted backend only carried an {@code Export()}
 * override emitting the same {@code isEqual(left, right)} / {@code isDifferent(left,
 * right)} calls (runtime predicates on {@code nacaLib.basePrgEnv.BaseProgram}, which
 * {@code nacaLib.fpacPrgEnv.FPacProgram} extends, with the same {@code [UNDEFINED]}
 * fallback for a missing right operand) that the shared template now produces.
 */
class CFPacJavaCondEqualsRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static CEntityExprTerminal expression(String value)
    {
        return new CEntityExprTerminal(new MockDataEntity(1, value));
    }

    private static String render(CEntityCondEquals condition)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(condition, JavaTemplateRole.REFERENCE);
    }

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        assertEquals(CEntityCondEquals.class,
            new CJavaFPacEntityFactory(catalog, null).NewEntityCondEquals().getClass());
    }

    @Test
    void rendersEqualityAndDifferenceThroughRecursiveAssembler()
    {
        CEntityCondEquals condition =
            new CJavaFPacEntityFactory(catalog, null).NewEntityCondEquals();

        condition.SetEqualCondition(expression("LEFT"), expression("RIGHT"));
        assertEquals("isEqual(LEFT, RIGHT)", render(condition));

        condition.SetDifferentCondition(expression("LEFT"), expression("RIGHT"));
        assertEquals("isDifferent(LEFT, RIGHT)", render(condition));
    }

    @Test
    void oppositeIsAnotherPureSemanticEqualityCondition()
    {
        CEntityCondEquals condition =
            new CJavaFPacEntityFactory(catalog, null).NewEntityCondEquals();
        condition.SetEqualCondition(expression("LEFT"), expression("RIGHT"));

        CEntityCondEquals opposite = assertInstanceOf(
            CEntityCondEquals.class, condition.GetOppositeCondition());
        assertEquals("isDifferent(LEFT, RIGHT)", render(opposite));
    }

    /**
     * End-to-end production lowering: the FPac factory builds the equality condition and
     * the enclosing {@code if} (exactly as {@code parser/FPac/CFPacGenericExpression}
     * does when it lowers an {@code EQ} operand pair), the parser-side mutators populate
     * the operands, and the recursive ST4 assembler renders the whole {@code if} through
     * the shared {@code recursiveIfEntity} binding. The then-block holds a real
     * {@code break;} action so the assertion is deterministic and independent of ST4's
     * empty-line suppression — the nested {@code recursiveCondEqualsEntity} render is
     * exactly the legacy backend's {@code isEqual(left, right)} shape.
     */
    @Test
    void fpacIfLowersEqualityThroughRecursiveAssembler()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityCondEquals equals = factory.NewEntityCondEquals();
        equals.SetEqualCondition(expression("LEFT"), expression("RIGHT"));
        CEntityBloc thenBloc = factory.NewEntityBloc(0);
        thenBloc.AddChild(new CEntityBreak(0, null));
        CEntityCondition condition = factory.NewEntityCondition(0);
        condition.SetCondition(equals, thenBloc, null);

        assertEquals("if (isEqual(LEFT, RIGHT)) {\nbreak;\n}",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(condition, JavaTemplateRole.REFERENCE));
    }
}
