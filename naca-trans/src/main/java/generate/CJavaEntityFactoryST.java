package generate;

import generate.java.st.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
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
import semantic.CICS.CEntityCICSAskTime;
import semantic.CICS.CEntityCICSAssign;
import semantic.CICS.CEntityCICSDeQ;
import semantic.CICS.CEntityCICSDelay;
import semantic.CICS.CEntityCICSDeleteQ;
import semantic.CICS.CEntityCICSGetMain;
import semantic.CICS.CEntityCICSHandleAID;
import semantic.CICS.CEntityCICSHandleCondition;
import semantic.CICS.CEntityCICSIgnoreCondition;
import semantic.CICS.CEntityCICSInquire;
import semantic.CICS.CEntityCICSEnQ;
import semantic.CICS.CEntityCICSLink;
import semantic.CICS.CEntityCICSReceiveMap;
import semantic.CICS.CEntityCICSReWrite;
import semantic.CICS.CEntityCICSRead;
import semantic.CICS.CEntityCICSReadQ;
import semantic.CICS.CEntityCICSRetrieve;
import semantic.CICS.CEntityCICSSendMap;
import semantic.CICS.CEntityCICSSetTDQueue;
import semantic.CICS.CEntityCICSStart;
import semantic.CICS.CEntityCICSStartBrowse;
import semantic.CICS.CEntityCICSReturn;
import semantic.CICS.CEntityCICSSyncPoint;
import semantic.CICS.CEntityCICSXctl;
import semantic.SQL.CEntityCondIsSQLCode;
import semantic.SQL.CEntitySQLCall;
import semantic.SQL.CEntitySQLCode;
import semantic.SQL.CEntitySQLCloseStatement;
import semantic.SQL.CEntitySQLCommit;
import semantic.SQL.CEntitySQLCursor;
import semantic.SQL.CEntitySQLCursorSelectStatement;
import semantic.SQL.CEntitySQLDeclareTable;
import semantic.SQL.CEntitySQLDeleteStatement;
import semantic.SQL.CEntitySQLExecute;
import semantic.SQL.CEntitySQLFetchStatement;
import semantic.SQL.CEntitySQLInsertStatement;
import semantic.SQL.CEntitySQLLock;
import semantic.SQL.CEntitySQLOpenStatement;
import semantic.SQL.CEntitySQLRollBack;
import semantic.SQL.CEntitySQLSelectStatement;
import semantic.SQL.CEntitySQLSessionDeclare;
import semantic.SQL.CEntitySQLSessionDrop;
import semantic.SQL.CEntitySQLSingleStatement;
import semantic.SQL.CEntitySQLUpdateStatement;
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

    // Embedded SQL DECLARE TABLE: the ST4 factory builds the pure semantic entity,
    // which the recursive assembler renders via the recursiveSQLDeclareTableEntity
    // binding (no CJava* controller). The statement emits no code; its effect is the
    // Stage-1 catalog side effect RegisterSQLTable(csViewName, this) applied in the
    // entity constructor during semantic analysis. Mirrors the WHENEVER exemplar above.
    @Override
    public CEntitySQLDeclareTable NewEntitySQLDeclareTable(int nLine, String csTableName, String csViewName, ArrayList arrTableColDescription) {
        CEntitySQLDeclareTable e = new CEntitySQLDeclareTable(nLine, programCatalog, csTableName, csViewName, arrTableColDescription);
        e.setLanguageExporter(langOutput);
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

    // Embedded CICS ASKTIME: the ST4 factory builds the pure semantic entity, which
    // the recursive assembler renders via the recursiveCICSAskTimeEntity binding
    // (no CJava* controller). Mirrors the CICS ABEND exemplar above.
    @Override
    public CEntityCICSAskTime NewEntityCICSAskTime(int l) {
        CEntityCICSAskTime e = new CEntityCICSAskTime(l, programCatalog);
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

    // Embedded CICS ASSIGN: the ST4 factory builds the pure semantic entity, which
    // the recursive assembler renders via the recursiveCICSAssignEntity binding
    // (no CJava* controller). Mirrors the CICS ADDRESS exemplar above.
    @Override
    public CEntityCICSAssign NewEntityCICSAssign(int l) {
        CEntityCICSAssign e = new CEntityCICSAssign(l, programCatalog);
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

    // Embedded CICS DEQ: the ST4 factory builds the pure semantic entity, which
    // the recursive assembler renders via the recursiveCICSDeQEntity binding
    // (no CJava* controller). Mirrors the CICS ABEND exemplar above.
    @Override
    public CEntityCICSDeQ NewEntityCICSDeQ(int l) {
        CEntityCICSDeQ e = new CEntityCICSDeQ(l, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded CICS ENQ: the ST4 factory builds the pure semantic entity, which
    // the recursive assembler renders via the recursiveCICSEnQEntity binding
    // (no CJava* controller). Mirrors the CICS DEQ exemplar above.
    @Override
    public CEntityCICSEnQ NewEntityCICSEnQ(int l) {
        CEntityCICSEnQ e = new CEntityCICSEnQ(l, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded CICS DELAY: the ST4 factory builds the pure semantic entity, which
    // the recursive assembler renders via the recursiveCICSDelayEntity binding
    // (no CJava* controller). Mirrors the CICS DEQ exemplar above.
    @Override
    public CEntityCICSDelay NewEntityCICSDelay(int l) {
        CEntityCICSDelay e = new CEntityCICSDelay(l, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded CICS DELETEQ: the ST4 factory builds the pure semantic entity, which
    // the recursive assembler renders via the recursiveCICSDeleteQEntity binding
    // (no CJava* controller). Mirrors the CICS DELAY exemplar above.
    @Override
    public CEntityCICSDeleteQ NewEntityCICSDeleteQ(int l, boolean b) {
        CEntityCICSDeleteQ e = new CEntityCICSDeleteQ(l, programCatalog, b);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded CICS GETMAIN: the ST4 factory builds the pure semantic entity, which
    // the recursive assembler renders via the recursiveCICSGetMainEntity binding
    // (no CJava* controller). Mirrors the CICS DELETEQ exemplar above.
    @Override
    public CEntityCICSGetMain NewEntityCICSGetMain(int l) {
        CEntityCICSGetMain e = new CEntityCICSGetMain(l, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded CICS HANDLE AID: the ST4 factory builds the pure semantic entity,
    // which the recursive assembler renders via the recursiveCICSHandleAIDEntity
    // binding (no CJava* controller). Mirrors the CICS GETMAIN exemplar above.
    @Override
    public CEntityCICSHandleAID NewEntityCICSHandleAID(int l) {
        CEntityCICSHandleAID e = new CEntityCICSHandleAID(l, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded CICS HANDLE CONDITION: the ST4 factory builds the pure semantic
    // entity, which the recursive assembler renders via the
    // recursiveCICSHandleConditionEntity binding (no CJava* controller). Mirrors
    // the CICS HANDLE AID exemplar above.
    @Override
    public CEntityCICSHandleCondition NewEntityCICSHandleCondition(int l) {
        CEntityCICSHandleCondition e = new CEntityCICSHandleCondition(l, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded CICS IGNORE CONDITION: the ST4 factory builds the pure semantic
    // entity, which the recursive assembler renders via the
    // recursiveCICSIgnoreConditionEntity binding (no CJava* controller). Mirrors
    // the CICS HANDLE CONDITION exemplar above.
    @Override
    public CEntityCICSIgnoreCondition NewEntityCICSIgnoreCondition(int l) {
        CEntityCICSIgnoreCondition e = new CEntityCICSIgnoreCondition(l, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded CICS INQUIRE: the ST4 factory builds the pure semantic entity,
    // which the recursive assembler renders via the recursiveCICSInquireEntity
    // binding (no CJava* controller). Mirrors the CICS IGNORE CONDITION
    // exemplar above.
    @Override
    public CEntityCICSInquire NewEntityCICSInquire(int l) {
        CEntityCICSInquire e = new CEntityCICSInquire(l, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntityCICSReWrite NewEntityCICSReWrite(int l) {
        CEntityCICSReWrite e = new CEntityCICSReWrite(l, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntityCICSRead NewEntityCICSRead(int l, CEntityCICSRead.CEntityCICSReadMode mode) {
        CEntityCICSRead e = new CEntityCICSRead(l, programCatalog, mode);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntityCICSReadQ NewEntityCICSReadQ(int l, boolean persistent) {
        CEntityCICSReadQ e = new CEntityCICSReadQ(l, programCatalog, persistent);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntityCICSRetrieve NewEntityCICSRetreive(int l, boolean pointer) {
        CEntityCICSRetrieve e = new CEntityCICSRetrieve(l, programCatalog, pointer);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntityCICSSetTDQueue NewEntityCICSSetTDQueue(int l) {
        CEntityCICSSetTDQueue e = new CEntityCICSSetTDQueue(l, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntityCICSStart NewEntityCICSStart(int l, CDataEntity transID) {
        CEntityCICSStart e = new CEntityCICSStart(l, programCatalog, transID);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntityCICSStartBrowse NewEntityCICSStartBrowse(int l) {
        CEntityCICSStartBrowse e = new CEntityCICSStartBrowse(l, programCatalog);
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

    // Embedded SQL CLOSE <cursor>: the ST4 factory builds the pure semantic entity,
    // which the recursive assembler renders via the recursiveSQLCloseStatementEntity
    // binding (no CJava* controller). The cursor remains a semantic child and is
    // recursively rendered through its reference binding; the SQLWARNING/SQLERROR
    // clause is a read-only catalog lookup the template chains onto cursorClose(...).
    @Override
    public CEntitySQLCloseStatement NewEntitySQLCloseStatement(int nLine, CEntitySQLCursor cur) {
        CEntitySQLCloseStatement e = new CEntitySQLCloseStatement(nLine, programCatalog, cur);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded SQL OPEN <cursor>: the ST4 factory builds the pure semantic entity,
    // which the recursive assembler renders via the recursiveSQLOpenStatementEntity
    // binding (no CJava* controller). The cursor and the optional USING
    // descriptor/host variable remain semantic children and are recursively rendered
    // through their reference bindings; a cursor with a bound SELECT renders that
    // SELECT child in place of the OPEN statement; the SQLWARNING/SQLERROR clause is
    // a read-only catalog lookup the template chains onto cursorOpen(...). Mirrors
    // the SQL CLOSE exemplar above.
    @Override
    public CEntitySQLOpenStatement NewEntitySQLOpenStatement(int nLine, CEntitySQLCursor cur) {
        CEntitySQLOpenStatement e = new CEntitySQLOpenStatement(nLine, programCatalog, cur);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded SQL FETCH <cursor> INTO :host [, :ind] ...: the ST4 factory builds
    // the pure semantic entity, which the recursive assembler renders via the
    // recursiveSQLFetchStatementEntity binding (no CJava* controller). The cursor,
    // every INTO target and every optional INDICATOR remain semantic children and
    // are recursively rendered through their reference bindings; the
    // SQLWARNING/SQLERROR clause is a read-only catalog lookup the template chains
    // onto cursorFetch(...). Mirrors the SQL CLOSE / SQL CALL exemplars above.
    @Override
    public CEntitySQLFetchStatement NewEntitySQLFetchStatement(int nLine, CEntitySQLCursor cur) {
        CEntitySQLFetchStatement e = new CEntitySQLFetchStatement(nLine, programCatalog, cur);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded SQL CALL <program> [USING (:host, ...)]: the ST4 factory builds the
    // pure semantic entity, which the recursive assembler renders via the
    // recursiveSQLCallEntity binding (no CJava* controller). The called program
    // reference and each host-variable parameter remain semantic children and are
    // recursively rendered through their reference bindings; the SQLWARNING/SQLERROR
    // clause is a read-only catalog lookup the template chains onto sqlCall(...).
    // Mirrors the SQL CLOSE exemplar above.
    @Override
    public CEntitySQLCall NewEntitySQLCall(int line) {
        CEntitySQLCall e = new CEntitySQLCall(line, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded SQL cursor SELECT (the SELECT bound to a DECLARE CURSOR, rendered
    // in place of the OPEN <cursor> statement): the ST4 factory builds the pure
    // semantic entity, which the recursive assembler renders via the
    // recursiveSQLCursorSelectStatementEntity binding (no CJava* controller). The
    // cursor handle and each host-variable parameter remain semantic children and
    // are recursively rendered through their reference bindings; the
    // SQLWARNING/SQLERROR clause is a read-only catalog lookup the template chains
    // onto cursorOpen(...). Mirrors the SQL CALL exemplar above.
    @Override
    public CEntitySQLCursorSelectStatement NewEntitySQLCursorSelectStatement(int line) {
        CEntitySQLCursorSelectStatement e = new CEntitySQLCursorSelectStatement(line, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded SQL DELETE FROM ... (EXEC SQL DELETE ... END-EXEC): the ST4 factory
    // builds the pure semantic entity, which the recursive assembler renders via the
    // recursiveSQLDeleteStatementEntity binding (no CJava* controller). The bound
    // cursor and each host-variable parameter remain semantic children and are
    // recursively rendered through their reference bindings; the SQLWARNING/SQLERROR
    // clause is a read-only catalog lookup the template chains onto the
    // sql(...)/cursorDeleteCurrent(...) runtime call. Mirrors the SQL cursor SELECT
    // exemplar above.
    @Override
    public CEntitySQLDeleteStatement NewEntitySQLDeleteStatement(int nLine, String csStatement, Vector<CDataEntity> arrParameters) {
        CEntitySQLDeleteStatement e = new CEntitySQLDeleteStatement(nLine, programCatalog, csStatement, arrParameters);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded SQL SELECT ... INTO :host [, :ind] ... (EXEC SQL SELECT ... END-EXEC),
    // the standalone non-cursor form: the ST4 factory builds the pure semantic
    // entity, which the recursive assembler renders via the
    // recursiveSQLSelectStatementEntity binding (no CJava* controller). The prepared
    // SELECT text is assembled in Stage 1; every host-variable parameter, every INTO
    // target and every optional INDICATOR remain semantic children recursively
    // rendered through their reference bindings, and the SQLWARNING/SQLERROR clause
    // is a read-only catalog lookup the template chains onto the sql(...) runtime
    // call. Mirrors the SQL DELETE / SQL FETCH exemplars above.
    @Override
    public CEntitySQLSelectStatement NewEntitySQLSelectStatement(int nLine, String csStatement, Vector<CDataEntity> arrParameters, Vector<CDataEntity> arrInto, Vector<CDataEntity> arrInd) {
        CEntitySQLSelectStatement e = new CEntitySQLSelectStatement(nLine, programCatalog, csStatement, arrParameters, arrInto, arrInd);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded SQL UPDATE ... (EXEC SQL UPDATE ... END-EXEC): the ST4 factory builds
    // the pure semantic entity, which the recursive assembler renders via the
    // recursiveSQLUpdateStatementEntity binding (no CJava* controller). The prepared
    // UPDATE text is assembled in Stage 1; every SET host-variable value and every
    // WHERE host-variable parameter remain semantic children recursively rendered
    // through their reference bindings as 1-based .value(N, <ref>) / .param(N, <ref>)
    // calls (the parameters continue the SET values' numbering, i.e. (i+1+sets.size()),
    // exactly as the retired backend did), the optional bound cursor stays a semantic
    // child rendered through its dataReferenceEntity binding (set by the parser via
    // setCursor), and the SQLWARNING/SQLERROR clause is a read-only catalog lookup the
    // template chains onto the sql(...)/cursorUpdateCurrent(...) runtime call. Mirrors
    // the SQL DELETE / SQL SELECT exemplars above.
    @Override
    public CEntitySQLUpdateStatement NewEntitySQLUpdateStatement(int nLine, String csStatement, Vector<CDataEntity> arrSets, Vector<CDataEntity> arrParameters) {
        CEntitySQLUpdateStatement e = new CEntitySQLUpdateStatement(nLine, programCatalog, csStatement, arrSets, arrParameters);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded SQL INSERT INTO ... (EXEC SQL INSERT ... END-EXEC): the ST4 factory
    // builds the pure semantic entity, which the recursive assembler renders via the
    // recursiveSQLInsertStatementEntity binding (no CJava* controller). The full
    // INSERT INTO ... text is assembled in Stage 1 (NUMBER/STRING VALUES inlined as
    // SQL literals, every other value a positional #N marker, or the INSERT ... SELECT
    // clause); the non-inlined VALUES entries and the SELECT host parameters remain
    // semantic children recursively rendered through their reference bindings as
    // 1-based .value(N, <ref>) calls, and the SQLWARNING/SQLERROR clause is a
    // read-only catalog lookup the template chains onto the sql(...) runtime call.
    // Mirrors the SQL DELETE exemplar above.
    @Override
    public CEntitySQLInsertStatement NewEntitySQLInsertStatement(int nLine) {
        CEntitySQLInsertStatement e = new CEntitySQLInsertStatement(nLine, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded SQL EXECUTE IMMEDIATE :<host> (EXEC SQL EXECUTE IMMEDIATE ...
    // END-EXEC): the ST4 factory builds the pure semantic entity, which the
    // recursive assembler renders via the recursiveSQLExecuteEntity binding (no
    // CJava* controller). The host-variable parameter stays a semantic child
    // rendered through its reference binding; the optional WHENEVER
    // SQLWARNING/SQLERROR clause is a read-only catalog lookup the template chains
    // onto the sql("EXECUTE IMMEDIATE #1").param(1, ...) runtime call. Mirrors the
    // SQL COMMIT / SQL CALL exemplars above.
    @Override
    public CEntitySQLExecute NewEntitySQLExecute(int line) {
        CEntitySQLExecute e = new CEntitySQLExecute(line, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded SQL SQLCODE condition (IF SQL-CODE = n / <> n): the ST4 factory
    // builds the pure semantic entity, which the recursive assembler renders via
    // the recursiveCondIsSQLCodeEntity binding (no CJava* controller). The
    // addImportDeclaration("SQL") Stage-1 side effect (emits
    // 'import nacaLib.sqlSupport.* ;' for the SQLCode.* constants the template
    // references) is preserved from the retired direct backend's factory method,
    // exactly as registerSqlWheneverPolicy is preserved above. Mirrors the
    // SQL CLOSE exemplar.
    @Override
    public CEntityCondIsSQLCode NewEntityCondIsSQLCode() {
        programCatalog.addImportDeclaration("SQL");
        return new CEntityCondIsSQLCode();
    }

    // Embedded SQL SQLCODE / SQLERRD data reference: the ST4 factory builds the
    // pure semantic entity, which the recursive assembler renders via the
    // recursiveSQLCodeEntity binding (no CJava* controller). The entity carries
    // its own getSQLCode()/getSQLDiagnosticCode(...) reference protocol, so no
    // Stage-1 import side effect is needed here. Mirrors the SQLCODE condition
    // exemplar above.
    @Override
    public CEntitySQLCode NewEntitySQLCode(String name) {
        CEntitySQLCode e = new CEntitySQLCode(name, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    @Override
    public CEntitySQLCode NewEntitySQLCode(String name, CBaseEntityExpression eHistoryItem) {
        CEntitySQLCode e = new CEntitySQLCode(name, programCatalog, eHistoryItem);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded SQL COMMIT (EXEC SQL COMMIT END-EXEC): the ST4 factory builds the
    // pure semantic entity, which the recursive assembler renders via the
    // recursiveSQLCommitEntity binding (no CJava* controller). The optional
    // WHENEVER SQLWARNING/SQLERROR clause is a read-only catalog lookup the
    // template chains onto sqlCommit(...). Mirrors the SQL CLOSE / SQL CALL
    // exemplars above.
    @Override
    public CEntitySQLCommit NewEntitySQLCommit(int l) {
        CEntitySQLCommit e = new CEntitySQLCommit(l, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded SQL LOCK TABLE <table> IN EXCLUSIVE MODE (EXEC SQL LOCK TABLE ...
    // END-EXEC): the ST4 factory builds the pure semantic entity, which the
    // recursive assembler renders via the recursiveSQLLockEntity binding (no CJava*
    // controller). The full "LOCK TABLE <table> IN EXCLUSIVE MODE" text is
    // assembled in Stage 1 and the optional WHENEVER SQLWARNING/SQLERROR clause is
    // a read-only catalog lookup the template chains onto sql(...). Mirrors the
    // SQL COMMIT exemplar above.
    @Override
    public CEntitySQLLock NewEntitySQLLock(int l) {
        CEntitySQLLock e = new CEntitySQLLock(l, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded SQL ROLLBACK (EXEC SQL ROLLBACK END-EXEC): the ST4 factory builds the
    // pure semantic entity, which the recursive assembler renders via the
    // recursiveSQLRollBackEntity binding (no CJava* controller). The optional
    // WHENEVER SQLWARNING/SQLERROR clause is a read-only catalog lookup the
    // template chains onto sqlRollback(...). Mirrors the SQL COMMIT exemplar above.
    @Override
    public CEntitySQLRollBack NewEntitySQLRollBack(int l) {
        CEntitySQLRollBack e = new CEntitySQLRollBack(l, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded SQL session DECLARE (the DECLARE GLOBAL TEMPORARY TABLE statement
    // assembled token-by-token by the parser): the ST4 factory builds the pure
    // semantic entity, which the recursive assembler renders via the
    // recursiveSQLSessionDeclareEntity binding (no CJava* controller). The full
    // statement text is carried by the entity (set by the parser via setSql) and
    // the optional WHENEVER SQLWARNING/SQLERROR clause is a read-only catalog
    // lookup the template chains onto sql(...). Mirrors the SQL LOCK exemplar
    // above.
    @Override
    public CEntitySQLSessionDeclare NewEntitySQLSessionDeclare(int line) {
        CEntitySQLSessionDeclare e = new CEntitySQLSessionDeclare(line, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded SQL session DROP (the DROP ... statement assembled token-by-token
    // by the parser): the ST4 factory builds the pure semantic entity, which the
    // recursive assembler renders via the recursiveSQLSessionDropEntity binding
    // (no CJava* controller). The full statement text is carried by the entity
    // (set by the parser via setSql) and the optional WHENEVER SQLWARNING/SQLERROR
    // clause is a read-only catalog lookup the template chains onto sql(...).
    // Mirrors the SQL session DECLARE exemplar above.
    @Override
    public CEntitySQLSessionDrop NewEntitySQLSessionDrop(int line) {
        CEntitySQLSessionDrop e = new CEntitySQLSessionDrop(line, programCatalog);
        e.setLanguageExporter(langOutput);
        return e;
    }

    // Embedded SQL single statement (a raw EXEC SQL <text> END-EXEC without a
    // dedicated statement entity): the ST4 factory builds the pure semantic
    // entity, which the recursive assembler renders via the
    // recursiveSQLSingleStatementEntity binding (no CJava* controller). The raw
    // statement text is carried by the entity (constructor argument) and the
    // template wraps it verbatim in the legacy getDBConnection().execSQL(...)
    // call; no WHENEVER SQLWARNING/SQLERROR clause is chained, exactly as the
    // retired backend. Mirrors the SQL session DROP exemplar above.
    @Override
    public CEntitySQLSingleStatement NewEntitySQLSingleStatement(int l, String st) {
        CEntitySQLSingleStatement e = new CEntitySQLSingleStatement(l, programCatalog, st);
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
