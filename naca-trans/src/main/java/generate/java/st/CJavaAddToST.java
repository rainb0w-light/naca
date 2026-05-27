package generate.java.st;

import generate.CBaseLanguageExporter;
import generate.templates.TemplateLoader;
import org.stringtemplate.v4.ST;
import semantic.Verbs.CEntityAddTo;
import utils.CObjectCatalog;

public class CJavaAddToST extends CEntityAddTo {
    public CJavaAddToST(int l, CObjectCatalog cat, CBaseLanguageExporter out) {
        super(l, cat, out);
    }

    @Override
    protected void DoExport() {
        ST template = TemplateLoader.getVerbsTemplate("addTo");
        template.add("entity", this);
        String output = template.render();
        if (!output.trim().isEmpty()) {
            WriteLine(output);
        }
    }
}
