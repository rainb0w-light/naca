package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.fixtures.LegacyIndexFixture;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.CEntityIndex;
import utils.CObjectCatalog;

class CEntityIndexRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityIndex.class,
            new CJavaEntityFactory(catalog, null).NewEntityIndex("ITEM-INDEX").getClass());
        assertEquals(CEntityIndex.class,
            new CJavaEntityFactoryST(catalog, null).NewEntityIndex("ITEM-INDEX").getClass());
    }

    @Test
    void assemblerSeparatesReferenceAndDeclaration()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CEntityIndex semantic = new CEntityIndex("ITEM-INDEX", catalog);
        LegacyIndexFixture legacy =
            new LegacyIndexFixture("ITEM-INDEX",
                new CObjectCatalog(null, null, null, null), exporter);
        generate.LegacyLanguageRenderer.startExport(legacy);

        assertEquals("ITEM_INDEX", TemplateLoader.getRecursiveAssembler()
            .renderRoot(semantic, JavaTemplateRole.REFERENCE));
        assertEquals(normalize(exporter.getCapturedOutput()), normalize(
            TemplateLoader.getRecursiveAssembler()
                .renderRoot(semantic, JavaTemplateRole.DECLARATION)));
    }

    private static String normalize(String source)
    {
        return source.replaceAll("\\s+", " ").strip();
    }
}
