package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityExternalDataStructure;
import utils.CObjectCatalog;

class CEntityExternalDataStructureRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityExternalDataStructure.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityExternalDataStructure(1, "MY-COPY").getClass());
        assertEquals(CEntityExternalDataStructure.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityExternalDataStructure(1, "MY-COPY").getClass());
    }

    @Test
    void assemblerSeparatesReferenceAndRootArtifact()
    {
        CEntityExternalDataStructure copybook =
            new CEntityExternalDataStructure(1, "MY-COPY", catalog);
        assertEquals("MY_COPY", TemplateLoader.getRecursiveAssembler()
            .renderRoot(copybook, JavaTemplateRole.REFERENCE));
        String root = TemplateLoader.getRecursiveAssembler()
            .renderRoot(copybook, JavaTemplateRole.ROOT);
        assertTrue(root.contains("public class My_copy extends Copy"), root);
    }
}
