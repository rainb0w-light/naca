/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate;

import generate.java.forms.BmsJavaEntities;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import semantic.CBaseEntityFactory;
import semantic.CBaseExternalEntity;
import semantic.CDataEntity;
import semantic.CEntityAddressReference;
import semantic.CEntityArrayReference;
import semantic.CEntityAttribute;
import semantic.CEntityBloc;
import semantic.CEntityClass;
import semantic.CEntityComment;
import semantic.CEntityCondition;
import semantic.CEntityDataSection;
import semantic.CEntityEnvironmentVariable;
import semantic.CEntityExternalDataStructure;
import semantic.CEntityFileDescriptor;
import semantic.CEntityFileDescriptorLengthDependency;
import semantic.CEntityFormatedVarReference;
import semantic.CEntityIndex;
import semantic.CEntityInline;
import semantic.CEntityMoveReference;
import semantic.CEntityNamedCondition;
import semantic.CEntityProcedure;
import semantic.CEntityProcedureDivision;
import semantic.CEntityProcedureSection;
import semantic.CEntitySQLCursorSection;
import semantic.CEntitySortedFileDescriptor;
import semantic.CEntityStructure;
import semantic.CEntityUnknownReference;
import semantic.CSubStringAttributReference;
import semantic.CICS.CEntityCICSAbend;
import semantic.CICS.CEntityCICSAddress;
import semantic.CICS.CEntityCICSAskTime;
import semantic.CICS.CEntityCICSAssign;
import semantic.CICS.CEntityCICSDeQ;
import semantic.CICS.CEntityCICSDelay;
import semantic.CICS.CEntityCICSDeleteQ;
import semantic.CICS.CEntityCICSEnQ;
import semantic.CICS.CEntityCICSGetMain;
import semantic.CICS.CEntityCICSHandleAID;
import semantic.CICS.CEntityCICSHandleCondition;
import semantic.CICS.CEntityCICSIgnoreCondition;
import semantic.CICS.CEntityCICSInquire;
import semantic.CICS.CEntityCICSLink;
import semantic.CICS.CEntityCICSReWrite;
import semantic.CICS.CEntityCICSRead;
import semantic.CICS.CEntityCICSReadQ;
import semantic.CICS.CEntityCICSReceiveMap;
import semantic.CICS.CEntityCICSRetrieve;
import semantic.CICS.CEntityCICSReturn;
import semantic.CICS.CEntityCICSSendMap;
import semantic.CICS.CEntityCICSSetTDQueue;
import semantic.CICS.CEntityCICSStart;
import semantic.CICS.CEntityCICSStartBrowse;
import semantic.CICS.CEntityCICSSyncPoint;
import semantic.CICS.CEntityCICSWrite;
import semantic.CICS.CEntityCICSWriteQ;
import semantic.CICS.CEntityCICSXctl;
import semantic.SQL.CEntityCondIsSQLCode;
import semantic.SQL.CEntitySQLCall;
import semantic.SQL.CEntitySQLCloseStatement;
import semantic.SQL.CEntitySQLCode;
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
import semantic.Verbs.CEntityAccept;
import semantic.Verbs.CEntityAddTo;
import semantic.Verbs.CEntityAssign;
import semantic.Verbs.CEntityAssignSpecial;
import semantic.Verbs.CEntityAssignWithAccessor;
import semantic.Verbs.CEntityBreak;
import semantic.Verbs.CEntityCalcul;
import semantic.Verbs.CEntityCallFunction;
import semantic.Verbs.CEntityCallProgram;
import semantic.Verbs.CEntityCase;
import semantic.Verbs.CEntityCloseFile;
import semantic.Verbs.CEntityContinue;
import semantic.Verbs.CEntityConvertReference;
import semantic.Verbs.CEntityCount;
import semantic.Verbs.CEntityDisplay;
import semantic.Verbs.CEntityDivide;
import semantic.Verbs.CEntityExec;
import semantic.Verbs.CEntityGoto;
import semantic.Verbs.CEntityGotoDepending;
import semantic.Verbs.CEntityInc;
import semantic.Verbs.CEntityInitialize;
import semantic.Verbs.CEntityInspectConverting;
import semantic.Verbs.CEntityLoopIter;
import semantic.Verbs.CEntityLoopWhile;
import semantic.Verbs.CEntityMultiply;
import semantic.Verbs.CEntityNextSentence;
import semantic.Verbs.CEntityOpenFile;
import semantic.Verbs.CEntityParseString;
import semantic.Verbs.CEntityReadFile;
import semantic.Verbs.CEntityReplace;
import semantic.Verbs.CEntityReturn;
import semantic.Verbs.CEntityRewriteFile;
import semantic.Verbs.CEntityRoutineEmulationCall;
import semantic.Verbs.CEntitySearch;
import semantic.Verbs.CEntitySetConstant;
import semantic.Verbs.CEntitySort;
import semantic.Verbs.CEntitySortRelease;
import semantic.Verbs.CEntitySortReturn;
import semantic.Verbs.CEntityStringConcat;
import semantic.Verbs.CEntitySubtractTo;
import semantic.Verbs.CEntitySwitchCase;
import semantic.Verbs.CEntityWriteFile;
import semantic.Verbs.CEntityDisplay.Upon;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityAddress;
import semantic.expression.CEntityAddressOf;
import semantic.expression.CEntityConcat;
import semantic.expression.CEntityCondAnd;
import semantic.expression.CEntityCondCompare;
import semantic.expression.CEntityCondEquals;
import semantic.expression.CEntityCondIsAll;
import semantic.expression.CEntityCondIsBoolean;
import semantic.expression.CEntityCondIsConstant;
import semantic.expression.CEntityCondIsKindOf;
import semantic.expression.CEntityCondNot;
import semantic.expression.CEntityCondOr;
import semantic.expression.CEntityConstant;
import semantic.expression.CEntityConstantValue;
import semantic.expression.CEntityCurrentDate;
import semantic.expression.CEntityDigits;
import semantic.expression.CEntityExprOpposite;
import semantic.expression.CEntityExprProd;
import semantic.expression.CEntityExprSum;
import semantic.expression.CEntityExprTerminal;
import semantic.expression.CEntityFunctionCall;
import semantic.expression.CEntityInternalBool;
import semantic.expression.CEntityIntrinsicFunction;
import semantic.expression.CEntityIsFileEOF;
import semantic.expression.CEntityIsNamedCondition;
import semantic.expression.CEntityLengthOf;
import semantic.expression.CEntityList;
import semantic.expression.CEntityNumber;
import semantic.expression.CEntityString;
import semantic.expression.CEntityConstant.Value;
import semantic.forms.CEntityFieldArrayReference;
import semantic.forms.CEntityFieldAttribute;
import semantic.forms.CEntityFieldColor;
import semantic.forms.CEntityFieldData;
import semantic.forms.CEntityFieldFlag;
import semantic.forms.CEntityFieldHighlight;
import semantic.forms.CEntityFieldLength;
import semantic.forms.CEntityFieldOccurs;
import semantic.forms.CEntityFieldRedefine;
import semantic.forms.CEntityFieldValidated;
import semantic.forms.CEntityFormRedefine;
import semantic.forms.CEntityGetKeyPressed;
import semantic.forms.CEntityIsFieldAttribute;
import semantic.forms.CEntityIsFieldColor;
import semantic.forms.CEntityIsFieldCursor;
import semantic.forms.CEntityIsFieldFlag;
import semantic.forms.CEntityIsFieldHighlight;
import semantic.forms.CEntityIsFieldModified;
import semantic.forms.CEntityIsKeyPressed;
import semantic.forms.CEntityKeyPressed;
import semantic.forms.CEntityResetKeyPressed;
import semantic.forms.CEntityResourceField;
import semantic.forms.CEntityResourceFieldArray;
import semantic.forms.CEntityResourceForm;
import semantic.forms.CEntityResourceFormContainer;
import semantic.forms.CEntitySetAttribute;
import semantic.forms.CEntitySetColor;
import semantic.forms.CEntitySetCursor;
import semantic.forms.CEntitySetFlag;
import semantic.forms.CEntitySetHighligh;
import semantic.forms.CEntitySkipFields;
import semantic.forms.CResourceStrings;
import utils.CGlobalCatalog;
import utils.CObjectCatalog;
import utils.NacaTransAssertException;



/**
 * @author sly
 *
 */
public class CJavaEntityFactory extends CBaseEntityFactory
{

    /** Executes the init custom global entities operation. */
    public void InitCustomGlobalEntities(CGlobalCatalog cat)
    {
        // manage HEXZONE
        CObjectCatalog ocat = new CObjectCatalog(cat, null, null, null) ;
        CEntityExternalDataStructure structure =
            new CEntityExternalDataStructure(0, "HEXZONE", ocat);
        structure.SetInline(true) ;
        CEntityAttribute att1 = new CEntityAttribute(0, "HEX-0E04", ocat) ;
        att1.SetTypeString(2) ;
        att1.SetInitialValue(getSpecialConstantValue("\u000E\u009C")) ;
        structure.AddChild(att1) ;
        ocat.RegisterAttribute(att1) ;
        CEntityAttribute att2 = new CEntityAttribute(0, "HEX-FF", ocat) ;
        att2.SetTypeString(1) ;
        att2.SetInitialValue(new CEntityString(ocat, new char[] {'\u00FF'})) ;
        structure.AddChild(att2) ;
        ocat.RegisterAttribute(att2) ;
        CEntityAttribute att3 = new CEntityAttribute(0, "HEX-80", ocat) ;
        att3.SetTypeString(1) ;
        att3.SetInitialValue(new CEntityString(ocat, new char[] {'\u0080'})) ;
        structure.AddChild(att3) ;
        ocat.RegisterAttribute(att3) ;
        cat.RegisterExternalDataStructure(structure) ;
    }

