package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityFileDescriptor;
import semantic.Verbs.CEntityWriteFile;
import utils.CObjectCatalog;

class CEntityWriteFileRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    private static CEntityFileDescriptor file(String name)
    {
        return new CEntityFileDescriptor(1, name, null)
        {
            @Override
            protected void RegisterMySelfToCatalog()
            {
                // No-op test descriptor.
            }
        };
    }

    private static String render(CEntityWriteFile write)
    {
        return TemplateLoader.getRecursiveAssembler()
            .renderRoot(write, JavaTemplateRole.REFERENCE).trim();
    }

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityWriteFile.class,
            new CJavaEntityFactory(catalog, null).NewEntityWriteFile(1).getClass());
        assertEquals(CEntityWriteFile.class,
            new CJavaEntityFactory(catalog, null).NewEntityWriteFile(1).getClass());
    }

    @Test
    void rendersWriteAndWriteFromThroughBaseProgramRuntime()
    {
        CEntityWriteFile write = new CEntityWriteFile(1, catalog);
        write.setFileDescriptor(file("OUTPUT-FILE"), null);
        assertEquals("write(OUTPUT_FILE);", render(write));

        write.setFileDescriptor(
            file("OUTPUT-FILE"), new MockDataEntity(1, "OUTPUT_RECORD"));
        assertEquals("writeFrom(OUTPUT_FILE, OUTPUT_RECORD);", render(write));
    }

    @Test
    void setAfterRetainsAndRendersItsSemanticOperand()
    {
        CEntityWriteFile write = new CEntityWriteFile(1, catalog);
        write.setFileDescriptor(file("OUTPUT-FILE"), null);
        write.SetAfter(new MockDataEntity(1, "2"));

        assertEquals("writeAfter(OUTPUT_FILE, 2);", render(write));
    }

    @Test
    void missingFileDescriptorRemainsExplicit()
    {
        assertEquals("write([UnknownReference]);",
            render(new CEntityWriteFile(1, catalog)));
    }
}
