package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityFileDescriptor;
import semantic.Verbs.CEntityRewriteFile;
import utils.CObjectCatalog;

class CEntityRewriteFileRenderTest
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

    private static String render(CEntityRewriteFile rewrite)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(rewrite, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityRewriteFile.class,
            new CJavaEntityFactory(catalog, null).NewEntityRewriteFile(1).getClass());
        assertEquals(CEntityRewriteFile.class,
            new CJavaEntityFactoryST(catalog, null).NewEntityRewriteFile(1).getClass());
    }

    @Test
    void rendersRecordAndFromFormsThroughTheRuntimeContract()
    {
        CEntityRewriteFile record = new CEntityRewriteFile(1, null);
        record.setFileDescriptor(fileDescriptor("CUSTOMER-FILE"), null);
        assertEquals("rewrite(CUSTOMER_FILE) ;", render(record));

        CEntityRewriteFile from = new CEntityRewriteFile(1, null);
        from.setFileDescriptor(
            fileDescriptor("CUSTOMER-FILE"), new MockDataEntity(1, "CUSTOMER_RECORD"));
        assertEquals("rewriteFrom(CUSTOMER_FILE, CUSTOMER_RECORD) ;", render(from));
    }
}
