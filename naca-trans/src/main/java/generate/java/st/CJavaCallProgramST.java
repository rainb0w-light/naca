package generate.java.st;

import generate.CBaseLanguageExporter;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import semantic.CDataEntity;
import semantic.Verbs.CEntityCallProgram;
import utils.CObjectCatalog;

/**
 * ST4-based implementation of CEntityCallProgram (CALL).
 * Renders through the recursive assembler
 * (CEntityCallProgram=recursiveCallProgramEntity).
 */
public class CJavaCallProgramST extends CEntityCallProgram {
    public CJavaCallProgramST(int l, CObjectCatalog cat, CBaseLanguageExporter out, CDataEntity reference) {
        super(l, cat, reference);
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
}
