package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactory;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityAddressReference;
import semantic.CEntityMoveReference;
import utils.CObjectCatalog;

class CEntityMoveReferenceRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityMoveReference.class,
            new CJavaEntityFactory(catalog, null).NewEntityMoveReference(1).getClass());
        assertEquals(CEntityMoveReference.class,
            new CJavaEntityFactory(catalog, null).NewEntityMoveReference(1).getClass());
    }

    @Test
    void recursivelyRendersBothPointerOperands()
    {
        CEntityMoveReference move = new CEntityMoveReference(1, catalog);
        move.SetMoveReference(
            new CEntityAddressReference(catalog, new MockDataEntity(1, "FROM_FIELD")),
            new CEntityAddressReference(catalog, new MockDataEntity(1, "TO_FIELD")));

        assertEquals(
            "moveReferenceTo(addressOf(FROM_FIELD), addressOf(TO_FIELD)) ;",
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(move, JavaTemplateRole.REFERENCE).strip());
    }
}
