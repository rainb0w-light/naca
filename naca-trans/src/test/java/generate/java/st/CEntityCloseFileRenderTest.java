package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityFileDescriptor;
import semantic.Verbs.CEntityCloseFile;
import utils.CObjectCatalog;

class CEntityCloseFileRenderTest
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

    private static String render(CEntityCloseFile close)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(close, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertInstanceOf(CEntityCloseFile.class,
            new CJavaEntityFactory(catalog, null).NewEntityCloseFile(1));
        assertInstanceOf(CEntityCloseFile.class,
            new CJavaEntityFactoryST(catalog, null).NewEntityCloseFile(1));
    }

    @Test
    void rendersDescriptorCloseThroughRuntimeContract()
    {
        CEntityCloseFile close = new CEntityCloseFile(1, catalog);
        close.setFileDescriptor(fileDescriptor("CUSTOMER-FILE"));

        assertEquals("CUSTOMER_FILE.close();", render(close));
    }
}
