package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.CJavaEntityFactory;
import generate.CJavaEntityFactoryST;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.Verbs.CEntityBreak;
import semantic.Verbs.CEntityStringConcat;
import utils.CObjectCatalog;

class CEntityStringConcatRenderTest
{
    private final CObjectCatalog catalog = new CObjectCatalog(null, null, null, null);

    @Test
    void bothFactoriesReturnPureSemanticEntity()
    {
        assertEquals(CEntityStringConcat.class,
            new CJavaEntityFactory(catalog, null).NewEntityStringConcat(1).getClass());
        assertEquals(CEntityStringConcat.class,
            new CJavaEntityFactoryST(catalog, null).NewEntityStringConcat(1).getClass());
    }

    @Test
    void rendersOrderedItemsAndDestinationFromTheSemanticModel()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CEntityStringConcat concat = new CEntityStringConcat(1, null);
        generate.LegacyLanguageRenderer.bind(concat, exporter);
        concat.AddItem(new MockDataEntity(1, "prefix"));
        concat.AddItem(
            new MockDataEntity(1, "text"),
            new MockDataEntity(1, "delimiter"));
        concat.SetVariable(new MockDataEntity(1, "destination"));

        String output = TemplateLoader.getRecursiveAssembler().renderRoot(concat, JavaTemplateRole.REFERENCE);

        assertTrue(
            output.contains("concat(prefix).concatDelimitedBy(text, delimiter).into(destination);"),
            output);
    }

    @Test
    void replacesEveryOccurrenceOfTheSameSourceVariable()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CEntityStringConcat concat = new CEntityStringConcat(1, null);
        generate.LegacyLanguageRenderer.bind(concat, exporter);
        MockDataEntity source = new MockDataEntity(1, "source");
        MockDataEntity replacement = new MockDataEntity(1, "replacement");
        concat.AddItem(source);
        concat.AddItem(source, source);
        concat.SetVariable(new MockDataEntity(1, "destination"), source);

        assertTrue(concat.ReplaceVariable(source, replacement));
        String output = TemplateLoader.getRecursiveAssembler().renderRoot(concat, JavaTemplateRole.REFERENCE);

        assertTrue(
            output.contains("concat(replacement).concatDelimitedBy(replacement, replacement)"
                    + ".withPointer(replacement).into(destination);"),
            output);
    }

    @Test
    void recursivelyRendersOverflowChildren()
    {
        CEntityStringConcat concat = new CEntityStringConcat(1, catalog);
        concat.AddItem(new MockDataEntity(1, "source"));
        concat.SetVariable(
            new MockDataEntity(1, "destination"),
            new MockDataEntity(1, "pointer"));
        concat.AddChild(new CEntityBreak(1, catalog));

        String output = TemplateLoader.getRecursiveAssembler()
            .renderRoot(concat, JavaTemplateRole.REFERENCE);

        assertTrue(output.contains(
            "if (concat(source).withPointer(pointer).into(destination).failed())"),
            output);
        assertTrue(output.contains("break;"), output);
    }
}
