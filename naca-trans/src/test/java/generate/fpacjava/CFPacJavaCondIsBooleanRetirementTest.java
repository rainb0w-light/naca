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
import semantic.expression.CEntityCondIsBoolean;
import utils.CObjectCatalog;

/**
 * Phase 2 (FPAC) — retirement proof for the former {@code CFPacJavaCondIsBoolean}
 * direct backend. FPac is the sole producer of
 * {@link semantic.expression.CEntityCondIsBoolean} (the COBOL factory throws), so
 * {@link CJavaFPacEntityFactory#NewEntityCondIsBoolean()} must hand back a pure
 * {@link CEntityCondIsBoolean} (no {@code generate.fpacjava} subclass). The parser
 * populates it via {@code setIsTrue}/{@code setIsFalse} exactly as
 * {@code parser/FPac/elements/CFPacCodeBloc} lowers the IF read-and-test-file
 * pattern, and rendering reaches the SHARED recursive ST4 assembler binding
 * ({@code semantic.expression.CEntityCondIsBoolean -> recursiveCondIsBooleanEntity})
 * — the exact production path a lowered FPac boolean condition takes. The deleted
 * backend only carried {@code GetPriorityLevel}/{@code GetOppositeCondition}/
 * {@code Export} overrides: the priority (7) and the opposite rebuild (copy the
 * reference, flip the flag) now live on the target-neutral semantic entity, and the
 * template emits the bare reference ("!"-prefixed when negated) — byte-identical to
 * the deleted {@code Export()}. No runtime operation is emitted beyond the reference
 * itself, which unfolds through the assembler's reference binding.
 */
class CFPacJavaCondIsBooleanRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static String render(CEntityCondIsBoolean condition)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(condition, JavaTemplateRole.REFERENCE);
    }

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        assertEquals(CEntityCondIsBoolean.class,
            new CJavaFPacEntityFactory(catalog, null).NewEntityCondIsBoolean().getClass());
    }

    @Test
    void rendersPositiveAndNegatedThroughRecursiveAssembler()
    {
        CEntityCondIsBoolean condition =
            new CJavaFPacEntityFactory(catalog, null).NewEntityCondIsBoolean();

        condition.setIsTrue(new MockDataEntity(1, "FLAG"));
        assertEquals("FLAG", render(condition));

        condition.setIsFalse(new MockDataEntity(1, "FLAG"));
        assertEquals("!FLAG", render(condition));
    }

    @Test
    void oppositeIsAnotherPureSemanticBooleanCondition()
    {
        CEntityCondIsBoolean condition =
            new CJavaFPacEntityFactory(catalog, null).NewEntityCondIsBoolean();
        condition.setIsTrue(new MockDataEntity(1, "FLAG"));

        CEntityCondIsBoolean opposite =
            assertInstanceOf(CEntityCondIsBoolean.class, condition.GetOppositeCondition());
        assertEquals("!FLAG", render(opposite));
        CEntityCondIsBoolean doubleOpposite =
            assertInstanceOf(CEntityCondIsBoolean.class, opposite.GetOppositeCondition());
        assertEquals("FLAG", render(doubleOpposite));
    }

    /**
     * End-to-end production lowering: the FPac factory builds the boolean condition
     * and the enclosing {@code if} exactly as {@code parser/FPac/elements/CFPacCodeBloc}
     * does for the IF read-and-test-file pattern (NewEntityCondition +
     * NewEntityCondIsBoolean + setIsTrue, then a then-block), the parser-side mutators
     * populate the reference and the true/false flag, and the recursive ST4 assembler
     * renders the whole {@code if} through the shared {@code recursiveIfEntity}
     * binding. The then-block holds a real {@code break;} action so the assertion is
     * deterministic and independent of ST4's empty-line suppression — the nested
     * {@code recursiveCondIsBooleanEntity} render is exactly the legacy backend's
     * bare-reference shape.
     */
    @Test
    void fpacIfLowersBooleanThroughRecursiveAssembler()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityCondIsBoolean bool = factory.NewEntityCondIsBoolean();
        bool.setIsTrue(new MockDataEntity(1, "FLAG"));
        CEntityBloc thenBloc = factory.NewEntityBloc(0);
        thenBloc.AddChild(new CEntityBreak(0, null));
        CEntityCondition condition = factory.NewEntityCondition(0);
        condition.SetCondition(bool, thenBloc, null);

        assertEquals("if (FLAG) {\nbreak;\n}",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(condition, JavaTemplateRole.REFERENCE));
    }
}
