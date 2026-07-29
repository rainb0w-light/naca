package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.fixtures.LegacyExternalDataStructureFixture;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityAttribute;
import semantic.CEntityInline;
import utils.CObjectCatalog;

class CEntityInlineRenderTest
{
    @Test
    void bothFactoriesReturnPureSemanticEntityAndRegisterTheCopybook()
    {
        assertPureFactory(new CJavaEntityFactory(catalog(), null));
        assertPureFactory(new CJavaEntityFactoryST(catalog(), null));
    }

    @Test
    void inlineCopybookRendersItsDeclarationsWithoutAnInstanceHeader()
    {
        CObjectCatalog catalog = catalog();
        MockJavaExporter exporter = new MockJavaExporter();
        LegacyExternalDataStructureFixture copybook =
            new LegacyExternalDataStructureFixture(1, "INLINE-COPY", catalog, exporter);
        copybook.SetInline(true);
        CEntityInline inline = new CEntityInline(2, catalog, copybook);
        CEntityAttribute child = new CEntityAttribute(3, "COPY-FIELD", catalog)
        {
            @Override
            public boolean ignore()
            {
                return false;
            }
        };
        child.SetLevel("01");
        child.SetTypeString(4);
        inline.AddChild(child);

        String rendered = TemplateLoader.getRecursiveAssembler()
            .renderRoot(inline, JavaTemplateRole.DECLARATION);
        assertFalse(rendered.contains(".Copy(this"), rendered);
        assertTrue(rendered.contains(
            "Var COPY_FIELD = declare.level(01).picX(4).var() ;"), rendered);
    }

    private static void assertPureFactory(CJavaEntityFactory factory)
    {
        CObjectCatalog catalog = catalog();
        LegacyExternalDataStructureFixture copybook =
            new LegacyExternalDataStructureFixture(1, "COPYBOOK", catalog, null);
        assertEquals(CEntityInline.class,
            factory.NewEntityInline(2, copybook).getClass());
    }

    private static CObjectCatalog catalog()
    {
        return new CObjectCatalog(null, null, null, null);
    }
}
