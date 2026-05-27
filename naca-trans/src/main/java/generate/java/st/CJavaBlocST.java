package generate.java.st;

import generate.CBaseLanguageExporter;
import semantic.CEntityBloc;
import utils.CObjectCatalog;

public class CJavaBlocST extends CEntityBloc {
    public CJavaBlocST(int l, CObjectCatalog cat, CBaseLanguageExporter out) {
        super(l, cat, out);
    }

    @Override
    protected void DoExport() {
        StartOutputBloc();
        ExportChildren();
        EndOutputBloc();
    }
}