    /** Executes the init custom cicsentities operation. */
    public void InitCustomCICSEntities()
    {
        // Some entries are supplied directly by the runtime environment.
        NewEntitySQLCode("SQLCODE") ;
        NewEntitySQLCode("SQLERRD") ;


    }

    /**
     * @param cat
     */
    public CJavaEntityFactory(CObjectCatalog cat, CBaseLanguageExporter out)    {
        super(cat);
        langOutput = out;
    }

    protected CBaseLanguageExporter langOutput;

    @Override
    public String getOutputDirectory()
    {
        return langOutput == null ? "" : langOutput.getOutputDir();
    }

    /** Creates the entity sqlselect statement. */
    public CEntitySQLSelectStatement NewEntitySQLSelectStatement(
        int nLine,
        String csStatement,
        Vector<CDataEntity> arrParameters,
        Vector<CDataEntity> arrInto,
        Vector<CDataEntity> arrInd) {
        // Direct backend CJavaSQLSelectStatement retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (the recursiveSQLSelectStatementEntity
        // binding). Every host-variable parameter, INTO target and optional INDICATOR
        // remain semantic children rendered through their reference bindings; the
        // SQLWARNING/SQLERROR clause is a read-only catalog lookup the template chains
        // onto the sql(...) runtime call. No generated string is materialized in the
        // factory.
        CEntitySQLSelectStatement e = new CEntitySQLSelectStatement(nLine, programCatalog, csStatement, arrParameters, arrInto, arrInd);
        return e;
    }
    /** Creates the entity sqlcursor select statement. */
    public CEntitySQLCursorSelectStatement NewEntitySQLCursorSelectStatement(int nLine) {
        // Direct backend CJavaSQLCursorSelectStatement retired: the pure semantic
        // entity is rendered by the recursive ST4 assembler (the
        // recursiveSQLCursorSelectStatementEntity binding) in place of the OPEN
        // <cursor> statement. The cursor and each host-variable parameter remain
        // semantic children rendered through their reference bindings; no generated
        // string is materialized in the factory.
        CEntitySQLCursorSelectStatement e = new CEntitySQLCursorSelectStatement(nLine, programCatalog);
        return e;
    }
    /** Creates the entity sqlfetch statement. */
    public CEntitySQLFetchStatement NewEntitySQLFetchStatement(int nLine, CEntitySQLCursor cur) {
        // Direct backend CJavaSQLFetchStatement retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (the recursiveSQLFetchStatementEntity
        // binding). The cursor, every INTO target and every optional INDICATOR remain
        // semantic children rendered through their reference bindings; the
        // SQLWARNING/SQLERROR clause is a read-only catalog lookup the template chains
        // onto cursorFetch(...). No generated string is materialized in the factory.
        CEntitySQLFetchStatement e = new CEntitySQLFetchStatement(nLine, programCatalog, cur);
        return e;
    }
    /** Creates the entity sqlopen statement. */
    public CEntitySQLOpenStatement NewEntitySQLOpenStatement(int nLine, CEntitySQLCursor cur)   {
        // Direct backend CJavaSQLOpenStatement retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (the recursiveSQLOpenStatementEntity
        // binding). The cursor and the optional USING descriptor/host variable remain
        // semantic children rendered through their reference bindings; a cursor with a
        // bound SELECT renders that SELECT child in place of the OPEN statement; the
        // SQLWARNING/SQLERROR clause is a read-only catalog lookup the template chains
        // onto cursorOpen(...). No generated string is materialized in the factory.
        CEntitySQLOpenStatement e = new CEntitySQLOpenStatement(nLine, programCatalog, cur);
        return e;
    }
    /** Creates the entity sqlclose statement. */
    public CEntitySQLCloseStatement NewEntitySQLCloseStatement(int nLine, CEntitySQLCursor cur) {
        // Direct backend CJavaSQLCloseStatement retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler. The cursor remains a semantic child
        // and is recursively rendered through its reference binding; no generated
        // string is materialized in the factory.
        CEntitySQLCloseStatement e = new CEntitySQLCloseStatement(nLine, programCatalog, cur);
        return e;
    }
    /** Creates the entity sqldelete statement. */
    public CEntitySQLDeleteStatement NewEntitySQLDeleteStatement(int nLine, String csStatement, Vector<CDataEntity> arrParameters)  {
        // Direct backend CJavaSQLDeleteStatement retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (the recursiveSQLDeleteStatementEntity
        // binding). The bound cursor and each host-variable parameter remain semantic
        // children rendered through their reference bindings; the SQLWARNING/SQLERROR
        // clause is a read-only catalog lookup the template chains onto the
        // sql(...)/cursorDeleteCurrent(...) runtime call. No generated string is
        // materialized in the factory.
        CEntitySQLDeleteStatement e = new CEntitySQLDeleteStatement(nLine, programCatalog, csStatement, arrParameters);
        return e;
    }
    /** Creates the entity sqlupdate statement. */
    public CEntitySQLUpdateStatement NewEntitySQLUpdateStatement(
        int nLine,
        String csStatement,
        Vector<CDataEntity> arrSets,
        Vector<CDataEntity> arrParameters) {
        // Direct backend CJavaSQLUpdateStatement retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (the recursiveSQLUpdateStatementEntity
        // binding). The prepared UPDATE text is assembled in Stage 1; the SET
        // host-variable values and the WHERE host-variable parameters remain semantic
        // children rendered through their reference bindings as 1-based .value(N, <ref>)
        // / .param(N, <ref>) calls (the parameters continue the SET values' numbering),
        // the optional bound cursor stays a semantic child rendered through its
        // dataReferenceEntity binding (set by the parser via setCursor), and the
        // SQLWARNING/SQLERROR clause is a read-only catalog lookup the template chains
        // onto the sql(...)/cursorUpdateCurrent(...) runtime call. No generated string
        // is materialized in the factory.
        CEntitySQLUpdateStatement e = new CEntitySQLUpdateStatement(nLine, programCatalog, csStatement, arrSets, arrParameters);
        return e;
    }
    /** Creates the entity sqlinsert statement. */
    public CEntitySQLInsertStatement NewEntitySQLInsertStatement(int nLine) {
        // Direct backend CJavaSQLInsertStatement retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (the recursiveSQLInsertStatementEntity
        // binding). The full INSERT INTO ... text is assembled in Stage 1; the
        // non-inlined VALUES entries and the INSERT...SELECT host parameters remain
        // semantic children rendered through their reference bindings as 1-based
        // .value(N, <ref>) calls, and the SQLWARNING/SQLERROR clause is a read-only
        // catalog lookup the template chains onto the sql(...) runtime call. No
        // generated string is materialized in the factory.
        CEntitySQLInsertStatement e = new CEntitySQLInsertStatement(nLine, programCatalog);
        return e;
    }
    /** Creates the entity sqldeclare table. */
    public CEntitySQLDeclareTable NewEntitySQLDeclareTable(
        int nLine,
        String csTableName,
        String csViewName,
        ArrayList arrTableColDescription)  {
        // Direct backend CJavaSQLDeclareTable retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (recursiveSQLDeclareTableEntity
        // binding). The statement emits no code; its effect is the Stage-1 catalog
        // side effect RegisterSQLTable(csViewName, this) applied in the entity
        // constructor during semantic analysis. Mirrors the CEntitySqlOnErrorGoto
        // (WHENEVER) retirement.
        CEntitySQLDeclareTable e = new CEntitySQLDeclareTable(nLine, programCatalog, csTableName, csViewName, arrTableColDescription);
        return e;
    }
    /** Creates the entity class. */
    public CEntityClass NewEntityClass(int l, String name)  {
        CEntityClass e = new CEntityClass(l, name, programCatalog);
        return e;
    }
    /** Creates the entity comment. */
    public CEntityComment NewEntityComment(int l, String comment)   {
        CEntityComment e = new CEntityComment(l, programCatalog, comment);
        return e;
    }
    /** Creates the entity attribute. */
    public CEntityAttribute NewEntityAttribute(int l, String name)  {
        CEntityAttribute e = new CEntityAttribute(l, name, programCatalog);
        return e;
    }
    /** Creates the entity structure. */
    public CEntityStructure NewEntityStructure(int l, String name, String level)    {
        CEntityStructure entity =
            new CEntityStructure(l, name, programCatalog, level);
        return entity;
    }
    /** Creates the entity procedure. */
    public CEntityProcedure NewEntityProcedure(int l, String name, CEntityProcedureSection section) {
        CEntityProcedure entity =
            new CEntityProcedure(l, name, programCatalog, section);
        return entity;
    }
    /** Creates the entity procedure section. */
    public CEntityProcedureSection NewEntityProcedureSection(int l, String name)    {
        CEntityProcedureSection entity =
            new CEntityProcedureSection(l, name, programCatalog);
        return entity;
    }
    /** Creates the entity assign. */
    public CEntityAssign NewEntityAssign(int l) {
        CEntityAssign e = new CEntityAssign(l, programCatalog);
        return e;
    }
    /** Creates the entity external data structure. */
    public CEntityExternalDataStructure NewEntityExternalDataStructure(int l, String name)  {
        CEntityExternalDataStructure e =
            new CEntityExternalDataStructure(l, name, programCatalog);
        return e;
    }
    /** Creates the entity inline. */
    public CEntityInline NewEntityInline(int l, CBaseExternalEntity ext)    {
        CEntityInline entity = new CEntityInline(l, programCatalog, ext);
        programCatalog.RegisterExternalDataStructure(ext);
        return entity;
    }
    /** Creates the entity condition. */
    public CEntityCondition NewEntityCondition(int l)   {
        return new CEntityCondition(l, programCatalog);
    }
    /** Creates the entity bloc. */
    public CEntityBloc NewEntityBloc(int l) {
        return new CEntityBloc(l, programCatalog);
    }
    /** Creates the entity calcul. */
    public CEntityCalcul NewEntityCalcul(int l) {
        CEntityCalcul e = new CEntityCalcul(l, programCatalog);
        return e;
    }
    /** Creates the entity sqlon error goto. */
    public CEntitySqlOnErrorGoto NewEntitySQLOnErrorGoto(int l, String ref) {
        // Direct backend CJavaSqlOnErrorGoto retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (recursiveSqlOnErrorGotoEntity
        // binding). The WHENEVER policy is a Stage-1 catalog side effect registered
        // here, in program order, during semantic analysis; the template emits no code.
        CEntitySqlOnErrorGoto e = new CEntitySqlOnErrorGoto(l, programCatalog, ref, false) ;
        registerSqlWheneverPolicy(ref, false);
        return e;
    }
    /** Creates the entity sqlon warning goto. */
    public CEntitySqlOnErrorGoto NewEntitySQLOnWarningGoto(int l, String ref)   {
        // Direct backend CJavaSqlOnErrorGoto retired (see NewEntitySQLOnErrorGoto).
        CEntitySqlOnErrorGoto e = new CEntitySqlOnErrorGoto(l, programCatalog, ref, true) ;
        registerSqlWheneverPolicy(ref, true);
        return e;
    }
    /**
     * Stage-1 side effect of {@code EXEC SQL WHENEVER ...}: records the SQLERROR /
     * SQLWARNING policy (continue vs goto + formatted target label) into the catalog
     * so the SQL statement backends can append the matching
     * {@code .onErrorGoto(...)}/{@code .onErrorContinue()} runtime clause. Moved out
     * of the retired direct backend's DoExport so it runs during semantic analysis
     * (program order) on both the default and ST4 export paths. The identifier is
     * formatted with the language exporter, exactly as the retired backend did.
     */
    protected void registerSqlWheneverPolicy(String ref, boolean onWarning) {
        if (onWarning)  {
            if (ref.equals("")) {
                programCatalog.registerSQLWarningContinue(null);
            }
            else    {
                programCatalog.registerSQLWarningGoto(langOutput.FormatIdentifier(ref));
            }
        }
        else    {
            if (ref.equals("")) {
                programCatalog.RegisterSQLErrorContinue(null);
            }
            else    {
                programCatalog.registerSQLErrorGoto(langOutput.FormatIdentifier(ref));
            }
        }
    }
    /** Creates the entity exec. */
    public CEntityExec NewEntityExec(int l, String statement)   {
        CEntityExec e = new CEntityExec(l, programCatalog, statement);
        return e;
    }
    /** Creates the entity form container. */
    public CEntityResourceFormContainer NewEntityFormContainer(int l, String name, boolean bSave)   {
        return BmsJavaEntities.formContainer(l, name, programCatalog, langOutput, bSave);
    }
    /** Creates the entity form. */
    public CEntityResourceForm NewEntityForm(int l, String name, boolean bSave) {
        return BmsJavaEntities.form(l, name, programCatalog, langOutput, bSave);
    }
    /** Creates the entity field attribute. */
    public CEntityFieldAttribute NewEntityFieldAttribute(int l, String name, CDataEntity owner) {
        return BmsJavaEntities.fieldAttribute(l, name, programCatalog, langOutput, owner);
    }
    /** Creates the entity call function. */
    public CEntityCallFunction NewEntityCallFunction(int l, String reference, String csRefThru, CEntityProcedureSection section)    {
        CEntityCallFunction e = new CEntityCallFunction(
            l, programCatalog, reference, csRefThru, section);
        return e;
    }
    /** Creates the entity initialize. */
    public CEntityInitialize NewEntityInitialize(int l, CDataEntity data)   {
        CEntityInitialize e = new CEntityInitialize(l, programCatalog, data);
        return e;
    }
    /** Creates the entity return. */
    public CEntityReturn NewEntityReturn(int l) {
        CEntityReturn e = new CEntityReturn(l, programCatalog);
        return e;
    }
    /** Creates the entity call program. */
    public CEntityCallProgram NewEntityCallProgram(int l, CDataEntity reference)    {
        CEntityCallProgram e = new CEntityCallProgram(l, programCatalog, reference);
        return e;
    }
    /** Creates the entity switch case. */
    public CEntitySwitchCase NewEntitySwitchCase(int l) {
        return new CEntitySwitchCase(l, programCatalog) ;
    }
    /** Creates the entity case. */
    public CEntityCase NewEntityCase(int l, int endline)    {
        return new CEntityCase(l, programCatalog, endline);
    }
    /** Creates the entity sub string. */
    public CSubStringAttributReference NewEntitySubString(int l)    {
        CSubStringAttributReference entity =
            new CSubStringAttributReference(l, programCatalog);
        return entity;
    }
    /** Creates the entity array reference. */
    public CEntityArrayReference NewEntityArrayReference(int l) {
        CEntityArrayReference e = new CEntityArrayReference(l, programCatalog);
        return e;
    }
    /** Creates the entity goto. */
    public CEntityGoto NewEntityGoto(int l, String reference, CEntityProcedureSection section)  {
        CEntityGoto e = new CEntityGoto(l, programCatalog, reference, section);
        return e;
    }
    /** Creates the entity goto depending. */
    public CEntityGotoDepending NewEntityGotoDepending(int l, List<String> refs, CDataEntity dep, CEntityProcedureSection section)  {
        CEntityGotoDepending e =
            new CEntityGotoDepending(l, programCatalog, refs, dep, section);
        return e;
    }
    /** Creates the entity loop while. */
    public CEntityLoopWhile NewEntityLoopWhile(int l)   {
        return new CEntityLoopWhile(l, programCatalog);
    }
    /** Creates the entity loop iter. */
    public CEntityLoopIter NewEntityLoopIter(int l) {
        return new CEntityLoopIter(l, programCatalog);
    }
    /** Creates the entity add to. */
    public CEntityAddTo NewEntityAddTo(int l)   {
        return new CEntityAddTo(l, programCatalog);
    }
    /** Creates the entity continue. */
    public CEntityContinue NewEntityContinue(int l) {
        return new CEntityContinue(l, programCatalog);
    }
    /** Creates the entity next sentence. */
    public CEntityNextSentence NewEntityNextSentence(int l) {
        CEntityNextSentence e = new CEntityNextSentence(l, programCatalog);
        return e;
    }
    /** Creates the entity named condition. */
    public CEntityNamedCondition NewEntityNamedCondition(int l, String name)    {
        CEntityNamedCondition entity =
            new CEntityNamedCondition(l, name, programCatalog);
        return entity;
    }
    /** Creates the entity sqlsingle statement. */
    public CEntitySQLSingleStatement NewEntitySQLSingleStatement(int l, String st)  {
        // Direct backend CJavaSQLSingleStatement retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (recursiveSQLSingleStatementEntity
        // binding). The raw statement text is carried by the entity and the template
        // wraps it verbatim in the legacy getDBConnection().execSQL("...") call; no
        // WHENEVER clause is chained and no generated string is materialized in the
        // factory.
        CEntitySQLSingleStatement e = new CEntitySQLSingleStatement(l, programCatalog, st);
        return e;
    }
    /** Creates the entity set color. */
    public CEntitySetColor NewEntitySetColor(int l, CDataEntity field)  {
        // Direct backend CJavaSetColor retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (semantic.forms.CEntitySetColor
        // -> recursiveSetColorEntity), reproducing the legacy DoExport exactly —
        // a set color constant emits moveColor(MapFieldAttrColor.<name>, <field>),
        // a moved color variable emits moveColor(<variable>, <field>), otherwise
        // the fall-through emits moveColor(MapFieldAttrColor.NEUTRAL, <field>) (the
        // protected OnlineProgram moveColor overloads contracted as
        // bms.color.move.attr / bms.color.move.edit / bms.color.move.var).
        // CJavaEntityFactory inherits this wiring; the legacy output controller
        // stays bound for the BMS traversal compatibility boundary.
        programCatalog.addImportDeclaration("MAP") ;
        CEntitySetColor e = new CEntitySetColor(l, programCatalog, field) ;
        return e ;
    }
    /** Creates the entity field lengh. */
    public CEntityFieldLength NewEntityFieldLengh(int l, String name, CDataEntity field)    {
        // Direct backend CJavaFieldLength retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (semantic.forms.CEntityFieldLength
        // -> valueReferenceEntity), reproducing the legacy ExportReference that
        // rendered the owner field reference. The legacy output controller stays
        // bound for the BMS traversal compatibility boundary.
        CEntityFieldLength e = new CEntityFieldLength(l, name, programCatalog, field) ;
        return e ;
    }
    /** Creates the entity field color. */
    public CEntityFieldColor NewEntityFieldColor(int l, String name, CDataEntity field) {
        // Direct backend CJavaFieldColor retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (semantic.forms.CEntityFieldColor
        // -> valueReferenceEntity), reproducing the legacy ExportReference that
        // rendered the owner field reference. The legacy ExportWriteAccessorTo
        // (moveColor) had no live consumer in the recursive pipeline, so it retired
        // without a replacement. The legacy output controller stays bound for the
        // BMS traversal compatibility boundary.
        CEntityFieldColor e = new CEntityFieldColor(l, name, programCatalog, field) ;
        return e ;
    }
    /** Creates the entity field highlight. */
    public CEntityFieldHighlight NewEntityFieldHighlight(int l, String name, CDataEntity field) {
        // Direct backend CJavaFieldHighligh retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (semantic.forms.CEntityFieldHighlight
        // -> fieldHighlightReferenceEntity), reproducing the legacy ExportReference
        // "getHighlighting(<owner field reference>)" (owner carried in the inherited
        // reference slot). The legacy ExportWriteAccessorTo (moveHighlight) had no
        // live consumer in the recursive pipeline (no naca-rt signature; the FPac
        // factory throws for this entity, and CJavaFormAccessor delegates to its
        // owner form), so it retired without a replacement. The legacy output
        // controller stays bound for the BMS traversal compatibility boundary.
        CEntityFieldHighlight e = new CEntityFieldHighlight(l, name, programCatalog, field) ;
        return e ;
    }
    /** Creates the entity field flag. */
    public CEntityFieldFlag NewEntityFieldFlag(int l, String name, CDataEntity field)   {
        // Direct backend CJavaFieldFlag retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (semantic.forms.CEntityFieldFlag
        // -> fieldFlagReferenceEntity), reproducing the legacy ExportReference that
        // rendered "<owner field reference>.getFlag()". The legacy
        // ExportWriteAccessorTo (moveFlag) had no live consumer in the recursive
        // pipeline (MOVEs on <FIELD>P lower through the CEntitySetFlag action
        // entity built by the semantic node's GetSpecialAssignment), so it retired
        // without a replacement. The legacy output controller stays bound for the
        // BMS traversal compatibility boundary.
        CEntityFieldFlag e = new CEntityFieldFlag(l, name, programCatalog, field) ;
        return e ;
    }
    /** Creates the entity set highlight. */
    public CEntitySetHighligh NewEntitySetHighlight(int l, CDataEntity field)   {
        // Direct backend CJavaSetHighlight retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (semantic.forms.CEntitySetHighligh
        // -> recursiveSetHighlightEntity), reproducing the legacy DoExport's
        // independent flag branches in order — blink -> setFieldBlink(<field>),
        // reverse -> setFieldReverse(<field>), underline -> setFieldUnderline(<field>),
        // normal -> setFieldUnhighlighted(<field>), a moved value ->
        // moveHighLighting(<value>, <field>) — and mapping the legacy reset branch's
        // resetFieldHighlighting(<field>) (which has no naca-rt signature and never
        // compiled) to the real OnlineProgram.setFieldUnhighlighted (highlighting
        // OFF). CJavaEntityFactory inherits this wiring; the legacy output
        // controller stays bound for the BMS traversal compatibility boundary.
        programCatalog.addImportDeclaration("MAP") ;
        CEntitySetHighligh e = new CEntitySetHighligh(l, programCatalog, field) ;
        return e ;
    }
    /** Creates the entity set flag. */
    public CEntitySetFlag NewEntitySetFlag(int l, CDataEntity field)    {
        // Direct backend CJavaSetFlag retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (semantic.forms.CEntitySetFlag
        // -> recursiveSetFlagEntity), reproducing the legacy DoExport exactly —
        // a non-null flag value emits moveFlag("<value>", <field>) with the
        // constant quoted verbatim, otherwise resetFlag(<field>) (the protected
        // OnlineProgram flag calls contracted as bms.flag.move / bms.flag.reset).
        // CJavaEntityFactory inherits this wiring; the legacy output controller
        // stays bound for the BMS traversal compatibility boundary.
        CEntitySetFlag e = new CEntitySetFlag(l, programCatalog, field) ;
        return e ;
    }
    /** Creates the entity set cursor. */
    public CEntitySetCursor NewEntitySetCursor(int l, CDataEntity field)    {
        // Direct backend CJavaSetCursor retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (semantic.forms.CEntitySetCursor
        // -> recursiveSetCursorEntity), reproducing the legacy DoExport exactly —
        // a set reference value emits moveCursor(<value>, <field>), the remove
        // flag emits removeCursor(<field>), otherwise setCursor(<field>) (the
        // OnlineProgram cursor calls contracted as bms.cursor.set/remove/move).
        // CJavaEntityFactory inherits this wiring; the legacy output controller
        // stays bound for the BMS traversal compatibility boundary.
        CEntitySetCursor e = new CEntitySetCursor(l, programCatalog, field) ;
        return e ;
    }
    /** Creates the entity set attribute. */
    public CEntitySetAttribute NewEntitySetAttribute(int l, CDataEntity field)  {
        // Direct backend CJavaSetAttribute retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (semantic.forms.CEntitySetAttribute
        // -> recursiveSetAttributeEntity), reproducing the legacy DoExport exactly —
        // a set attribute value emits the single moveAttribute(<value>, <field>) and
        // stops, otherwise up to three moveAttribute(<constant>, <field>) calls are
        // emitted, one per attribute group in the legacy else-if precedence
        // (protection / intensity / modified; the protected OnlineProgram moveAttribute
        // overloads contracted as bms.attribute.protection / .intensity / .modified /
        // .move.var). CJavaEntityFactory inherits this wiring; the legacy output
        // controller stays bound for the BMS traversal compatibility boundary.
        programCatalog.addImportDeclaration("MAP") ;
        CEntitySetAttribute e = new CEntitySetAttribute(l, programCatalog, field) ;
        return e ;
    }
    /** Creates the entity assign with accessor. */
    public CEntityAssignWithAccessor NewEntityAssignWithAccessor(int l) {
        CEntityAssignWithAccessor e = new CEntityAssignWithAccessor(l, programCatalog);
        return e;
    }
    /** Creates the entity field data. */
    public CEntityFieldData NewEntityFieldData(int l, String name, CDataEntity field)   {
        return BmsJavaEntities.fieldData(l, name, programCatalog, langOutput, field);
    }
    /** Creates the resource string. */
    public CResourceStrings NewResourceString(int nbLines, int nbCols)  {
        return BmsJavaEntities.resourceStrings(nbLines, nbCols);
    }
    /** Creates the entity environment variable. */
    public CEntityEnvironmentVariable NewEntityEnvironmentVariable(String name, String acc, boolean bNumeric)   {
        CEntityEnvironmentVariable e =
            new CEntityEnvironmentVariable(0, name, programCatalog, acc, "", bNumeric);
        return e;
    }
    /** Creates the entity environment variable. */
    public CEntityEnvironmentVariable NewEntityEnvironmentVariable(String name, String acc, String write, boolean bNumeric) {
        CEntityEnvironmentVariable e =
            new CEntityEnvironmentVariable(0, name, programCatalog, acc, write, bNumeric);
        return e;
    }
    /** Creates the entity working skip field. */
    public CEntitySkipFields NewEntityWorkingSkipField(int l, String name, int nbFields, String level)  {
        return BmsJavaEntities.skipFields(l, name, programCatalog, langOutput, nbFields, level);
    }
    /** Creates the entity entry field. */
    public CEntityResourceField NewEntityEntryField(int l, String name) {
        return BmsJavaEntities.entryField(l, name, programCatalog, langOutput);
    }
    /** Creates the entity label field. */
    public CEntityResourceField NewEntityLabelField(int l)  {
        return BmsJavaEntities.labelField(l, programCatalog, langOutput);
    }
    /** Creates the entity field redefine. */
    public CEntityFieldRedefine NewEntityFieldRedefine(int l, String name, String level)    {
        return BmsJavaEntities.fieldRedefine(l, name, programCatalog, langOutput, level);
    }
    /** Creates the entity form redefine. */
    public CEntityFormRedefine NewEntityFormRedefine(int l, String name, CDataEntity eForm, boolean bSaveMap)   {
        //programCatalog.addImportDeclaration("MAP") ;
        return BmsJavaEntities.formRedefine(l, name, programCatalog, langOutput, eForm, bSaveMap);
    }
    /** Creates the entity string. */
    public CEntityString NewEntityString(char[] value)  {
        CEntityString e = new CEntityString(programCatalog, value) ;
        return e ;
    }
    /** Creates the entity cond or. */
    public CEntityCondOr NewEntityCondOr()  {
        return new CEntityCondOr();
    }
    /** Creates the entity number. */
    public CEntityNumber NewEntityNumber(String value)  {
        CEntityNumber e = new CEntityNumber(programCatalog, value);
        return e;
    }
    /** Creates the entity expr terminal. */
    public CEntityExprTerminal NewEntityExprTerminal(CDataEntity eData) {
        return new CEntityExprTerminal(eData);
    }
    /** Creates the entity expr sum. */
    public CEntityExprSum NewEntityExprSum()    {
        return new CEntityExprSum();
    }
    /** Creates the entity expr prod. */
    public CEntityExprProd NewEntityExprProd()  {
        return new CEntityExprProd();
    }
    /** Creates the entity cond not. */
    public CEntityCondNot NewEntityCondNot()    {
        return new CEntityCondNot();
    }
    /** Creates the entity cond equals. */
    public CEntityCondEquals NewEntityCondEquals()  {
        return new CEntityCondEquals();
    }
    /** Creates the entity cond compare. */
    public CEntityCondCompare NewEntityCondCompare()    {
        return new CEntityCondCompare();
    }
    /** Creates the entity cond and. */
    public CEntityCondAnd NewEntityCondAnd()    {
        return new CEntityCondAnd();
    }
    /** Creates the entity cond is all. */
    public CEntityCondIsAll NewEntityCondIsAll()    {
        return new CEntityCondIsAll();
    }
    /** Creates the entity cond is kind of. */
    public CEntityCondIsKindOf NewEntityCondIsKindOf()  {
        return new CEntityCondIsKindOf();
    }
    /** Creates the entity cond is constant. */
    public CEntityCondIsConstant NewEntityCondIsConstant()  {
        return new CEntityCondIsConstant() ;
    }
    /** Creates the entity is field flag. */
    public CEntityIsFieldFlag NewEntityIsFieldFlag()    {
        // Direct backend CJavaIsFieldFlag retired: the pure semantic entity is rendered
        // by the recursive ST4 assembly contract
        // (semantic.forms.CEntityIsFieldFlag -> recursiveIsFieldFlagEntity), reproducing
        // the legacy Export exactly — isFieldFlagSet(<reference>) / isNotFieldFlagSet(<reference>)
        // when the isSet flag is set (IF <FIELD>P = LOW-VALUE) and
        // isFieldFlag(<reference>, "v") / isNotFieldFlag(<reference>, "v") otherwise
        // (IF <FIELD>P = 1 / 0), the four protected OnlineProgram condition calls contracted
        // as bms.field.flag / bms.field.flag.not / bms.field.flag.set / bms.field.flag.setNot.
        // The template branches on the entity's own isOpposite()/isSet() flags (SetIsFlag /
        // SetIsFlagSet / SetOpposite and GetOppositeCondition set them), so all four legacy
        // runtime calls are preserved byte-for-byte. CJavaEntityFactory inherits this
        // wiring; the FPac factory still throws for this entity, so no FPac tree holds it.
        return new CEntityIsFieldFlag();
    }
    /** Creates the entity set constant. */
    public CEntitySetConstant NewEntitySetConstant(int l)   {
        CEntitySetConstant e = new CEntitySetConstant(l, programCatalog);
        return e;
    }
    /** Creates the entity is field color. */
    public CEntityIsFieldColor NewEntityIsFieldColor()  {
        programCatalog.addImportDeclaration("MAP") ;
        return BmsJavaEntities.isFieldColor();
    }
    /** Creates the entity is field attribute. */
    public CEntityIsFieldAttribute NewEntityIsFieldAttribute()  {
        programCatalog.addImportDeclaration("MAP") ;
        return BmsJavaEntities.isFieldAttribute() ;
    }
    /** Creates the entity address reference. */
    public CEntityAddressReference NewEntityAddressReference(CDataEntity ref)   {
        CEntityAddressReference e = new CEntityAddressReference(programCatalog, ref);
        return e;
    }
    /** Creates the entity move reference. */
    public CEntityMoveReference NewEntityMoveReference(int l)   {
        CEntityMoveReference entity = new CEntityMoveReference(l, programCatalog) ;
        return entity ;
    }
    /** Creates the entity subtract to. */
    public CEntitySubtractTo NewEntitySubtractTo(int l) {
        CEntitySubtractTo e = new CEntitySubtractTo(l, programCatalog);
        return e;
    }
    /** Creates the entity is named condition. */
    public CEntityIsNamedCondition NewEntityIsNamedCondition()  {
        return new CEntityIsNamedCondition();
    }
    /** Creates the entity data section. */
    public CEntityDataSection NewEntityDataSection(int l, String name)  {
        CEntityDataSection e = new CEntityDataSection(l, name, programCatalog);
        return e;
    }
    /** Creates the entity replace. */
    public CEntityReplace NewEntityReplace(int l)   {
        CEntityReplace e = new CEntityReplace(l, programCatalog);
        return e;
    }
    /** Creates the entity is field highlight. */
    public CEntityIsFieldHighlight NewEntityIsFieldHighlight(CDataEntity ref)   {
        programCatalog.addImportDeclaration("MAP") ;
        return BmsJavaEntities.isFieldHighlight(ref) ;
    }
    /** Creates the entity field validated. */
    public CEntityFieldValidated NewEntityFieldValidated(int l, String name, CDataEntity field) {
        // Direct backend CJavaFieldValidated retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (semantic.forms.CEntityFieldValidated
        // -> fieldValidatedReferenceEntity), reproducing the legacy ExportReference
        // "<owner field reference>.getValidation()" (owner carried in the inherited
        // reference slot). The legacy ExportWriteAccessorTo (moveValidation) had no
        // live consumer in the recursive pipeline (no naca-rt signature; the FPac
        // factory throws for this entity, and CJavaFormAccessor delegates to its
        // owner form), so it retired without a replacement. The legacy output
        // controller stays bound for the BMS traversal compatibility boundary.
        CEntityFieldValidated e = new CEntityFieldValidated(l, name, programCatalog, field) ;
        return e ;
    }
    /** Creates the entity string concat. */
    public CEntityStringConcat NewEntityStringConcat(int l) {
        CEntityStringConcat e = new CEntityStringConcat(l, programCatalog);
        return e;
    }
    /** Creates the entity divide. */
    public CEntityDivide NewEntityDivide(int l) {
        CEntityDivide e = new CEntityDivide(l, programCatalog);
        return e;
    }
    /** Creates the entity multiply. */
    public CEntityMultiply NewEntityMultiply(int l) {
        CEntityMultiply e = new CEntityMultiply(l, programCatalog);
        return e;
    }
    /** Creates the entity parse string. */
    public CEntityParseString NewEntityParseString(int l)   {
        CEntityParseString e = new CEntityParseString(l, programCatalog);
        return e;
    }
    /** Creates the entity sqlroll back. */
    public CEntitySQLRollBack NewEntitySQLRollBack(int l)   {
        // Direct backend CJavaSQLRollBack retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (recursiveSQLRollBackEntity
        // binding). The optional WHENEVER SQLWARNING/SQLERROR clause is a read-only
        // catalog lookup the template chains onto sqlRollback(); no generated string
        // is materialized in the factory.
        CEntitySQLRollBack e = new CEntitySQLRollBack(l, programCatalog);
        return e;
    }
    /** Creates the entity sqlcommit. */
    public CEntitySQLCommit NewEntitySQLCommit(int l)   {
        // Direct backend CJavaSQLCommit retired: the pure semantic entity is rendered
        // by the recursive ST4 assembler (recursiveSQLCommitEntity binding). The
        // optional WHENEVER SQLWARNING/SQLERROR clause is a read-only catalog lookup
        // the template chains onto sqlCommit(); no generated string is materialized
        // in the factory.
        CEntitySQLCommit e = new CEntitySQLCommit(l, programCatalog);
        return e;
    }
    /** Creates the entity expr opposite. */
    public CEntityExprOpposite NewEntityExprOpposite()  {
        return new CEntityExprOpposite();
    }
    /** Creates the entity cicsxctl. */
    public CEntityCICSXctl NewEntityCICSXctl(int l) {
        // Direct backend CJavaCICSXctl retired: the pure semantic entity is rendered
        // by the recursive ST4 assembler (recursiveCICSXctlEntity binding).
        CEntityCICSXctl e = new CEntityCICSXctl(l, programCatalog);
        return e;
    }
    /** Creates the entity cicslink. */
    public CEntityCICSLink NewEntityCICSLink(int l) {
        // Direct backend CJavaCICSLink retired: the pure semantic entity is rendered
        // by the recursive ST4 assembler (recursiveCICSLinkEntity binding).
        CEntityCICSLink e = new CEntityCICSLink(l, programCatalog);
        return e;
    }
    /** Creates the entity cicsaddress. */
    public CEntityCICSAddress NewEntityCICSAddress(int l) {
        // Direct backend CJavaCICSAddress retired: the pure semantic entity is rendered
        // by the recursive ST4 assembler (recursiveCICSAddressEntity binding).
        CEntityCICSAddress e = new CEntityCICSAddress(l, programCatalog);
        return e;
    }
    /** Creates the entity cicsask time. */
    public CEntityCICSAskTime NewEntityCICSAskTime(int l)   {
        // Direct backend CJavaCICSAskTime retired: the pure semantic entity is rendered
        // by the recursive ST4 assembler (recursiveCICSAskTimeEntity binding).
        CEntityCICSAskTime e = new CEntityCICSAskTime(l, programCatalog);
        return e;
    }
    /** Creates the entity current date. */
    public CEntityCurrentDate NewEntityCurrentDate()    {
        CEntityCurrentDate e = new CEntityCurrentDate(programCatalog);
        return e;
    }
    /** Creates the entity intrinsic function. */
    public CEntityIntrinsicFunction NewEntityIntrinsicFunction(String functionName, List<CBaseEntityExpression> arguments)  {
        CEntityIntrinsicFunction e =
            new CEntityIntrinsicFunction(programCatalog, functionName, arguments);
        return e;
    }
    /** Creates the entity address of. */
    public CEntityAddressOf NewEntityAddressOf(CDataEntity data)    {
        CEntityAddressOf e = new CEntityAddressOf(programCatalog, data);
        return e;
    }
    /** Creates the entity length of. */
    public CEntityLengthOf NewEntityLengthOf(CDataEntity data)  {
        CEntityLengthOf e = new CEntityLengthOf(programCatalog, data);
        return e;
    }
    /** Creates the entity cicshandle condition. */
    public CEntityCICSHandleCondition NewEntityCICSHandleCondition(int l)   {
        // Direct backend CJavaCICSHandleCondition retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (recursiveCICSHandleConditionEntity
        // binding).
        CEntityCICSHandleCondition e = new CEntityCICSHandleCondition(l, programCatalog);
        return e;
    }
    /** Creates the entity cicshandle aid. */
    public CEntityCICSHandleAID NewEntityCICSHandleAID(int l)   {
        // Direct backend CJavaCICSHandleAID retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (recursiveCICSHandleAIDEntity
        // binding).
        CEntityCICSHandleAID e = new CEntityCICSHandleAID(l, programCatalog);
        return e;
    }
    /** Creates the entity cicsignore condition. */
    public CEntityCICSIgnoreCondition NewEntityCICSIgnoreCondition(int l)   {
        CEntityCICSIgnoreCondition e = new CEntityCICSIgnoreCondition(l, programCatalog);
        return e;
    }
    /** Creates the entity cicsretreive. */
    public CEntityCICSRetrieve NewEntityCICSRetreive(int l, boolean bPointer)   {
        CEntityCICSRetrieve e = new CEntityCICSRetrieve(l, programCatalog, bPointer);
        return e;
    }
    /** Creates the entity cicsstart. */
    public CEntityCICSStart NewEntityCICSStart(int l, CDataEntity tid)  {
        CEntityCICSStart e = new CEntityCICSStart(l, programCatalog, tid);
        return e;
    }
    /** Creates the entity cicsreturn. */
    public CEntityCICSReturn NewEntityCICSReturn(int l) {
        // Direct backend CJavaCICSReturn retired: the pure semantic entity is rendered
        // by the recursive ST4 assembler (recursiveCICSReturnEntity binding).
        CEntityCICSReturn e = new CEntityCICSReturn(l, programCatalog);
        return e;
    }
    /** Creates the entity cicssend map. */
    public CEntityCICSSendMap NewEntityCICSSendMap(int l)   {
        // Direct backend CJavaCICSSendMap retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (recursiveCICSSendMapEntity binding).
        CEntityCICSSendMap e = new CEntityCICSSendMap(l, programCatalog);
        return e;
    }
    /** Creates the entity cicswrite. */
    public CEntityCICSWrite NewEntityCICSWrite(int l)   {
        CEntityCICSWrite e = new CEntityCICSWrite(l, programCatalog);
        return e;
    }
    /** Creates the entity cicsreceive map. */
    public CEntityCICSReceiveMap NewEntityCICSReceiveMap(int l, CDataEntity name)   {
        // Direct backend CJavaCICSReceiveMap retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (recursiveCICSReceiveMapEntity binding).
        CEntityCICSReceiveMap e = new CEntityCICSReceiveMap(l, programCatalog, name);
        return e;
    }
    /** Creates the entity is field modified. */
    public CEntityIsFieldModified NewEntityIsFieldModified() {
        // Direct backend CJavaIsFieldModified retired: the pure semantic entity is
        // rendered by the recursive ST4 assembly contract
        // (semantic.forms.CEntityIsFieldModified -> recursiveIsFieldModifiedEntity),
        // reproducing the legacy Export exactly — isFieldModified(<reference>), the
        // protected OnlineProgram.isFieldModified(Edit) condition call contracted as
        // bms.field.modified. GetOppositeCondition now returns a pure
        // semantic.expression.CEntityCondNot (rendered !(isFieldModified(<reference>))
        // by recursiveCondNotEntity) instead of the legacy generate.bmsjava.CBmsJavaCondNot,
        // keeping the semantic tree free of any generate.* coupling. CJavaEntityFactory
        // inherits this wiring; the FPac factory still throws for this entity, so no FPac
        // tree ever holds it.
        return new CEntityIsFieldModified();
    }
    /** Creates the entity cicssync point. */
    public CEntityCICSSyncPoint NewEntityCICSSyncPoint(int l, boolean bRollBack)    {
        // Direct backend CJavaCICSSyncPoint retired: the pure semantic entity is rendered
        // by the recursive ST4 assembler (recursiveCICSSyncPointEntity binding).
        CEntityCICSSyncPoint e = new CEntityCICSSyncPoint(l, programCatalog, bRollBack);
        return e;
    }
    /** Creates the entity cicsinquire. */
    public CEntityCICSInquire NewEntityCICSInquire(int l)   {
        // Direct backend CJavaCICSInquire retired: the pure semantic entity is rendered
        // by the recursive ST4 assembler (recursiveCICSInquireEntity binding).
        CEntityCICSInquire e = new CEntityCICSInquire(l, programCatalog);
        return e;
    }
    /** Creates the entity cicsabend. */
    public CEntityCICSAbend NewEntityCICSAbend(int l)   {
        // Direct backend CJavaCICSAbend retired: the pure semantic entity is rendered
        // by the recursive ST4 assembler (recursiveCICSAbendEntity binding).
        CEntityCICSAbend e = new CEntityCICSAbend(l, programCatalog);
        return e;
    }
    /** Creates the entity cicsread. */
    public CEntityCICSRead NewEntityCICSRead(int l, CEntityCICSRead.CEntityCICSReadMode mode)   {
        CEntityCICSRead e = new CEntityCICSRead(l, programCatalog, mode);
        return e;
    }
    /** Creates the entity cicsstart browse. */
    public CEntityCICSStartBrowse NewEntityCICSStartBrowse(int l)   {
        CEntityCICSStartBrowse e = new CEntityCICSStartBrowse(l, programCatalog);
        return e;
    }
    /** Creates the entity cicsdelete q. */
    public CEntityCICSDeleteQ NewEntityCICSDeleteQ(int l, boolean b)    {
        CEntityCICSDeleteQ e = new CEntityCICSDeleteQ(l, programCatalog, b);
        return e;
    }
    /** Creates the entity cicswrite q. */
    public CEntityCICSWriteQ NewEntityCICSWriteQ(int l, boolean b)  {
        CEntityCICSWriteQ e = new CEntityCICSWriteQ(l, programCatalog, b);
        return e;
    }
    /** Creates the entity cicsread q. */
    public CEntityCICSReadQ NewEntityCICSReadQ(int l, boolean b)    {
        CEntityCICSReadQ e = new CEntityCICSReadQ(l, programCatalog, b);
        return e;
    }
    /** Creates the entity cicsassign. */
    public CEntityCICSAssign NewEntityCICSAssign(int l) {
        // Direct backend CJavaCICSAssign retired: the pure semantic entity is rendered
        // by the recursive ST4 assembler (recursiveCICSAssignEntity binding).
        CEntityCICSAssign e = new CEntityCICSAssign(l, programCatalog);
        return e;
    }
    /** Creates the entity display. */
    public CEntityDisplay NewEntityDisplay(int l, Upon t)   {
        CEntityDisplay e = new CEntityDisplay(l, programCatalog, t);
        return e;
    }
    /** Creates the entity count. */
    public CEntityCount NewEntityCount(int l)   {
        CEntityCount e = new CEntityCount(l, programCatalog);
        return e;
    }
    /** Creates the entity inspect converting. */
    public CEntityInspectConverting NewEntityInspectConverting(int l) {
        CEntityInspectConverting e = new CEntityInspectConverting(l, programCatalog);
        return e;
    }
    /** Creates the entity cicsre write. */
    public CEntityCICSReWrite NewEntityCICSReWrite(int l)   {
        CEntityCICSReWrite e = new CEntityCICSReWrite(l, programCatalog);
        return e;
    }
    /** Creates the entity cicsdelay. */
    public CEntityCICSDelay NewEntityCICSDelay(int l)   {
        // Direct backend CJavaCICSDelay retired: the pure semantic entity is rendered
        // by the recursive ST4 assembler (recursiveCICSDelayEntity binding).
        CEntityCICSDelay e = new CEntityCICSDelay(l, programCatalog);
        return e;
    }
    /** Creates the entity cicsset tdqueue. */
    public CEntityCICSSetTDQueue NewEntityCICSSetTDQueue(int l) {
        CEntityCICSSetTDQueue e = new CEntityCICSSetTDQueue(l, programCatalog);
        return e;
    }
    /** Creates the entity cicsde q. */
    public CEntityCICSDeQ NewEntityCICSDeQ(int l)   {
        // Direct backend CJavaCICSDeQ retired: the pure semantic entity is rendered
        // by the recursive ST4 assembler (recursiveCICSDeQEntity binding).
        CEntityCICSDeQ e = new CEntityCICSDeQ(l, programCatalog);
        return e;
    }
    /** Creates the entity cicsen q. */
    public CEntityCICSEnQ NewEntityCICSEnQ(int l)   {
        // Direct backend CJavaCICSEnQ retired: the pure semantic entity is rendered
        // by the recursive ST4 assembler (recursiveCICSEnQEntity binding).
        CEntityCICSEnQ e = new CEntityCICSEnQ(l, programCatalog);
        return e;
    }
    /** Creates the entity procedure division. */
    public CEntityProcedureDivision NewEntityProcedureDivision(int l)   {
        CEntityProcedureDivision entity =
            new CEntityProcedureDivision(l, programCatalog);
        return entity;
    }
    /** Creates the entity sqlcursor section. */
    public CEntitySQLCursorSection NewEntitySQLCursorSection()  {
        // Direct backend CJavaSQLCursorSection retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (recursiveSQLCursorSectionEntity
        // declaration binding).
        CEntitySQLCursorSection e = new CEntitySQLCursorSection(programCatalog);
        return e;
    }
    /** Creates the entity field array reference. */
    public CEntityFieldArrayReference NewEntityFieldArrayReference(int l)   {
        // Direct backend CJavaFieldArrayReference retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (semantic.forms.CEntityFieldArrayReference
        // -> arrayReferenceEntity), reproducing the legacy ExportReference
        // "<field reference>.getAt(<indexes>)" byte-for-byte (the same frozen template
        // the COBOL CEntityArrayReference uses). The legacy ExportWriteAccessorTo returned
        // "" (unused) and had no live consumer in the recursive pipeline
        // (LegacyDataRenderer.renderWriteAccessor is only reached from the FPac accessor
        // backend, whose factory throws for this entity), so it retired without a
        // replacement. The legacy output controller stays bound for the BMS traversal
        // compatibility boundary.
        CEntityFieldArrayReference e = new CEntityFieldArrayReference(l, programCatalog);
        return e;
    }
    /** Creates the entity index. */
    public CEntityIndex NewEntityIndex(String name) {
        CEntityIndex entity = new CEntityIndex(name, programCatalog);
        return entity;
    }
    /** Creates the entity sqlcursor. */
    public CEntitySQLCursor NewEntitySQLCursor(String name) {
        // Direct backend CJavaSQLCursor retired: the pure semantic entity renders
        // through the recursive ST4 assembler (semantic.SQL.CEntitySQLCursor =
        // dataReferenceEntity reference binding) and keeps its legacy formatted
        // ExportReference for the direct path.
        CEntitySQLCursor e = new CEntitySQLCursor(name, programCatalog);
        return e;
    }
    /** Creates the entity key pressed. */
    public CEntityKeyPressed NewEntityKeyPressed(String name, String caption)   {
        //programCatalog.UseMapSupport() ;
        return BmsJavaEntities.keyPressed(0, name, programCatalog, langOutput, caption);
    }
    /** Creates the entity get key pressed. */
    public CEntityGetKeyPressed NewEntityGetKeyPressed(String name) {
        return BmsJavaEntities.getKeyPressed(name, programCatalog, langOutput);
    }
    /** Creates the entity is key pressed. */
    public CEntityIsKeyPressed NewEntityIsKeyPressed()  {
        programCatalog.addImportDeclaration("KEYPRESSED") ;
        return BmsJavaEntities.isKeyPressed();
    }
    /** Creates the entity field occurs. */
    public CEntityFieldOccurs NewEntityFieldOccurs(int l, String name)  {
        return BmsJavaEntities.fieldOccurs(l, name, programCatalog, langOutput);
    }
    /** Creates the entity unknown reference. */
    public CEntityUnknownReference NewEntityUnknownReference(int nLine, String csName)
    {
        CEntityUnknownReference entity =
            new CEntityUnknownReference(nLine, csName, programCatalog);
        return entity;
    }
    /** Creates the entity cicsget main. */
    public CEntityCICSGetMain NewEntityCICSGetMain(int l)   {
        // Direct backend CJavaCICSGetMain retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (recursiveCICSGetMainEntity
        // binding).
        CEntityCICSGetMain e = new CEntityCICSGetMain(l, programCatalog);
        return e;
    }
    /** Creates the entity reset key pressed. */
    public CEntityResetKeyPressed NewEntityResetKeyPressed(int l)   {
        return BmsJavaEntities.resetKeyPressed(l, programCatalog, langOutput);
    }
    /** Creates the entity field array. */
    public CEntityResourceFieldArray NewEntityFieldArray()  {
        return BmsJavaEntities.fieldArray(0, "", programCatalog, langOutput);
    }
    /** Creates the entity sqlcode. */
    public CEntitySQLCode NewEntitySQLCode(String name) {
        // Direct backend CJavaSQLCode retired: the pure semantic entity is rendered
        // by the recursive ST4 assembler (recursiveSQLCodeEntity binding) and carries
        // its own getSQLCode()/resetSQLCode(...) data-reference protocol.
        CEntitySQLCode e = new CEntitySQLCode(name, programCatalog);
        return e;
    }
    /** Creates the entity sqlcode. */
    public CEntitySQLCode NewEntitySQLCode(String name, CBaseEntityExpression eHistoryItem) {
        CEntitySQLCode e = new CEntitySQLCode(name, programCatalog, eHistoryItem);
        return e;
    }
    /** Creates the entity cond is sqlcode. */
    public CEntityCondIsSQLCode NewEntityCondIsSQLCode()    {
        // Direct backend CJavaCondIsSQLCode retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (recursiveCondIsSQLCodeEntity
        // binding); the addImportDeclaration("SQL") Stage-1 side effect (emits
        // 'import nacaLib.sqlSupport.* ;' for the SQLCode.* constants) is preserved.
        programCatalog.addImportDeclaration("SQL") ;
        return new CEntityCondIsSQLCode();
    }
    /** Creates the entity routine emulation call. */
    public CEntityRoutineEmulationCall NewEntityRoutineEmulationCall(int l) {
        CEntityRoutineEmulationCall e = new CEntityRoutineEmulationCall(l, programCatalog);
        return e;
    }

