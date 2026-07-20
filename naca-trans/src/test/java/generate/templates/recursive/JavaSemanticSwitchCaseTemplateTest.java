package generate.templates.recursive;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.java.expressions.CJavaCondEquals;
import generate.java.expressions.CJavaEntityNumber;
import generate.java.expressions.CJavaExprTerminal;
import generate.java.st.MockJavaExporter;
import semantic.Verbs.CEntityBreak;
import semantic.Verbs.CEntityCase;
import semantic.Verbs.CEntityContinue;
import semantic.Verbs.CEntitySwitchCase;
import generate.templates.TemplateLoader;
import org.junit.jupiter.api.Test;
import semantic.CIgnoredEntity;

class JavaSemanticSwitchCaseTemplateTest
{
    private final JavaTemplateAssembler assembler = TemplateLoader.newRecursiveAssembler();

    @Test
    void recursivelyRendersWhenAndWhenOtherLikeTheDirectGenerator()
    {
        MockJavaExporter output = new MockJavaExporter();
        TestJavaSwitchCase evaluate = new TestJavaSwitchCase(output);
        evaluate.AddChild(branch(output, equals("1", "1"), new CEntityContinue(0, null)));
        evaluate.AddChild(branch(output, equals("2", "2"), new CEntityBreak(0, null)));
        evaluate.AddChild(branch(output, null, new CEntityContinue(0, null)));

        assertEquals("if (isEqual(1, 1))\n{\n// CONTINUE \n}\nelse if (isEqual(2, 2))\n{\nbreak;\n}\nelse \n{\n// CONTINUE \n}",
            assembler.renderRoot(evaluate).stripTrailing());
    }

    @Test
    void skipsIgnoredWhenBranchesWithoutBreakingElseChaining()
    {
        MockJavaExporter output = new MockJavaExporter();
        TestJavaSwitchCase evaluate = new TestJavaSwitchCase(output);
        CJavaCondEquals ignored = new CJavaCondEquals();
        ignored.SetEqualCondition(
            new CJavaExprTerminal(new CIgnoredEntity(0, "", null)),
            new CJavaExprTerminal(new CJavaEntityNumber(null, null, "0")));
        evaluate.AddChild(branch(output, ignored, new CEntityBreak(0, null)));
        evaluate.AddChild(branch(output, equals("1", "1"), new CEntityContinue(0, null)));
        evaluate.AddChild(branch(output, null, new CEntityBreak(0, null)));

        assertEquals("if (isEqual(1, 1))\n{\n// CONTINUE \n}\nelse \n{\nbreak;\n}",
            assembler.renderRoot(evaluate).stripTrailing());
    }

    private CEntityCase branch(
        MockJavaExporter output,
        CJavaCondEquals condition,
        semantic.CBaseActionEntity action)
    {
        CEntityCase branch = new CEntityCase(0, null, 0);
        branch.SetCondition(condition);
        branch.AddChild(action);
        return branch;
    }

    private CJavaCondEquals equals(String left, String right)
    {
        CJavaCondEquals condition = new CJavaCondEquals();
        condition.SetEqualCondition(
            new CJavaExprTerminal(new CJavaEntityNumber(null, null, left)),
            new CJavaExprTerminal(new CJavaEntityNumber(null, null, right)));
        return condition;
    }

    private static final class TestJavaSwitchCase extends CEntitySwitchCase
    {
        private TestJavaSwitchCase(MockJavaExporter output)
        {
            super(0, null);
        }
    }
}
