package generate.templates.recursive;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.fixtures.LegacyAttributeFixture;
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
    private final MockJavaExporter referenceOutput = new MockJavaExporter();
    private final JavaTemplateAssembler assembler = TemplateLoader.newRecursiveAssembler();

    @Test
    void rendersTestBeforeIncrementDecrementAndExplicitStepLikeDirect()
    {
        assertRendered(testBefore(Mode.INCREMENT, null),
            "for (move(1, OUTER_INDEX); isLess(OUTER_INDEX$1, 10); inc(OUTER_INDEX)) {\n// CONTINUE \n}");
        assertRendered(testBefore(Mode.DECREMENT, null),
            "for (move(1, OUTER_INDEX); isLess(OUTER_INDEX$1, 10); dec(OUTER_INDEX)) {\n// CONTINUE \n}");
        assertRendered(testBefore(Mode.EXPLICIT_STEP, number("2")),
            "for (move(1, OUTER_INDEX); isLess(OUTER_INDEX$1, 10); inc(2, OUTER_INDEX)) {\n// CONTINUE \n}");
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
            "for (move(1, OUTER_INDEX); isLess(OUTER_INDEX$1, 10); inc(OUTER_INDEX)) {\n"
                + "for (move(1, INNER_INDEX); isLess(INNER_INDEX$1, 10); inc(2, INNER_INDEX)) {\n"
                + "// CONTINUE \n}\n}");
    }

    @Test
    void rendersTestAfterIncrementAndDecrementLikeDirect()
    {
        assertRendered(testAfter(Mode.INCREMENT),
            "move(1, AFTER_INDEX);\nwhile (true) {\n// CONTINUE \n"
                + "if (isLess(AFTER_INDEX$1, 10)) {\ninc(AFTER_INDEX) ;\n}\n"
                + "else {\nbreak ;\n}\n}");
        assertRendered(testAfter(Mode.DECREMENT),
            "move(1, AFTER_INDEX);\nwhile (true) {\n// CONTINUE \n"
                + "if (isLess(AFTER_INDEX$1, 10)) {\ndec(AFTER_INDEX) ;\n}\n"
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
            new LegacyTerminalFixture(attribute(variableName, output)),
            new LegacyTerminalFixture(number(limit)));
        return condition;
    }

    private LegacyAttributeFixture attribute(String name, MockJavaExporter output)
    {
        return new LegacyAttributeFixture(0, name, catalog, referenceOutput);
    }

    private LegacyNumberFixture number(String value)
    {
        return new LegacyNumberFixture(catalog, value);
    }

    private void assertRendered(LoopFixture fixture, String expected)
    {
        assertEquals(normalize(expected), normalize(assembler.renderRoot(fixture.loop, JavaTemplateRole.REFERENCE).stripTrailing()));
    }

    private String normalize(String source)
    {
        return source.replaceAll("(OUTER_INDEX|AFTER_INDEX|INNER_INDEX)\\$\\d+", "$1");
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
