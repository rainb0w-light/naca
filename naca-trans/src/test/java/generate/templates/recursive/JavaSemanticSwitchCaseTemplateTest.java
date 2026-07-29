package generate.templates.recursive;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.java.expressions.CJavaExprTerminal;
import generate.java.st.MockJavaExporter;
import semantic.Verbs.CEntityBreak;
import semantic.Verbs.CEntityCase;
import semantic.Verbs.CEntityContinue;
import semantic.Verbs.CEntitySwitchCase;
import generate.templates.TemplateLoader;
import org.junit.jupiter.api.Test;
import semantic.CIgnoredEntity;
import semantic.expression.CEntityCondEquals;

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
            assembler.renderRoot(evaluate, JavaTemplateRole.REFERENCE).stripTrailing());
    }

    @Test
    void skipsIgnoredWhenBranchesWithoutBreakingElseChaining()
    {
        MockJavaExporter output = new MockJavaExporter();
        TestJavaSwitchCase evaluate = new TestJavaSwitchCase(output);
        CEntityCondEquals ignored = new CEntityCondEquals();
        ignored.SetEqualCondition(
            new CJavaExprTerminal(new CIgnoredEntity(0, "", null)),
            new CJavaExprTerminal(new LegacyNumberFixture(null, "0")));
        evaluate.AddChild(branch(output, ignored, new CEntityBreak(0, null)));
        evaluate.AddChild(branch(output, equals("1", "1"), new CEntityContinue(0, null)));
        evaluate.AddChild(branch(output, null, new CEntityBreak(0, null)));

        assertEquals("if (isEqual(1, 1))\n{\n// CONTINUE \n}\nelse \n{\nbreak;\n}",
            assembler.renderRoot(evaluate, JavaTemplateRole.REFERENCE).stripTrailing());
    }

    private CEntityCase branch(
        MockJavaExporter output,
        CEntityCondEquals condition,
        semantic.CBaseActionEntity action)
    {
        CEntityCase branch = new CEntityCase(0, null, 0);
        branch.SetCondition(condition);
        branch.AddChild(action);
        return branch;
    }

    private CEntityCondEquals equals(String left, String right)
    {
        CEntityCondEquals condition = new CEntityCondEquals();
        condition.SetEqualCondition(
            new CJavaExprTerminal(new LegacyNumberFixture(null, left)),
            new CJavaExprTerminal(new LegacyNumberFixture(null, right)));
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
