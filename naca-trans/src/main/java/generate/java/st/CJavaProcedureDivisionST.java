package generate.java.st;

import generate.CBaseLanguageExporter;
import generate.templates.TemplateLoader;
import semantic.CEntityProcedureDivision;
import utils.CObjectCatalog;

/**
 * ST4 production emitter for the PROCEDURE DIVISION. Instead of walking the
 * semantic tree with the legacy {@code DoExport}/{@code ExportChildren}
 * protocol, it renders the whole division subtree through the recursive
 * {@link generate.templates.recursive.JavaTemplateAssembler} and writes the
 * resulting lines. This is the bridge that wires the recursive ST pipeline
 * into the live export driver.
 */
public class CJavaProcedureDivisionST extends CEntityProcedureDivision {
    public CJavaProcedureDivisionST(int line, CObjectCatalog cat, CBaseLanguageExporter out) {
        super(line, cat);
        setLanguageExporter(out);
    }

    @Override
    protected void DoExport() {
        String rendered = TemplateLoader.getRecursiveAssembler().renderRoot(this);
        for (String line : rendered.split("\n", -1)) {
            if (!line.isEmpty()) {
                WriteLine(line);
            }
        }
    }
}
