package generate.templates.recursive;

import static org.junit.jupiter.api.Assertions.assertEquals;

import semantic.CEntityBloc;
import semantic.CEntityCondition;
import generate.java.st.MockJavaExporter;
import semantic.Verbs.CEntityBreak;
import semantic.Verbs.CEntityContinue;
import generate.templates.TemplateLoader;
import org.junit.jupiter.api.Test;
import semantic.CIgnoredEntity;
import semantic.expression.CEntityCondEquals;

class JavaSemanticIfTemplateTest
{
    private final JavaTemplateAssembler assembler = TemplateLoader.newRecursiveAssembler();

    @Test
    void recursivelyRendersIfElseLikeTheDirectGenerator()
    {
        MockJavaExporter output = new MockJavaExporter();
        CEntityBloc thenBlock = block(output, new CEntityContinue(0, null));
        CEntityBloc elseBlock = block(output, new CEntityBreak(0, null));
        TestJavaCondition condition = new TestJavaCondition(output);
        condition.SetCondition(equals("1", "1"), thenBlock, elseBlock);

        assertEquals("if (isEqual(1, 1)) {\n// CONTINUE \n}\nelse {\nbreak;\n}",
            assembler.renderRoot(condition, JavaTemplateRole.REFERENCE).stripTrailing());
    }

    @Test
    void recursivelyRendersNestedIfTrees()
    {
        MockJavaExporter output = new MockJavaExporter();
        TestJavaCondition inner = new TestJavaCondition(output);
        inner.SetCondition(
            equals("2", "2"),
            block(output, new CEntityBreak(0, null)),
            null);

        TestJavaCondition outer = new TestJavaCondition(output);
        outer.SetCondition(equals("1", "1"), block(output, inner), null);

        assertEquals("if (isEqual(1, 1)) {\nif (isEqual(2, 2)) {\nbreak;\n}\n}",
            assembler.renderRoot(outer, JavaTemplateRole.REFERENCE).stripTrailing());
    }

    @Test
    void rendersNothingWhenTheConditionIsMissing()
    {
        MockJavaExporter output = new MockJavaExporter();
        TestJavaCondition condition = new TestJavaCondition(output);
        condition.SetCondition(null, block(output, new CEntityBreak(0, null)), null);

        assertEquals("", assembler.renderRoot(condition, JavaTemplateRole.REFERENCE).stripTrailing());
    }

    @Test
    void rendersOnlyTheElseBlockWhenTheConditionIsIgnored()
    {
        MockJavaExporter output = new MockJavaExporter();
        CEntityCondEquals ignored = new CEntityCondEquals();
        ignored.SetEqualCondition(
            new LegacyTerminalFixture(new CIgnoredEntity(0, "", null)),
            new LegacyTerminalFixture(new LegacyNumberFixture(null, "1")));
        TestJavaCondition condition = new TestJavaCondition(output);
        condition.SetCondition(
            ignored,
            block(output, new CEntityContinue(0, null)),
            block(output, new CEntityBreak(0, null)));

        assertEquals("{\nbreak;\n}", assembler.renderRoot(condition, JavaTemplateRole.REFERENCE).stripTrailing());
    }

    private CEntityCondEquals equals(String left, String right)
    {
        CEntityCondEquals condition = new CEntityCondEquals();
        condition.SetEqualCondition(
            new LegacyTerminalFixture(new LegacyNumberFixture(null, left)),
            new LegacyTerminalFixture(new LegacyNumberFixture(null, right)));
        return condition;
    }

    private CEntityBloc block(MockJavaExporter output, semantic.CBaseActionEntity action)
    {
        CEntityBloc block = new CEntityBloc(0, null);
        block.AddChild(action);
        return block;
    }

    private static final class TestJavaCondition extends CEntityCondition
    {
        private TestJavaCondition(MockJavaExporter output)
        {
            super(0, null);
        }
    }
}
