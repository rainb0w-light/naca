package generate.java.st;

import static org.junit.jupiter.api.Assertions.assertTrue;

import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import org.junit.jupiter.api.Test;
import semantic.Verbs.CEntityStringConcat;

class CJavaStringConcatSTTest
{
    @Test
    void rendersOrderedItemsAndDestinationFromTheSemanticModel()
    {
        MockJavaExporter exporter = new MockJavaExporter();
        CEntityStringConcat concat = new CEntityStringConcat(1, null);
        concat.setLanguageExporter(exporter);
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
        concat.setLanguageExporter(exporter);
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
}