    protected Hashtable<String, CDataEntity> tabConstantValues = new Hashtable<String, CDataEntity>() ;
    /** Adds the special constant value. */
    public void addSpecialConstantValue(String value, String constant)
    {
        tabConstantValues.put(value, new CEntityConstantValue(constant));
    }
    /** Returns the special constant value. */
    public CDataEntity getSpecialConstantValue(String value)
    {
        if (tabConstantValues.containsKey(value))
        {
            return tabConstantValues.get(value) ;
        }
        else
        {
            int n = programCatalog.GetNbMap() ;
            for (int i=0; i<n; i++)
            {
                CEntityResourceForm form = programCatalog.GetMap(i) ;
                if (form.isFormAlias(value))
                {
                    String code = "LanguageCode."+CResourceStrings.getOfficialLanguageCode(value);
                    programCatalog.addImportDeclaration("MAP") ;
                    return new CEntityConstantValue(code) ;
                }
            }
            return null;
        }
    }
    /** Returns the all special constant attributes. */
    public Vector<CDataEntity> getAllSpecialConstantAttributes()
    {
        Vector<CDataEntity> arr = new Vector<CDataEntity>() ;
        Enumeration<CDataEntity> iter = tabConstantValues.elements();
        while (iter.hasMoreElements())
        {
            arr.add(iter.nextElement()) ;
        }
        return arr ;
    }
    /** Creates the entity concat. */
    public CEntityConcat NewEntityConcat(CDataEntity e1, CDataEntity e2)    {
        CEntityConcat e = new CEntityConcat(programCatalog, e1, e2);
        return e;
    }
    /** Creates the entity is field cursor. */
    public CEntityIsFieldCursor NewEntityIsFieldCursor()    {
        // Direct backend CJavaIsFieldCursor retired: the pure semantic entity is rendered
        // by the recursive ST4 assembly contract
        // (semantic.forms.CEntityIsFieldCursor -> recursiveIsFieldCursorEntity),
        // reproducing the legacy Export exactly — isFieldHasCursor(<reference>) when the
        // hasCursor flag is set (IF <FIELD>-L = -1) and isNotFieldHasCursor(<reference>)
        // otherwise, the two protected OnlineProgram condition calls contracted as
        // bms.field.cursor.has / bms.field.cursor.hasNot. The template branches on the
        // entity's own isOpposite() flag (SetHasCursor/SetHasNotCursor and
        // GetOppositeCondition flip it), so both legacy runtime calls are preserved
        // byte-for-byte without a CEntityCondNot wrap. CJavaEntityFactory inherits this
        // wiring; the FPac factory still throws for this entity, so no FPac tree holds it.
        return new CEntityIsFieldCursor();
    }
    /** Creates the entity list. */
    public CEntityList NewEntityList(String name)   {
        CEntityList e = new CEntityList(name, programCatalog);
        return e;
    }
    /** Creates the entity digits. */
    public CEntityDigits NewEntityDigits(CDataEntity nel)   {
        CEntityDigits e = new CEntityDigits(programCatalog, nel);
        return e;
    }
    /** Creates the entity search. */
    public CEntitySearch NewEntitySearch(int line)  {
        CEntitySearch e = new CEntitySearch(line, programCatalog);
        return e;
    }
    /** Creates the entity internal bool. */
    public CEntityInternalBool NewEntityInternalBool(String name)   {
        CEntityInternalBool e = new CEntityInternalBool(name, programCatalog);
        return e;
    }
    /** Creates the entity break. */
    public CEntityBreak NewEntityBreak(int line)    {
        return new CEntityBreak(line, programCatalog) ;
    }
    /** Creates the entity file descriptor. */
    public CEntityFileDescriptor NewEntityFileDescriptor(int line, String name) {
        CEntityFileDescriptor e =
            new CEntityFileDescriptor(line, name, programCatalog);
        return e;
    }
    /** Creates the entity sorted file descriptor. */
    public CEntitySortedFileDescriptor NewEntitySortedFileDescriptor(int line, String name) {
        CEntitySortedFileDescriptor entity =
            new CEntitySortedFileDescriptor(line, name, programCatalog);
        return entity;
    }
    /** Creates the entity open file. */
    public CEntityOpenFile NewEntityOpenFile(int line)  {
        CEntityOpenFile e = new CEntityOpenFile(line, programCatalog);
        return e;
    }
    /** Creates the entity close file. */
    public CEntityCloseFile NewEntityCloseFile(int line)    {
        CEntityCloseFile e = new CEntityCloseFile(line, programCatalog);
        return e;
    }
    /** Creates the entity read file. */
    public CEntityReadFile NewEntityReadFile(int line)  {
        CEntityReadFile e = new CEntityReadFile(line, programCatalog);
        return e;
    }
    /** Creates the entity write file. */
    public CEntityWriteFile NewEntityWriteFile(int line) {
        CEntityWriteFile e = new CEntityWriteFile(line, programCatalog);
        return e;
    }
    /** Creates the entity accept. */
    public CEntityAccept NewEntityAccept(int line){
        CEntityAccept e = new CEntityAccept(line, programCatalog);
        return e;
    }
    /** Creates the entity sort. */
    public CEntitySort NewEntitySort(int line)  {
        CEntitySort e = new CEntitySort(line, programCatalog);
        return e;
    }
    /** Creates the entity sort release. */
    public CEntitySortRelease NewEntitySortRelease(int line)    {
        CEntitySortRelease e = new CEntitySortRelease(line, programCatalog);
        return e;
    }
    /** Creates the entity sort return. */
    public CEntitySortReturn NewEntitySortReturn(int line)  {
        CEntitySortReturn e = new CEntitySortReturn(line, programCatalog);
        return e;
    }
    /** Creates the entity rewrite file. */
    public CEntityRewriteFile NewEntityRewriteFile(int line)    {
        CEntityRewriteFile e = new CEntityRewriteFile(line, programCatalog);
        return e;
    }
    /** Creates the entity address. */
    public CEntityAddress NewEntityAddress(String csAddresse)   {
        throw new NacaTransAssertException("Method not implemented") ;
    }
    /** Creates the entity function call. */
    public CEntityFunctionCall NewEntityFunctionCall(String mehodName, CDataEntity object)  {
        throw new NacaTransAssertException("Method not implemented") ;
    }
    /** Creates the entity cond is boolean. */
    public CEntityCondIsBoolean NewEntityCondIsBoolean()    {
        throw new NacaTransAssertException("Method not implemented") ;
    }
    /** Creates the entity sqlsession declare. */
    public CEntitySQLSessionDeclare NewEntitySQLSessionDeclare(int line)    {
        // Direct backend CJavaSQLSessionDeclare retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (recursiveSQLSessionDeclareEntity
        // binding). The full "DECLARE GLOBAL ..." statement text is carried by the
        // entity (set by the parser via setSql) and the optional WHENEVER
        // SQLWARNING/SQLERROR clause is a read-only catalog lookup the template
        // chains onto the sql(...) runtime call; no generated string is materialized
        // in the factory.
        CEntitySQLSessionDeclare e = new CEntitySQLSessionDeclare(line, programCatalog);
        return e;
    }
    /** Creates the entity sqlsession drop. */
    public CEntitySQLSessionDrop NewEntitySQLSessionDrop(int line)  {
        // Direct backend CJavaSQLSessionDrop retired: the pure semantic entity is
        // rendered by the recursive ST4 assembler (recursiveSQLSessionDropEntity
        // binding). The full "DROP ..." statement text is carried by the entity
        // (set by the parser via setSql) and the optional WHENEVER
        // SQLWARNING/SQLERROR clause is a read-only catalog lookup the template
        // chains onto the sql(...) runtime call; no generated string is materialized
        // in the factory.
        CEntitySQLSessionDrop e = new CEntitySQLSessionDrop(line, programCatalog);
        return e;
    }
    /** Creates the entity sqllock. */
    public CEntitySQLLock NewEntitySQLLock(int line)    {
        // Direct backend CJavaSQLLock retired: the pure semantic entity is rendered
        // by the recursive ST4 assembler (recursiveSQLLockEntity binding). The full
        // "LOCK TABLE <table> IN EXCLUSIVE MODE" text is assembled in Stage 1 and
        // the optional WHENEVER SQLWARNING/SQLERROR clause is a read-only catalog
        // lookup the template chains onto the sql(...) runtime call; no generated
        // string is materialized in the factory.
        CEntitySQLLock e = new CEntitySQLLock(line, programCatalog);
        return e;
    }
    /** Creates the entity sqlexecute. */
    public CEntitySQLExecute NewEntitySQLExecute(int line)  {
        // Direct backend CJavaSQLExecute retired: the pure semantic entity is rendered
        // by the recursive ST4 assembler (recursiveSQLExecuteEntity binding). The host
        // variable stays a semantic child rendered through its reference binding, and
        // the optional WHENEVER SQLWARNING/SQLERROR clause is a read-only catalog
        // lookup the template chains onto sql("EXECUTE IMMEDIATE #1").param(1, ...);
        // no generated string is materialized in the factory.
        CEntitySQLExecute e = new CEntitySQLExecute(line, programCatalog);
        return e;
    }
    /** Creates the entity formated var reference. */
    public CEntityFormatedVarReference NewEntityFormatedVarReference(CDataEntity object, String format) {
        throw new NacaTransAssertException("Method not implemented") ;
    }
    /** Creates the entity inc. */
    public CEntityInc NewEntityInc(int line)    {
        return new CEntityInc(line, programCatalog) ;
    }
    /** Creates the entity convert. */
    public CEntityConvertReference NewEntityConvert(int line)   {
        throw new NacaTransAssertException("Method not implemented") ;
    }
    /** Creates the entity is file eof. */
    public CEntityIsFileEOF NewEntityIsFileEOF(CEntityFileDescriptor fb)    {
        throw new NacaTransAssertException("Method not implemented") ;
    }
    /** Creates the entity constant. */
    public CEntityConstant NewEntityConstant(Value val) {
        return new CEntityConstant(val) ;
    }
    /** Creates the entity file descriptor length dependency. */
    public CEntityFileDescriptorLengthDependency NewEntityFileDescriptorLengthDependency(String name)   {
        CEntityFileDescriptorLengthDependency entity =
            new CEntityFileDescriptorLengthDependency(name, programCatalog) ;
        return entity ;
    }
    /** Creates the entity assign special. */
    public CEntityAssignSpecial NewEntityAssignSpecial(int l)   {
        throw new NacaTransAssertException("Method not implemented") ;
    }
    /** Creates the entity sqlcall. */
    public CEntitySQLCall NewEntitySQLCall(int line) {
        // Direct backend CJavaSQLCall retired: the pure semantic entity is rendered
        // by the recursive ST4 assembler (recursiveSQLCallEntity binding). The called
        // program reference and each host-variable parameter remain semantic children
        // and are recursively rendered through their reference bindings; no generated
        // string is materialized in the factory.
        CEntitySQLCall e = new CEntitySQLCall(line, programCatalog);
        return e;
    }


}
