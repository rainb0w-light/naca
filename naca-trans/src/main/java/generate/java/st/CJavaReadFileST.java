package generate.java.st;

import generate.CBaseLanguageExporter;
import generate.templates.TemplateLoader;
import org.stringtemplate.v4.ST;
import semantic.Verbs.CEntityReadFile;
import utils.CObjectCatalog;

public class CJavaReadFileST extends CEntityReadFile {
    public CJavaReadFileST(int l, CObjectCatalog cat, CBaseLanguageExporter out) {
        super(l, cat, out);
    }

    @Override
    protected void DoExport() {
        ST template = TemplateLoader.getVerbsTemplate("readFile");
        template.add("entity", this);
        String output = template.render();
        WriteLine(output);
    }
}
