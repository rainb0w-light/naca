package generate.java.st;

import generate.CBaseLanguageExporter;
import generate.templates.TemplateLoader;
import semantic.CDataEntity;
import semantic.Verbs.CEntityInitialize;
import utils.CObjectCatalog;

/**
 * ST4-based implementation of CEntityInitialize (INITIALIZE).
 * Rendered through the recursive assembler
 * (CEntityInitialize=recursiveInitializeEntity). The REPLACING variants and the
 * data target are exposed as semantic properties; the template only selects
 * the correct runtime call and lets the model adaptor render the references.
 */
public class CJavaInitializeST extends CEntityInitialize {
    public CJavaInitializeST(int l, CObjectCatalog cat, CBaseLanguageExporter out, CDataEntity data) {
        super(l, cat, data);
        setLanguageExporter(out);
    }

    public CDataEntity getData() {
        return data;
    }

    public CDataEntity getFillAlphaWith() {
        return fillAlphaWith;
    }

    public CDataEntity getRepAlphaWith() {
        return repAlphaWith;
    }

    public CDataEntity getRepNumWith() {
        return repNumWith;
    }

    public CDataEntity getRepNumEditedWith() {
        return repNumEditedWith;
    }

    /** SQLCODE is reset through a dedicated runtime call rather than initialize(). */
    public boolean isSqlCodeReset() {
        return data != null && "getSQLCode()".equals(data.ExportReference(getLine()));
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
