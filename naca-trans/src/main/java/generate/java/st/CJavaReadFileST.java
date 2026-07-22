package generate.java.st;

import generate.CBaseLanguageExporter;
import generate.templates.TemplateLoader;
import org.stringtemplate.v4.ST;
import semantic.Verbs.CEntityReadFile;
import utils.CObjectCatalog;

/**
 * ST4-based implementation of CEntityReadFile (READ).
 *
 * In production a READ statement is rendered as a child of a procedure through
 * the recursive assembler binding (CEntityReadFile=recursiveReadFileEntity).
 * This {@code DoExport} keeps the legacy template path so the controller's
 * dedicated unit tests (which drive {@code DoExport} directly with data-entity
 * mocks that are not recursive-binding aware) remain valid.
 */
public class CJavaReadFileST extends CEntityReadFile {
    public CJavaReadFileST(int line, CObjectCatalog cat, CBaseLanguageExporter out) {
        super(line, cat);
        setLanguageExporter(out);
    }

    @Override
    protected void DoExport() {
        ST template = TemplateLoader.getVerbsTemplate("readFile");
        template.add("entity", this);
        String output = template.render();
        writeRendered(output);
    }

    private void writeRendered(String output) {
        for (String line : output.split("\\R")) {
            WriteLine(line);
        }
    }
}
