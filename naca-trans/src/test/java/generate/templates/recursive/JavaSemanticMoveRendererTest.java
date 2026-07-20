package generate.templates.recursive;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.java.CJavaAttribute;
import generate.java.CJavaExporter;
import generate.java.expressions.CJavaEntityNumber;
import generate.java.st.MockJavaExporter;
import generate.java.verbs.CJavaAssign;
import generate.templates.TemplateLoader;
import org.junit.jupiter.api.Test;
import utils.CObjectCatalog;

class JavaSemanticMoveRendererTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);
    private final CJavaExporter referenceOutput = new CJavaExporter(
        null, "/tmp/unused.java", null, false);
    private final JavaTemplateAssembler assembler = TemplateLoader.newRecursiveAssembler();

    @Test
    void rendersMoveToMultipleDestinationsLikeTheDirectGenerator()
    {
        assertMatchesDirect(move(false, false));
    }

    @Test
    void rendersMoveAllAndMoveCorrespondingLikeTheDirectGenerator()
    {
        assertMatchesDirect(move(true, false));
        assertMatchesDirect(move(false, true));
    }

    @Test
    void rendersNoStatementsWhenThereAreNoDestinations()
    {
        MockJavaExporter output = new MockJavaExporter();
        TestJavaAssign move = new TestJavaAssign(output);
        move.SetValue(number("7"));

        assertMatchesDirect(new MoveFixture(move, output));
    }

    private MoveFixture move(boolean fillAll, boolean corresponding)
    {
        MockJavaExporter output = new MockJavaExporter();
        TestJavaAssign move = new TestJavaAssign(output);
        move.SetValue(number("7"));
        move.AddRefTo(attribute("FIRST-DESTINATION"));
        move.AddRefTo(attribute("SECOND-DESTINATION"));
        move.SetFillAll(fillAll);
        move.SetAssignCorresponding(corresponding);
        return new MoveFixture(move, output);
    }

    private CJavaAttribute attribute(String name)
    {
        return new CJavaAttribute(0, name, catalog, referenceOutput);
    }

    private CJavaEntityNumber number(String value)
    {
        return new CJavaEntityNumber(catalog, referenceOutput, value);
    }

    private void assertMatchesDirect(MoveFixture fixture)
    {
        fixture.move.exportDirect();
        assertEquals(fixture.output.getCapturedOutput().stripTrailing(),
            assembler.renderRoot(fixture.move).stripTrailing());
    }

    private record MoveFixture(TestJavaAssign move, MockJavaExporter output) {}

    private static final class TestJavaAssign extends CJavaAssign
    {
        private TestJavaAssign(MockJavaExporter output)
        {
            super(0, null, output);
        }

        private void exportDirect()
        {
            super.DoExport();
        }
    }
}
