package generate.templates.recursive;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.java.expressions.CJavaCondEquals;
import generate.java.expressions.CJavaEntityNumber;
import generate.java.expressions.CJavaExprTerminal;
import generate.java.st.MockJavaExporter;
import semantic.Verbs.CEntityBreak;
import semantic.Verbs.CEntityContinue;
import semantic.Verbs.CEntityLoopWhile;
import generate.templates.TemplateLoader;
import org.junit.jupiter.api.Test;

class JavaSemanticLoopWhileTemplateTest
{
    private final JavaTemplateAssembler assembler = TemplateLoader.newRecursiveAssembler();

    @Test
    void recursivelyRendersWhileAndDoWhileLikeTheDirectGenerator()
    {
        MockJavaExporter whileOutput = new MockJavaExporter();
        TestJavaLoopWhile whileLoop = loop(whileOutput);
        whileLoop.SetWhileCondition(equals("1", "1"));
        assertEquals("while (isEqual(1, 1)) {\n// CONTINUE \nbreak;\n}",
            assembler.renderRoot(whileLoop, JavaTemplateRole.REFERENCE).stripTrailing());

        MockJavaExporter doOutput = new MockJavaExporter();
        TestJavaLoopWhile doLoop = loop(doOutput);
        doLoop.SetDoWhileCondition(equals("2", "2"));
        assertEquals("do {\n// CONTINUE \nbreak;\n}\nwhile (isEqual(2, 2)) ;",
            assembler.renderRoot(doLoop, JavaTemplateRole.REFERENCE).stripTrailing());
    }

    @Test
    void recursivelyRendersNestedLoops()
    {
        MockJavaExporter output = new MockJavaExporter();
        TestJavaLoopWhile inner = loop(output);
        inner.SetWhileCondition(equals("2", "2"));

        TestJavaLoopWhile outer = new TestJavaLoopWhile(output);
        outer.SetDoWhileCondition(equals("1", "1"));
        outer.AddChild(inner);

        assertEquals("do {\nwhile (isEqual(2, 2)) {\n// CONTINUE \nbreak;\n}\n}\nwhile (isEqual(1, 1)) ;",
            assembler.renderRoot(outer, JavaTemplateRole.REFERENCE).stripTrailing());
    }

    @Test
    void usesTheSemanticOppositeForUntilLoops()
    {
        MockJavaExporter output = new MockJavaExporter();
        TestJavaLoopWhile loop = loop(output);
        loop.SetUntilCondition(equals("1", "2"));

        assertEquals("while (isDifferent(1, 2)) {\n// CONTINUE \nbreak;\n}",
            assembler.renderRoot(loop, JavaTemplateRole.REFERENCE).stripTrailing());
    }

    private TestJavaLoopWhile loop(MockJavaExporter output)
    {
        TestJavaLoopWhile loop = new TestJavaLoopWhile(output);
        loop.AddChild(new CEntityContinue(0, null));
        loop.AddChild(new CEntityBreak(0, null));
        return loop;
    }

    private CJavaCondEquals equals(String left, String right)
    {
        CJavaCondEquals condition = new CJavaCondEquals();
        condition.SetEqualCondition(
            new CJavaExprTerminal(new CJavaEntityNumber(null, null, left)),
            new CJavaExprTerminal(new CJavaEntityNumber(null, null, right)));
        return condition;
    }

    private static final class TestJavaLoopWhile extends CEntityLoopWhile
    {
        private TestJavaLoopWhile(MockJavaExporter output)
        {
            super(0, null);
        }
    }
}
