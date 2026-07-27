package generate;

import generate.java.st.*;
import java.util.List;
import semantic.CEntityBloc;
import semantic.CEntityCondition;
import semantic.CEntityProcedure;
import semantic.CEntityProcedureDivision;
import semantic.CEntityProcedureSection;
import semantic.CBaseEntityFactory;
import semantic.CDataEntity;
import semantic.CEntitySQLCursorSection;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityIntrinsicFunction;
import semantic.Verbs.CEntityAddTo;
import semantic.Verbs.CEntityAssign;
import semantic.Verbs.CEntityAssignWithAccessor;
import semantic.Verbs.CEntityBreak;
import semantic.Verbs.CEntityCalcul;
import semantic.Verbs.CEntityCallFunction;
import semantic.Verbs.CEntityCallProgram;
import semantic.Verbs.CEntityCase;
import semantic.Verbs.CEntityCloseFile;
import semantic.Verbs.CEntityContinue;
import semantic.Verbs.CEntityDisplay;
import semantic.Verbs.CEntityDisplay.Upon;
import semantic.Verbs.CEntityDivide;
import semantic.Verbs.CEntityGoto;
import semantic.Verbs.CEntityInitialize;
import semantic.Verbs.CEntityLoopIter;
import semantic.Verbs.CEntityLoopWhile;
import semantic.Verbs.CEntityMultiply;
import semantic.Verbs.CEntityOpenFile;
import semantic.Verbs.CEntityReadFile;
import semantic.Verbs.CEntityReturn;
import semantic.Verbs.CEntitySubtractTo;
import semantic.Verbs.CEntityWriteFile;
import semantic.Verbs.CEntityAccept;
import semantic.Verbs.CEntityParseString;
import semantic.Verbs.CEntityReplace;
import semantic.Verbs.CEntitySetConstant;
import semantic.Verbs.CEntityCount;
import semantic.Verbs.CEntitySearch;
import semantic.Verbs.CEntitySort;
import semantic.Verbs.CEntitySortRelease;
import semantic.Verbs.CEntitySortReturn;
import semantic.Verbs.CEntityRewriteFile;
import semantic.Verbs.CEntityInspectConverting;
import semantic.Verbs.CEntityRoutineEmulationCall;
import semantic.Verbs.CEntityStringConcat;
import semantic.Verbs.CEntitySwitchCase;
import semantic.Verbs.CEntityNextSentence;
import semantic.Verbs.CEntityExec;
import semantic.CICS.CEntityCICSAbend;
import semantic.CICS.CEntityCICSAddress;
import semantic.CICS.CEntityCICSLink;
import semantic.CICS.CEntityCICSReceiveMap;
import semantic.CICS.CEntityCICSSendMap;
import semantic.CICS.CEntityCICSReturn;
import semantic.CICS.CEntityCICSSyncPoint;
import semantic.CICS.CEntityCICSXctl;
import semantic.SQL.CEntitySqlOnErrorGoto;
import utils.CObjectCatalog;
import generate.CBaseLanguageExporter;

public class CJavaEntityFactoryST extends CJavaEntityFactory {

    public CJavaEntityFactoryST(CObjectCatalog cat, CBaseLanguageExporter out) {
        super(cat, out);
    }

    @Override
    public CEntityBloc NewEntityBloc(int l) {
        return new CEntityBloc(l, programCatalog);
    }

    @Override
    public CEntityCondition NewEntityCondition(int l) {
        return new CEntityCondition(l, programCatalog);
    }

    @Override
    public CEntityAssign NewEntityAssign(int l) {
        return new CJavaAssignST(l, programCatalog, langOutput);
    }

    @Override
    public CEntityAddTo NewEntityAddTo(int l) {
        return new CEntityAddTo(l, programCatalog);
    }

    @Override
    public CEntityLoopWhile NewEntityLoopWhile(int l) {
        return new CEntityLoopWhile(l, programCatalog);
    }

    @Override
    public CEntityLoopIter NewEntityLoopIter(int l) {
        return new CEntityLoopIter(l, programCatalog);
    }

    @Override
    public CEntityIntrinsicFunction NewEntityIntrinsicFunction(String functionName, List<CBaseEntityExpression> arguments) {
        return new CJavaIntrinsicFunctionST(programCatalog, langOutput, functionName, arguments);
    }

    @Override
    public CEntityDisplay NewEntityDisplay(int l, Upon t) {
        return new CJavaDisplayST(l, programCatalog, langOutput, t);
    }

