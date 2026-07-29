package generate.templates.recursive;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.java.CJavaAttribute;
import generate.java.CJavaExporter;
import generate.java.expressions.CJavaEntityNumber;
import generate.java.expressions.CJavaExprTerminal;
import generate.java.st.MockJavaExporter;
import semantic.Verbs.CEntityContinue;
import semantic.Verbs.CEntityLoopIter;
import generate.templates.TemplateLoader;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.expression.CEntityCondCompare;
import utils.CObjectCatalog;

class JavaSemanticLoopIterationTemplateTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
    private final CJavaExporter referenceOutput = new CJavaExporter(
        null, "/tmp/unused.java", null, false);
    private final JavaTemplateAssembler assembler = TemplateLoader.newRecursiveAssembler();

    @Test
    void rendersTestBeforeIncrementDecrementAndExplicitStepLikeDirect()
    {
        assertRendered(testBefore(Mode.INCREMENT, null),
            "for (move(1, outer_Index); isLess(outer_Index$1, 10); inc(outer_Index)) {\n// CONTINUE \n}");
        assertRendered(testBefore(Mode.DECREMENT, null),
            "for (move(1, outer_Index); isLess(outer_Index$1, 10); dec(outer_Index)) {\n// CONTINUE \n}");
        assertRendered(testBefore(Mode.EXPLICIT_STEP, number("2")),
            "for (move(1, outer_Index); isLess(outer_Index$1, 10); inc(2, outer_Index)) {\n// CONTINUE \n}");
    }

    @Test
    void recursivelyNestsAfterIterationsLikeTheDirectGenerator()
    {
        LoopFixture fixture = testBefore(Mode.INCREMENT, null);
        fixture.loop.AddAfter(
            attribute("INNER-INDEX", fixture.output),
            number("1"),
            number("2"),
            lessThan("INNER-INDEX", "10", fixture.output));

        assertRendered(fixture,
            "for (move(1, outer_Index); isLess(outer_Index$1, 10); inc(outer_Index)) {\n"
                + "for (move(1, inner_Index); isLess(inner_Index$1, 10); inc(2, inner_Index)) {\n"
                + "// CONTINUE \n}\n}");
    }

    @Test
    void rendersTestAfterIncrementAndDecrementLikeDirect()
    {
        assertRendered(testAfter(Mode.INCREMENT),
            "move(1, after_Index);\nwhile (true) {\n// CONTINUE \n"
                + "if (isLess(after_Index$1, 10)) {\ninc(after_Index) ;\n}\n"
                + "else {\nbreak ;\n}\n}");
        assertRendered(testAfter(Mode.DECREMENT),
            "move(1, after_Index);\nwhile (true) {\n// CONTINUE \n"
                + "if (isLess(after_Index$1, 10)) {\ndec(after_Index) ;\n}\n"
                + "else {\nbreak ;\n}\n}");
    }

    private LoopFixture testBefore(Mode mode, CDataEntity step)
    {
        MockJavaExporter output = new MockJavaExporter();
        TestJavaLoopIter loop = new TestJavaLoopIter(output);
        configure(loop, mode, attribute("OUTER-INDEX", output), number("1"), step);
        loop.SetWhileCondition(lessThan("OUTER-INDEX", "10", output), true);
        loop.AddChild(new CEntityContinue(0, null));
        return new LoopFixture(loop, output);
    }

    private LoopFixture testAfter(Mode mode)
    {
        MockJavaExporter output = new MockJavaExporter();
        TestJavaLoopIter loop = new TestJavaLoopIter(output);
        configure(loop, mode, attribute("AFTER-INDEX", output), number("1"), null);
        loop.SetWhileCondition(lessThan("AFTER-INDEX", "10", output), false);
        loop.AddChild(new CEntityContinue(0, null));
        return new LoopFixture(loop, output);
    }

    private void configure(
        TestJavaLoopIter loop,
        Mode mode,
        CDataEntity variable,
        CDataEntity initialValue,
        CDataEntity step)
    {
        switch (mode)
        {
            case INCREMENT -> loop.SetLoopIterInc(variable, initialValue);
            case DECREMENT -> loop.SetLoopIterDec(variable, initialValue);
            case EXPLICIT_STEP -> loop.SetLoopIter(variable, initialValue, step);
        }
    }

    private CEntityCondCompare lessThan(String variableName, String limit, MockJavaExporter output)
    {
        CEntityCondCompare condition = new CEntityCondCompare();
        condition.SetLessThan(
            new CJavaExprTerminal(attribute(variableName, output)),
            new CJavaExprTerminal(number(limit)));
        return condition;
    }

    private CJavaAttribute attribute(String name, MockJavaExporter output)
    {
        return new CJavaAttribute(0, name, catalog, referenceOutput);
    }

    private CJavaEntityNumber number(String value)
    {
        return new CJavaEntityNumber(catalog, null, value);
    }

    private void assertRendered(LoopFixture fixture, String expected)
    {
        assertEquals(normalize(expected), normalize(assembler.renderRoot(fixture.loop, JavaTemplateRole.REFERENCE).stripTrailing()));
    }

    private String normalize(String source)
    {
        return source.replaceAll("((?:outer|after|inner)_Index)\\$\\d+", "$1");
    }

    private record LoopFixture(TestJavaLoopIter loop, MockJavaExporter output) {}

    private enum Mode { INCREMENT, DECREMENT, EXPLICIT_STEP }

    private static final class TestJavaLoopIter extends CEntityLoopIter
    {
        private TestJavaLoopIter(MockJavaExporter output)
        {
            super(0, null);
        }
    }
}
