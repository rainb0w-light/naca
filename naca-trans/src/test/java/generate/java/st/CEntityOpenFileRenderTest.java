package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityFileDescriptor;
import semantic.Verbs.CEntityOpenFile;
import semantic.Verbs.CEntityOpenFile.OpenMode;
import utils.CObjectCatalog;

class CEntityOpenFileRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static CEntityFileDescriptor fileDescriptor(String name)
    {
        return new CEntityFileDescriptor(1, name, null)
        {
            @Override
            protected void RegisterMySelfToCatalog()
            {
                // No catalog is needed for this focused reference fixture.
            }
        };
    }

    private static String render(OpenMode mode)
    {
        CEntityOpenFile open = new CEntityOpenFile(1, null);
        open.setFileDescriptor(fileDescriptor("CUSTOMER-FILE"), mode);
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(open, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityOpenFile.class,
            new CJavaEntityFactory(catalog, null).NewEntityOpenFile(1).getClass());
        assertEquals(CEntityOpenFile.class,
            new CJavaEntityFactory(catalog, null).NewEntityOpenFile(1).getClass());
    }

    @Test
    void rendersEveryCobolOpenModeThroughTheRuntimeContract()
    {
        assertEquals("CUSTOMER_FILE.openInput();", render(OpenMode.INPUT));
        assertEquals("CUSTOMER_FILE.openOutput();", render(OpenMode.OUTPUT));
        assertEquals("CUSTOMER_FILE.openInputOutput();", render(OpenMode.INPUT_OUTPUT));
        assertEquals("CUSTOMER_FILE.openExtend();", render(OpenMode.APPEND));
    }
}
