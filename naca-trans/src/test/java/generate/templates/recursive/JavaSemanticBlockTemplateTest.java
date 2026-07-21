package generate.templates.recursive;

import static org.junit.jupiter.api.Assertions.assertEquals;

import semantic.CEntityBloc;
import generate.java.st.MockJavaExporter;
import semantic.Verbs.CEntityBreak;
import semantic.Verbs.CEntityContinue;
import generate.templates.TemplateLoader;
import org.junit.jupiter.api.Test;
import semantic.CEntityNoAction;

class JavaSemanticBlockTemplateTest
{
    private final JavaTemplateAssembler assembler = TemplateLoader.newRecursiveAssembler();

    @Test
    void rendersAnEmptyBlockLikeTheDirectGenerator()
    {
        MockJavaExporter output = new MockJavaExporter();
        TestJavaBloc block = new TestJavaBloc(0, output);

        assertEquals("", assembler.renderRoot(block, JavaTemplateRole.REFERENCE).stripTrailing());
    }

    @Test
    void recursivelyRendersActiveActionsAndSkipsIgnoredActions()
    {
        MockJavaExporter output = new MockJavaExporter();
        TestJavaBloc block = new TestJavaBloc(0, output);
        block.AddChild(new CEntityContinue(0, null));
        block.AddChild(new CEntityNoAction(0, null));
        block.AddChild(new CEntityBreak(0, null));

        assertEquals("// CONTINUE \nbreak;", assembler.renderRoot(block, JavaTemplateRole.REFERENCE).stripTrailing());
    }

    private static final class TestJavaBloc extends CEntityBloc
    {
        private TestJavaBloc(int line, MockJavaExporter output)
        {
            super(line, null);
        }

        private void exportDirect()
        {
            super.DoExport();
        }
    }
}
