package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaFPacEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityBloc;
import semantic.CEntityCondition;
import semantic.SQL.CEntityCondIsSQLCode;
import semantic.Verbs.CEntityBreak;
import semantic.expression.CEntityCondAnd;
import utils.CObjectCatalog;

/**
 * Phase 2 (FPAC) — retirement proof for the former {@code CFPacJavaCondAnd} direct
 * backend. The FPac pipeline shares the target-neutral semantic expression model, so
 * {@link CJavaFPacEntityFactory#NewEntityCondAnd()} must hand back a pure
 * {@link CEntityCondAnd} (no {@code generate.fpacjava} subclass). Its operands and
 * grouping are precomputed by the parser, and rendering reaches the SHARED recursive
 * ST4 assembler binding ({@code semantic.expression.CEntityCondAnd ->
 * recursiveCondAndEntity}) — the exact production path a lowered FPac AND condition
 * takes. The deleted backend only carried an {@code Export()} override emitting the
 * same priority-1 {@code "left \n&& right"} shape the shared template now produces.
 */
class CFPacJavaCondAndRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        assertEquals(CEntityCondAnd.class,
            new CJavaFPacEntityFactory(catalog, null).NewEntityCondAnd().getClass());
    }

    @Test
    void rendersAndConditionThroughRecursiveAssembler()
    {
        CEntityCondAnd condition = new CJavaFPacEntityFactory(catalog, null).NewEntityCondAnd();
        condition.SetCondition(sqlCode(0), sqlCode(42));

        assertEquals("isSQLCode(SQLCode.SQL_OK) \n&& isSQLCode(42)",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(condition, JavaTemplateRole.REFERENCE));
    }

    /**
     * End-to-end production lowering: the FPac factory builds the AND condition and the
     * enclosing {@code if}, the parser populates the operands, and the recursive ST4
     * assembler renders the whole {@code if} through the shared {@code recursiveIfEntity}
     * binding. The then-block holds a real {@code break;} action so the assertion is
     * deterministic and independent of ST4's empty-line suppression — the nested
     * {@code recursiveCondAndEntity} render is exactly the legacy backend's priority-1
     * {@code "left \n&& right"} shape.
     */
    @Test
    void fpacIfLowersConditionThroughRecursiveAssembler()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityCondAnd and = factory.NewEntityCondAnd();
        and.SetCondition(sqlCode(0), sqlCode(42));
        CEntityBloc thenBloc = factory.NewEntityBloc(0);
        thenBloc.AddChild(new CEntityBreak(0, null));
        CEntityCondition condition = factory.NewEntityCondition(0);
        condition.SetCondition(and, thenBloc, null);

        assertEquals("if (isSQLCode(SQLCode.SQL_OK) \n&& isSQLCode(42)) {\nbreak;\n}",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(condition, JavaTemplateRole.REFERENCE));
    }

    private static CEntityCondIsSQLCode sqlCode(int value)
    {
        CEntityCondIsSQLCode condition = new CEntityCondIsSQLCode();
        condition.setIsEqual(value);
        return condition;
    }
}
