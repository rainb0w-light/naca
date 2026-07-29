package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityAttribute;
import semantic.CEntitySortedFileDescriptor;
import utils.CObjectCatalog;

class CEntitySortedFileDescriptorRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntitySortedFileDescriptor.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntitySortedFileDescriptor(1, "SORT-FILE").getClass());
        assertEquals(CEntitySortedFileDescriptor.class,
            new CJavaEntityFactoryST(catalog, null)
                .NewEntitySortedFileDescriptor(1, "SORT-FILE").getClass());
    }

    @Test
    void declarationRecursivelyRendersTheFirstRecord()
    {
        CEntitySortedFileDescriptor descriptor =
            new CEntitySortedFileDescriptor(1, "SORT-FILE", catalog);
        CEntityAttribute record = new CEntityAttribute(2, "SORT-RECORD", catalog)
        {
            @Override
            public boolean ignore()
            {
                return false;
            }
        };
        record.SetLevel("01");
        record.SetTypeString(8);
        descriptor.AddChild(record);

        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(descriptor, JavaTemplateRole.DECLARATION);
        assertTrue(rendered.contains(
            "SortDescriptor SORT_FILE = declare.sort() ;"), rendered);
        assertTrue(rendered.contains(
            "Var SORT_RECORD = declare.level(01).picX(8).var() ;"), rendered);
    }
}
