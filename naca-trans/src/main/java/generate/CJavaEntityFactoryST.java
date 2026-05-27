package generate;

import generate.java.st.*;
import semantic.CBaseEntityFactory;
import semantic.CEntityBloc;
import semantic.CEntityCondition;
import semantic.Verbs.CEntityAddTo;
import semantic.Verbs.CEntityAssign;
import semantic.Verbs.CEntityDisplay;
import semantic.Verbs.CEntityDisplay.Upon;
import semantic.Verbs.CEntityLoopIter;
import semantic.Verbs.CEntityLoopWhile;
import semantic.Verbs.CEntityReadFile;
import utils.CObjectCatalog;
import generate.CBaseLanguageExporter;

public class CJavaEntityFactoryST extends CJavaEntityFactory {

    public CJavaEntityFactoryST(CObjectCatalog cat, CBaseLanguageExporter out) {
        super(cat, out);
    }

    @Override
    public CEntityBloc NewEntityBloc(int l) {
        return new CJavaBlocST(l, programCatalog, langOutput);
    }

    @Override
    public CEntityCondition NewEntityCondition(int l) {
        return new CJavaConditionST(l, programCatalog, langOutput);
    }

    @Override
    public CEntityAssign NewEntityAssign(int l) {
        return new CJavaAssignST(l, programCatalog, langOutput);
    }

    @Override
    public CEntityAddTo NewEntityAddTo(int l) {
        return new CJavaAddToST(l, programCatalog, langOutput);
    }

    @Override
    public CEntityLoopWhile NewEntityLoopWhile(int l) {
        return new CJavaLoopWhileST(l, programCatalog, langOutput);
    }

    @Override
    public CEntityLoopIter NewEntityLoopIter(int l) {
        return new CJavaLoopIterST(l, programCatalog, langOutput);
    }

    @Override
    public CEntityDisplay NewEntityDisplay(int l, Upon t) {
        return new CJavaDisplayST(l, programCatalog, langOutput, t);
    }

    @Override
    public CEntityReadFile NewEntityReadFile(int line) {
        return new CJavaReadFileST(line, programCatalog, langOutput);
    }
}