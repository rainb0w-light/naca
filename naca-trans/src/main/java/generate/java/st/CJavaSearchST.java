package generate.java.st;

import generate.CBaseLanguageExporter;
import generate.templates.TemplateLoader;
import semantic.CDataEntity;
import semantic.Verbs.CEntitySearch;
import utils.CObjectCatalog;

/**
 * ST4-based implementation of CEntitySearch (SEARCH).
 * Renders through the recursive assembler (CEntitySearch=recursiveSearchEntity).
 * Mirrors the direct generator: a {@code for} loop scanning the table occurrences
 * with a Search-Found flag, the WHEN body as children, and an optional ELSE bloc.
 */
public class CJavaSearchST extends CEntitySearch {
    public CJavaSearchST(int l, CObjectCatalog cat, CBaseLanguageExporter out) {
        super(l, cat);
        setLanguageExporter(out);
    }

    public CDataEntity getVariable() {
        return eVariable;
    }

    public CDataEntity getIndex() {
        return eIndex;
    }

    public semantic.CEntityBloc getElseBloc() {
        return blocElse;
    }

    /** Formatted name of the synthetic Search-Found flag. */
    public String getSearchFoundFlag() {
        return FormatIdentifier("Search-Found");
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
