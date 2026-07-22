package generate.java.st;

import generate.CBaseLanguageExporter;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.CEntityProcedure;
import semantic.CEntityProcedureSection;
import utils.CObjectCatalog;

/**
 * ST4 production emitter for a COBOL paragraph. Renders the paragraph wrapper
 * and its statements through the recursive assembler rather than the legacy
 * {@code ExportChildren} walk.
 */
public class CJavaProcedureST extends CEntityProcedure {
    public CJavaProcedureST(int l, String name, CObjectCatalog cat, CBaseLanguageExporter out, CEntityProcedureSection section) {
        super(l, name, cat, section);
        setLanguageExporter(out);
    }

    @Override
    protected void DoExport() {
        String rendered = TemplateLoader.getRecursiveAssembler().renderRoot(this, JavaTemplateRole.REFERENCE);
        for (String line : rendered.split("\n", -1)) {
            if (!line.isEmpty()) {
                WriteLine(line);
            }
        }
    }

    @Override
    public String ExportReference(int nLine) {
        return FormatIdentifier(GetName());
    }
}
