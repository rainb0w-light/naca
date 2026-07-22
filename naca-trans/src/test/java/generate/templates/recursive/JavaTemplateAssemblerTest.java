package generate.templates.recursive;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import generate.java.expressions.CJavaEntityNumber;
import generate.templates.TemplateLoader;
import org.junit.jupiter.api.Test;
import utils.CObjectCatalog;

class JavaTemplateAssemblerTest
{
    @Test
    void rendersADeclarativelyBoundSemanticEntity()
    {
        JavaTemplateAssembler assembler = TemplateLoader.newRecursiveAssembler();
        CJavaEntityNumber number = new CJavaEntityNumber(
            new CObjectCatalog(null, null, null, null), null, "00042");

        assertEquals("42", assembler.renderRoot(number, JavaTemplateRole.REFERENCE));
    }

    @Test
    void failsClosedWhenNoDeclarativeBindingExists()
    {
        JavaTemplateAssembler assembler = TemplateLoader.newRecursiveAssembler();

        assertThrows(
            MissingTemplateRendererException.class,
            () -> assembler.renderNode(new Object()));
    }
}
