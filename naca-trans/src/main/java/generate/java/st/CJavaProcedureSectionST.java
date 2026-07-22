package generate.java.st;

import generate.CBaseLanguageExporter;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.CEntityProcedureSection;
import utils.CObjectCatalog;

/**
 * ST4 production emitter for a COBOL SECTION. Renders the section (its
 * Paragraph/Section wrapper, body bloc and child paragraphs) through the
 * recursive assembler rather than the legacy export walk.
 */
public class CJavaProcedureSectionST extends CEntityProcedureSection {
    public CJavaProcedureSectionST(int l, String name, CObjectCatalog cat, CBaseLanguageExporter out) {
        super(l, name, cat);
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
