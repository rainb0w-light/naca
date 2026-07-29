package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CDataEntity;
import semantic.CEntityUnknownReference;
import utils.CObjectCatalog;

class CEntityUnknownReferenceRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void bothCobolFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityUnknownReference.class,
            new CJavaEntityFactory(catalog, null)
                .NewEntityUnknownReference(1, "MISSING").getClass());
        assertEquals(CEntityUnknownReference.class,
            new CJavaEntityFactoryST(catalog, null)
                .NewEntityUnknownReference(1, "MISSING").getClass());
    }

    @Test
    void unresolvedReferenceFailsClosedWithoutGeneratingSource()
    {
        CEntityUnknownReference reference =
            new CEntityUnknownReference(1, "MISSING", catalog);

        assertEquals(CDataEntity.CDataEntityType.UNKNWON,
            reference.GetDataType());
        assertFalse(reference.HasAccessors());
        assertFalse(reference.isValNeeded());
        assertEquals("",
            generate.LegacyDataRenderer.renderReference(reference, 1));
        assertEquals("", TemplateLoader.getRecursiveAssembler()
            .renderRoot(reference, JavaTemplateRole.REFERENCE));
        assertEquals("", TemplateLoader.getRecursiveAssembler()
            .renderRoot(reference, JavaTemplateRole.DECLARATION));
    }
}
