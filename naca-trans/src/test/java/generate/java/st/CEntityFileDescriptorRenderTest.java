package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityFileDescriptor;
import utils.CObjectCatalog;

class CEntityFileDescriptorRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityFileDescriptor.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityFileDescriptor(1, "FILE-IN").getClass());
        assertEquals(CEntityFileDescriptor.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityFileDescriptor(1, "FILE-IN").getClass());
    }

    @Test
    void assemblerSeparatesReferenceAndDeclaration()
    {
        CEntityFileDescriptor file = new CEntityFileDescriptor(1, "FILE-IN", catalog);
        assertEquals("FILE_IN", TemplateLoader.getRecursiveAssembler()
            .renderRoot(file, JavaTemplateRole.REFERENCE));
        assertTrue(TemplateLoader.getRecursiveAssembler()
            .renderRoot(file, JavaTemplateRole.DECLARATION)
            .contains("FileDescriptor FILE_IN = declare.file(\"FILE-IN\") ;"));
    }
}
