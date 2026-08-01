package generate.fpacjava;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaFPacEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import parser.condition.CCondOrStatement;
import parser.expression.CDefaultConditionManager;
import parser.expression.CExpression;
import semantic.CBaseEntityFactory;
import semantic.CEntityBloc;
import semantic.CEntityCondition;
import semantic.SQL.CEntityCondIsSQLCode;
import semantic.Verbs.CEntityBreak;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityCondOr;
import utils.CObjectCatalog;

/**
 * Phase 2 (FPAC) — retirement proof for the former {@code CFPacJavaCondOr} direct
 * backend. The FPac pipeline shares the target-neutral semantic expression model, so
 * {@link CJavaFPacEntityFactory#NewEntityCondOr()} must hand back a pure
 * {@link CEntityCondOr} (no {@code generate.fpacjava} subclass). Its operands and
 * grouping are precomputed by the parser, and rendering reaches the SHARED recursive
 * ST4 assembler binding ({@code semantic.expression.CEntityCondOr ->
 * recursiveCondOrEntity}) — the exact production path a lowered FPac OR condition
 * takes. The deleted backend only carried an {@code Export()} override emitting the
 * same priority-2 {@code "left \n|| right"} shape the shared template now produces
 * (its parenthesization via {@code CJavaExporter.ExportChildCondition(2, op)} is the
 * same rule the entity's {@code isLeft/RightGrouped} getters precompute).
 */
class CFPacJavaCondOrRetirementTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void fpacFactoryReturnsPureSemanticEntity()
    {
        assertEquals(CEntityCondOr.class,
            new CJavaFPacEntityFactory(catalog, null).NewEntityCondOr().getClass());
    }

    @Test
    void rendersOrConditionThroughRecursiveAssembler()
    {
        CEntityCondOr condition = new CJavaFPacEntityFactory(catalog, null).NewEntityCondOr();
        condition.SetCondition(sqlCode(0), sqlCode(42));

        assertEquals("isSQLCode(SQLCode.SQL_OK) \n|| isSQLCode(42)",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(condition, JavaTemplateRole.REFERENCE));
    }

    /**
     * Production parser path: the FPac pipeline lowers {@code "cond1 OR cond2"} through
     * {@code parser/FPac/CFPacElement} -> {@link CCondOrStatement#AnalyseCondition}, which
     * calls {@code factory.NewEntityCondOr()} + {@code SetCondition(op1, op2)}. This fixture
     * drives that exact shared parser node with the FPac factory and asserts the lowered
     * entity is the pure semantic {@link CEntityCondOr} (not a {@code generate.fpacjava}
     * subclass) and that the recursive ST4 assembler renders it to the legacy backend's
     * {@code "left \n|| right"} output.
     */
    @Test
    void parserOrStatementLowersThroughFpacFactoryToRecursiveAssembler()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CCondOrStatement orStatement = new CCondOrStatement(
            0, fixedCondition(sqlCode(0)), fixedCondition(sqlCode(42)));

        CBaseEntityCondition lowered = orStatement.AnalyseCondition(
            factory, new CDefaultConditionManager(orStatement));

        assertEquals(CEntityCondOr.class, lowered.getClass());
        assertEquals("isSQLCode(SQLCode.SQL_OK) \n|| isSQLCode(42)",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(lowered, JavaTemplateRole.REFERENCE));
    }

    /**
     * End-to-end production lowering: the FPac factory builds the OR condition and the
     * enclosing {@code if}, the parser populates the operands, and the recursive ST4
     * assembler renders the whole {@code if} through the shared {@code recursiveIfEntity}
     * binding. The then-block holds a real {@code break;} action so the assertion is
     * deterministic and independent of ST4's empty-line suppression — the nested
     * {@code recursiveCondOrEntity} render is exactly the legacy backend's priority-2
     * {@code "left \n|| right"} shape.
     */
    @Test
    void fpacIfLowersConditionThroughRecursiveAssembler()
    {
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(catalog, null);
        CEntityCondOr or = factory.NewEntityCondOr();
        or.SetCondition(sqlCode(0), sqlCode(42));
        CEntityBloc thenBloc = factory.NewEntityBloc(0);
        thenBloc.AddChild(new CEntityBreak(0, null));
        CEntityCondition condition = factory.NewEntityCondition(0);
        condition.SetCondition(or, thenBloc, null);

        assertEquals("if (isSQLCode(SQLCode.SQL_OK) \n|| isSQLCode(42)) {\nbreak;\n}",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(condition, JavaTemplateRole.REFERENCE));
    }

    private static CEntityCondIsSQLCode sqlCode(int value)
    {
        CEntityCondIsSQLCode condition = new CEntityCondIsSQLCode();
        condition.setIsEqual(value);
        return condition;
    }

    private static CExpression fixedCondition(CBaseEntityCondition condition)
    {
        return new FixedConditionExpression(condition);
    }

    /** Minimal parser operand whose condition analysis yields a fixed semantic condition. */
    private static final class FixedConditionExpression extends CExpression
    {
        private final CBaseEntityCondition condition;

        FixedConditionExpression(CBaseEntityCondition condition)
        {
            super(0);
            this.condition = condition;
        }

        @Override
        public CBaseEntityExpression AnalyseExpression(CBaseEntityFactory factory)
        {
            return null;
        }

        @Override
        public CBaseEntityCondition AnalyseCondition(CBaseEntityFactory factory,
                        CDefaultConditionManager masterCond)
        {
            return condition;
        }

        @Override
        public Element DoExport(Document root)
        {
            return null;
        }

        @Override
        protected boolean CheckMembersBeforeExport()
        {
            return true;
        }

        @Override
        public CExpression GetFirstConditionOperand()
        {
            return this;
        }

        @Override
        public CExpression GetSimilarExpression(CExpression operand)
        {
            return null;
        }

        @Override
        public boolean IsBinaryCondition()
        {
            return false;
        }

        @Override
        public CExpression GetFirstCalculOperand()
        {
            return this;
        }
    }
}
