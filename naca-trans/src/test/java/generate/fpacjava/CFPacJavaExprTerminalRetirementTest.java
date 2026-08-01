package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaFPacEntityFactory;
import generate.java.st.MockDataEntity;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.CEntityBloc;
import semantic.CEntityCondition;
import semantic.Verbs.CEntityBreak;
import semantic.expression.CEntityCondCompare;
import semantic.expression.CEntityExprTerminal;
import utils.CObjectCatalog;
import org.junit.jupiter.api.Test;

/**
 * Phase 2 (FPAC) — retirement proof for the former {@code CFPacJavaExprTerminal} direct
 * backend. FPac shares the target-neutral semantic expression model with COBOL, so
 * {@link CJavaFPacEntityFactory#NewEntityExprTerminal(semantic.CDataEntity)} must hand back a
 * pure {@link CEntityExprTerminal} (no {@code generate.fpacjava} subclass). The terminal is the
 * single-operand expression leaf the FPac parser builds (CFPacGenericExpression / CFPacMove /
 * CFPacArithmeticOperation / CFPacDoLoop / CFPacWTO / CFPacCall and the shared
 * CCondEqualsStatement / CTermExpression) and renders through the SHARED recursive ST4 binding
 * {@code semantic.expression.CEntityExprTerminal -> expressionTerminalEntity}
 * ({@code "<entity.term>"}) — the exact production path a lowered FPac terminal takes.
 *
 * <p>The deleted backend only carried a dead {@code Export()} override
 * ({@code renderReference(term, getLine())}) plus a {@code toString()}. That {@code Export()}
 * was unreachable on every FPac production path: a terminal is only ever rendered as an operand
 * child ({@code term}/{@code left}/{@code right}/{@code condition}) of a condition/expression
 * template, whose child role defaults to {@link JavaTemplateRole#REFERENCE}, so the assembler
 * dispatches on the target-neutral semantic class {@code CEntityExprTerminal} and walks the term
 * through the shared reference manifest — byte-identical output with or without the
 * {@code generate.fpacjava} subclass (the binding already resolved by superclass walk while the
 * subclass existed). The only reflective-{@code Export()} consumer,
 * {@code CFPacJavaIntrinsicFunction.ExportReference}, is unreachable from FPac because no FPac
 * parser node calls {@code NewEntityIntrinsicFunction}. This mirrors the {@code CEntityExprSum},
 * {@code CEntityAddress} and {@code CEntityCondIsBoolean} retirements (pure entity + shared
 * assembler, dead {@code Export()} removed). No FPac override is required in
 * {@code semantic-fpac-bindings.properties}: FPac's terminal lowering is the bare term reference,
 * identical in shape to COBOL's.
 */
class CFPacJavaExprTerminalRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String render(Object model)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(model, JavaTemplateRole.REFERENCE);
    }

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        assertEquals(CEntityExprTerminal.class,
            new CJavaFPacEntityFactory(catalog, null)
                .NewEntityExprTerminal(new MockDataEntity(1, "FIELD")).getClass());
    }

    /**
     * The terminal renders as its bare term through the shared {@code expressionTerminalEntity}
     * binding. The assembler walks the term under {@link JavaTemplateRole#REFERENCE} (the child
     * role for the {@code term} property), so the terminal's output equals the term's own
     * reference render — pinning the canonical convergence the retirement relies on (the same
     * mechanism the frozen COBOL {@code CEntityExprTerminalRenderTest} exercises).
     */
    @Test
    void assemblerRendersTheBareTermReference()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        MockDataEntity term = new MockDataEntity(1, "FIELD");

        assertEquals("FIELD", render(factory.NewEntityExprTerminal(term)));
        assertEquals(render(term), render(factory.NewEntityExprTerminal(term)),
            "a terminal must render exactly as its term through the shared reference walk");
    }

    /**
     * Live production lowering mirroring {@code parser/FPac/elements/CFPacDoLoop} (the DO loop's
     * {@code comp.SetLessThan(NewEntityExprTerminal(index), NewEntityExprTerminal(nbLoops))}) and
     * {@code parser/FPac/elements/CFPacCodeBloc}'s IF construction. The FPac factory builds the
     * ordered comparison and the enclosing {@code if}, the parser-side mutators wrap each operand
     * in a pure terminal, and the recursive ST4 assembler renders the whole {@code if} through the
     * shared {@code recursiveIfEntity} -> {@code recursiveCondCompareEntity} bindings. The then-block
     * holds a real {@code break;} so the assertion is deterministic; the nested terminal operands
     * render as their bare references — the operands' child role is REFERENCE, proving the terminals
     * unfold through the shared assembler reference walk, not a {@code generate.fpacjava} backend.
     */
    @Test
    void fpacIfComparisonLowersTerminalsThroughSharedAssembler()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityCondCompare compare = factory.NewEntityCondCompare();
        compare.SetLessThan(
            factory.NewEntityExprTerminal(new MockDataEntity(1, "INDEX")),
            factory.NewEntityExprTerminal(new MockDataEntity(1, "LIMIT")));
        CEntityBloc thenBloc = factory.NewEntityBloc(0);
        thenBloc.AddChild(new CEntityBreak(0, null));
        CEntityCondition condition = factory.NewEntityCondition(0);
        condition.SetCondition(compare, thenBloc, null);

        assertEquals("if (isLess(INDEX, LIMIT)) {\nbreak;\n}", render(condition));
    }
}
