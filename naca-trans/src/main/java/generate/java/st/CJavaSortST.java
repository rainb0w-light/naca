package generate.java.st;

import generate.CBaseLanguageExporter;
import generate.templates.TemplateLoader;
import generate.templates.recursive.JavaTemplateRole;
import java.util.ArrayList;
import java.util.List;
import semantic.CDataEntity;
import semantic.CEntityFileDescriptor;
import semantic.CEntityProcedure;
import semantic.Verbs.CEntitySort;
import utils.CObjectCatalog;

/**
 * ST4-based implementation of CEntitySort (SORT).
 * Renders through the recursive assembler (CEntitySort=recursiveSortEntity).
 * Mirrors the direct generator:
 * {@code sort(file).ascKey/descKey(..).using/usingInput(..).giving/usingOutput(..).exec() ;}.
 */
public class CJavaSortST extends CEntitySort {
    public CJavaSortST(int l, CObjectCatalog cat, CBaseLanguageExporter out) {
        super(l, cat);
        setLanguageExporter(out);
    }

    public CEntityFileDescriptor getFileDescriptor() {
        return fileDescriptor;
    }

    public CEntityFileDescriptor getInputFile() {
        return fdInputFile;
    }

    public CEntityFileDescriptor getOutputFile() {
        return fdOutputFile;
    }

    /** Resolved INPUT PROCEDURE reference (or {@code [name]} placeholder), or null. */
    public String getInputProcedureRef() {
        CEntityProcedure p = pInputProcedure;
        if (p == null && csInputProcedureName != null) {
            p = programCatalog.GetProcedure(csInputProcedureName, "");
        }
        if (p != null) {
            return p.ExportReference(getLine());
        }
        return csInputProcedureName != null ? "[" + csInputProcedureName + "]" : null;
    }

    /** Resolved OUTPUT PROCEDURE reference (or {@code [name]} placeholder), or null. */
    public String getOutputProcedureRef() {
        CEntityProcedure p = pOutputProcedure;
        if (p == null && csOutputProcedureName != null) {
            p = programCatalog.GetProcedure(csOutputProcedureName, "");
        }
        if (p != null) {
            return p.ExportReference(getLine());
        }
        return csOutputProcedureName != null ? "[" + csOutputProcedureName + "]" : null;
    }

    /** One SORT KEY clause: {@code .ascKey(key)} / {@code .descKey(key)}. */
    public static class SortKeyModel {
        private final CDataEntity key;
        private final boolean ascending;

        public SortKeyModel(CDataEntity key, boolean ascending) {
            this.key = key;
            this.ascending = ascending;
        }

        public boolean isAscending() {
            return ascending;
        }

        public CDataEntity getKey() {
            return key;
        }
    }

    public List<SortKeyModel> getSortKeys() {
        List<SortKeyModel> result = new ArrayList<>();
        for (CEntitySortKey k : sortKey) {
            result.add(new SortKeyModel(k.key, k.bAscending));
        }
        return result;
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
