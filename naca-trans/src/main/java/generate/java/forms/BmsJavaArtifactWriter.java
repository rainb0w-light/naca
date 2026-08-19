package generate.java.forms;

import generate.CBaseLanguageExporter;
import generate.LanguageArtifactOutputRegistry;
import generate.templates.JavaTemplatePipeline;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.forms.CEntityResourceFormContainer;

/** Writes a fully assembled BMS mapset to the output selected during analysis. */
public final class BmsJavaArtifactWriter
{
    private BmsJavaArtifactWriter()
    {
    }

    /** Executes the write operation. */
    public static void write(CEntityResourceFormContainer mapset)
    {
        CBaseLanguageExporter output = LanguageArtifactOutputRegistry.get(mapset);
        if (output == null)
        {
            throw new IllegalStateException(
                "No Java artifact output registered for BMS mapset " + mapset.GetName());
        }
        String source = TemplateLoader.getRecursiveAssembler(JavaTemplatePipeline.BMS)
            .renderRoot(mapset, JavaTemplateRole.ROOT);
        output.WriteLine(source, mapset.getLine());
        output.closeOutput();
    }
}