    @Override
    public CEntityReadFile NewEntityReadFile(int line) {
        CEntityReadFile e = new CEntityReadFile(line, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded CICS RETURN: the ST4 factory builds the pure semantic entity, which
    // the recursive assembler renders via the recursiveCICSReturnEntity binding
    // (no CJava* controller). Mirrors the READ exemplar above.
    @Override
    public CEntityCICSReturn NewEntityCICSReturn(int l) {
        CEntityCICSReturn e = new CEntityCICSReturn(l, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded CICS SYNCPOINT: the ST4 factory builds the pure semantic entity, which
    // the recursive assembler renders via the recursiveCICSSyncPointEntity binding
    // (no CJava* controller). Mirrors the CICS RETURN exemplar above.
    @Override
    public CEntityCICSSyncPoint NewEntityCICSSyncPoint(int l, boolean bRollBack) {
        CEntityCICSSyncPoint e = new CEntityCICSSyncPoint(l, programCatalog, bRollBack);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded SQL WHENEVER SQLERROR: the ST4 factory builds the pure semantic entity,
    // which the recursive assembler renders via the recursiveSqlOnErrorGotoEntity
    // binding (no CJava* controller). The WHENEVER policy is a Stage-1 catalog side
    // effect (registerSqlWheneverPolicy, inherited from CJavaEntityFactory) applied
    // here in program order; the template emits no code. Mirrors the CICS RETURN exemplar.
    @Override
    public CEntitySqlOnErrorGoto NewEntitySQLOnErrorGoto(int l, String ref) {
        CEntitySqlOnErrorGoto e = new CEntitySqlOnErrorGoto(l, programCatalog, ref, false);
        e.setLanguageExporter(langOutput);
        registerSqlWheneverPolicy(ref, false);
        return e;
    }

    // Embedded SQL WHENEVER SQLWARNING: same as above with the warning policy.
    @Override
    public CEntitySqlOnErrorGoto NewEntitySQLOnWarningGoto(int l, String ref) {
        CEntitySqlOnErrorGoto e = new CEntitySqlOnErrorGoto(l, programCatalog, ref, true);
        e.setLanguageExporter(langOutput);
        registerSqlWheneverPolicy(ref, true);
        return e;
    }

    // Embedded CICS XCTL: the ST4 factory builds the pure semantic entity, which
    // the recursive assembler renders via the recursiveCICSXctlEntity binding
    // (no CJava* controller). Mirrors the CICS RETURN exemplar above.
    @Override
    public CEntityCICSXctl NewEntityCICSXctl(int l) {
        CEntityCICSXctl e = new CEntityCICSXctl(l, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded CICS LINK: the ST4 factory builds the pure semantic entity, which
    // the recursive assembler renders via the recursiveCICSLinkEntity binding
    // (no CJava* controller). Mirrors the CICS XCTL exemplar above.
    @Override
    public CEntityCICSLink NewEntityCICSLink(int l) {
        CEntityCICSLink e = new CEntityCICSLink(l, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded CICS ABEND: the ST4 factory builds the pure semantic entity, which
    // the recursive assembler renders via the recursiveCICSAbendEntity binding
    // (no CJava* controller). Mirrors the CICS LINK exemplar above.
    @Override
    public CEntityCICSAbend NewEntityCICSAbend(int l) {
        CEntityCICSAbend e = new CEntityCICSAbend(l, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded CICS ADDRESS: the ST4 factory builds the pure semantic entity, which
    // the recursive assembler renders via the recursiveCICSAddressEntity binding
    // (no CJava* controller). Mirrors the CICS ABEND exemplar above.
    @Override
    public CEntityCICSAddress NewEntityCICSAddress(int l) {
        CEntityCICSAddress e = new CEntityCICSAddress(l, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded CICS RECEIVE MAP: the ST4 factory builds the pure semantic entity,
    // which the recursive assembler renders via the recursiveCICSReceiveMapEntity
    // binding (no CJava* controller). Mirrors the CICS ADDRESS exemplar above.
    @Override
    public CEntityCICSReceiveMap NewEntityCICSReceiveMap(int l, CDataEntity name) {
        CEntityCICSReceiveMap e = new CEntityCICSReceiveMap(l, programCatalog, name);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded CICS SEND MAP: the ST4 factory builds the pure semantic entity, which
    // the recursive assembler renders via the recursiveCICSSendMapEntity binding
    // (no CJava* controller). Mirrors the CICS RECEIVE MAP exemplar above.
    @Override
    public CEntityCICSSendMap NewEntityCICSSendMap(int l) {
        CEntityCICSSendMap e = new CEntityCICSSendMap(l, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded SQL cursor declaration section: the ST4 factory builds the pure
    // semantic entity, which the recursive assembler renders via the
    // recursiveSQLCursorSectionEntity declaration binding (no CJava* controller).
    @Override
    public CEntitySQLCursorSection NewEntitySQLCursorSection() {
        CEntitySQLCursorSection e = new CEntitySQLCursorSection(programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntitySubtractTo NewEntitySubtractTo(int l) {
        CEntitySubtractTo e = new CEntitySubtractTo(l, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntityMultiply NewEntityMultiply(int l) {
        CEntityMultiply e = new CEntityMultiply(l, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntityDivide NewEntityDivide(int l) {
        CEntityDivide e = new CEntityDivide(l, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntityCalcul NewEntityCalcul(int l) {
        CEntityCalcul e = new CEntityCalcul(l, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntityAssignWithAccessor NewEntityAssignWithAccessor(int l) {
        return new CJavaAssignWithAccessorST(l, programCatalog, langOutput);
    }

    @Override
    public CEntityCase NewEntityCase(int l, int endline) {
        return new CEntityCase(l, programCatalog, endline);
    }

    @Override
    public CEntityGoto NewEntityGoto(int l, String Reference, CEntityProcedureSection section) {
        CEntityGoto e = new CEntityGoto(l, programCatalog, Reference, section);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntityCallFunction NewEntityCallFunction(int l, String reference, String csRefThru, CEntityProcedureSection section) {
        CEntityCallFunction e = new CEntityCallFunction(l, programCatalog, reference, csRefThru, section);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntityCallProgram NewEntityCallProgram(int l, CDataEntity reference) {
        return new CJavaCallProgramST(l, programCatalog, langOutput, reference);
    }

    @Override
    public CEntityInitialize NewEntityInitialize(int l, CDataEntity data) {
        return new CJavaInitializeST(l, programCatalog, langOutput, data);
    }

    @Override
    public CEntityReturn NewEntityReturn(int l) {
        CEntityReturn e = new CEntityReturn(l, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntityWriteFile NewEntityWriteFile(int line) {
        CEntityWriteFile e = new CEntityWriteFile(line, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntityOpenFile NewEntityOpenFile(int line) {
        CEntityOpenFile e = new CEntityOpenFile(line, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntityCloseFile NewEntityCloseFile(int line) {
        CEntityCloseFile e = new CEntityCloseFile(line, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntityContinue NewEntityContinue(int l) {
        return new CEntityContinue(l, programCatalog);
    }

    @Override
    public CEntityBreak NewEntityBreak(int line) {
        return new CEntityBreak(line, programCatalog);
    }

    @Override
    public CEntityAccept NewEntityAccept(int line) {
        CEntityAccept e = new CEntityAccept(line, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntityParseString NewEntityParseString(int line) {
        CEntityParseString e = new CEntityParseString(line, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntityReplace NewEntityReplace(int line) {
        CEntityReplace e = new CEntityReplace(line, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntitySetConstant NewEntitySetConstant(int line) {
        CEntitySetConstant e = new CEntitySetConstant(line, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntityCount NewEntityCount(int line) {
        CEntityCount e = new CEntityCount(line, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntitySearch NewEntitySearch(int line) {
        return new CJavaSearchST(line, programCatalog, langOutput);
    }

    @Override
    public CEntitySort NewEntitySort(int line) {
        return new CJavaSortST(line, programCatalog, langOutput);
    }

    @Override
    public CEntitySortRelease NewEntitySortRelease(int line) {
        CEntitySortRelease e = new CEntitySortRelease(line, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntitySortReturn NewEntitySortReturn(int line) {
        CEntitySortReturn e = new CEntitySortReturn(line, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntityRewriteFile NewEntityRewriteFile(int line) {
        CEntityRewriteFile e = new CEntityRewriteFile(line, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntityInspectConverting NewEntityInspectConverting(int line) {
        CEntityInspectConverting e = new CEntityInspectConverting(line, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntityRoutineEmulationCall NewEntityRoutineEmulationCall(int line) {
        CEntityRoutineEmulationCall e = new CEntityRoutineEmulationCall(line, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntityStringConcat NewEntityStringConcat(int line) {
        CEntityStringConcat e = new CEntityStringConcat(line, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntitySwitchCase NewEntitySwitchCase(int line) {
        return new CEntitySwitchCase(line, programCatalog);
    }

    @Override
    public CEntityNextSentence NewEntityNextSentence(int line) {
        CEntityNextSentence e = new CEntityNextSentence(line, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntityExec NewEntityExec(int l, String statement) {
        CEntityExec e = new CEntityExec(l, programCatalog, statement);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Procedure structure is rendered by the recursive assembler in production:
    // the ST controller renders its whole subtree via JavaTemplateAssembler.
    @Override
    public CEntityProcedureDivision NewEntityProcedureDivision(int l) {
        return new CJavaProcedureDivisionST(l, programCatalog, langOutput);
    }

    @Override
    public CEntityProcedureSection NewEntityProcedureSection(int l, String name) {
        return new CJavaProcedureSectionST(l, name, programCatalog, langOutput);
    }

    @Override
    public CEntityProcedure NewEntityProcedure(int l, String name, CEntityProcedureSection section) {
        return new CJavaProcedureST(l, name, programCatalog, langOutput, section);
    }
}
