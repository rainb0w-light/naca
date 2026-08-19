/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

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
import semantic.CICS.CEntityCICSSendText;
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
import semantic.forms.CEntityFieldArrayReference;
import semantic.forms.CEntityFieldAttribute;
import semantic.forms.CEntityFieldAttributeReference;
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
import utils.CObjectCatalog;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;





/**
 * @author sly
 *
 */
public abstract class CBaseEntityFactory
{

    /**
     *
     */
    public CBaseEntityFactory(CObjectCatalog cat)
    {
        programCatalog = cat ;
    }

//  public abstract CEntity NewEntityCondition(int l, String name) ;
    /** Creates the entity is field cursor. */
    public abstract CEntityIsFieldCursor NewEntityIsFieldCursor() ;
    /** Creates the entity no action. */
    public CEntityNoAction NewEntityNoAction(int l)
    {
        return new CEntityNoAction(l, programCatalog);
    }
    /** Creates the ignore external entity. */
    public CIgnoreExternalEntity NewIgnoreExternalEntity(String name)
    {
        return new CIgnoreExternalEntity(0, name, programCatalog);
    }
    /** Creates the ignore entity. */
    public CIgnoredEntity NewIgnoreEntity(String name)
    {
        return new CIgnoredEntity(0, name, programCatalog);
    };
    /** Creates the ignore entity. */
    public CIgnoredEntity NewIgnoreEntity(int l, String name)
    {
        return new CIgnoredEntity(l, name, programCatalog);
    };
    /** Creates the entity reset key pressed. */
    public abstract CEntityResetKeyPressed NewEntityResetKeyPressed(int l) ;
    /** Creates the entity cicsget main. */
    public abstract CEntityCICSGetMain NewEntityCICSGetMain(int l) ;
    /** Creates the entity unknown reference. */
    public abstract CEntityUnknownReference NewEntityUnknownReference(int nLine, String csName) ;
    /** Creates the entity field occurs. */
    public abstract CEntityFieldOccurs NewEntityFieldOccurs(int i, String string) ;
    /** Creates the entity get key pressed. */
    public abstract CEntityGetKeyPressed NewEntityGetKeyPressed(String name) ;
    /** Creates the entity is key pressed. */
    public abstract CEntityIsKeyPressed NewEntityIsKeyPressed() ;
    /** Creates the entity key pressed. */
    public abstract CEntityKeyPressed NewEntityKeyPressed(String name, String caption) ;
    /** Creates the entity sqlcursor. */
    public abstract CEntitySQLCursor NewEntitySQLCursor(String name) ;
    /** Creates the entity index. */
    public abstract CEntityIndex NewEntityIndex(String name) ;
    /** Creates the entity field array reference. */
    public abstract CEntityFieldArrayReference NewEntityFieldArrayReference(int l) ;
    /** Creates the entity sqlcursor section. */
    public abstract CEntitySQLCursorSection NewEntitySQLCursorSection() ;
    /** Creates the entity procedure division. */
    public abstract CEntityProcedureDivision NewEntityProcedureDivision(int l) ;
    /** Creates the entity cicsre write. */
    public abstract CEntityCICSReWrite NewEntityCICSReWrite(int l) ;
    /** Creates the entity cicsdelay. */
    public abstract CEntityCICSDelay NewEntityCICSDelay(int l) ;
    /** Creates the entity cicsset tdqueue. */
    public abstract CEntityCICSSetTDQueue NewEntityCICSSetTDQueue(int l) ;
    /** Creates the entity cicsde q. */
    public abstract CEntityCICSDeQ NewEntityCICSDeQ(int l) ;
    /** Creates the entity cicsen q. */
    public abstract CEntityCICSEnQ NewEntityCICSEnQ(int l) ;
    /** Creates the entity count. */
    public abstract CEntityCount NewEntityCount(int l) ;
    /** Creates the entity inspect converting. */
    public abstract CEntityInspectConverting NewEntityInspectConverting(int l) ;
    /** Creates the entity display. */
    public abstract CEntityDisplay NewEntityDisplay(int l, Upon t) ;
    /** Creates the entity cicsassign. */
    public abstract CEntityCICSAssign NewEntityCICSAssign(int l) ;
    /** Creates the entity cicswrite q. */
    public abstract CEntityCICSWriteQ NewEntityCICSWriteQ(int l, boolean b) ;
    /** Creates the entity cicsread q. */
    public abstract CEntityCICSReadQ NewEntityCICSReadQ(int l, boolean b) ;
    /** Creates the entity cicsdelete q. */
    public abstract CEntityCICSDeleteQ NewEntityCICSDeleteQ(int l, boolean b) ;
    /** Creates the entity cicsstart browse. */
    public abstract CEntityCICSStartBrowse NewEntityCICSStartBrowse(int l) ;
    /** Creates the entity cicsread. */
    public abstract CEntityCICSRead NewEntityCICSRead(int l, CEntityCICSRead.CEntityCICSReadMode mode) ;
    /** Creates the entity cicsabend. */
    public abstract CEntityCICSAbend NewEntityCICSAbend(int l) ;
    /** Creates the entity cicsinquire. */
    public abstract CEntityCICSInquire NewEntityCICSInquire(int l) ;
    /** Creates the entity cicssync point. */
    public abstract CEntityCICSSyncPoint NewEntityCICSSyncPoint(int l, boolean bRollBack) ;
    /** Creates the entity is field modified. */
    public abstract CEntityIsFieldModified NewEntityIsFieldModified() ;
    /** Creates the entity cicsreceive map. */
    public abstract CEntityCICSReceiveMap NewEntityCICSReceiveMap(int l, CDataEntity name) ;
    /** Creates the entity cicswrite. */
    public abstract CEntityCICSWrite NewEntityCICSWrite(int l) ;
    /** Creates the entity cicssend map. */
    public abstract CEntityCICSSendMap NewEntityCICSSendMap(int l) ;
    /** Creates the entity cicssend text. */
    public abstract CEntityCICSSendText NewEntityCICSSendText(int l) ;
    /** Creates the entity cicsreturn. */
    public abstract CEntityCICSReturn NewEntityCICSReturn(int l) ;
    /** Creates the entity cicsstart. */
    public abstract CEntityCICSStart NewEntityCICSStart(int l, CDataEntity tid) ;
    /** Creates the entity cicsretreive. */
    public abstract CEntityCICSRetrieve NewEntityCICSRetreive(int l, boolean bPointer) ;
    /** Creates the entity cicsignore condition. */
    public abstract CEntityCICSIgnoreCondition NewEntityCICSIgnoreCondition(int l) ;
    /** Creates the entity cicshandle condition. */
    public abstract CEntityCICSHandleCondition NewEntityCICSHandleCondition(int line) ;
    /** Creates the entity cicshandle aid. */
    public abstract CEntityCICSHandleAID NewEntityCICSHandleAID(int line) ;
    /** Creates the entity current date. */
    public abstract CEntityCurrentDate NewEntityCurrentDate() ;
    /** Creates the entity intrinsic function. */
    public abstract CEntityIntrinsicFunction NewEntityIntrinsicFunction(String functionName, List<CBaseEntityExpression> arguments) ;
    /** Creates the entity address of. */
    public abstract CEntityAddressOf NewEntityAddressOf(CDataEntity data) ;
    /** Creates the entity length of. */
    public abstract CEntityLengthOf NewEntityLengthOf(CDataEntity data) ;
    /** Creates the entity cicsxctl. */
    public abstract CEntityCICSXctl NewEntityCICSXctl(int l) ;
    /** Creates the entity cicslink. */
    public abstract CEntityCICSLink NewEntityCICSLink(int l) ;
    /** Creates the entity cicsaddress. */
    public abstract CEntityCICSAddress NewEntityCICSAddress(int l) ;
    /** Creates the entity cicsask time. */
    public abstract CEntityCICSAskTime NewEntityCICSAskTime(int l) ;
    /** Creates the entity expr opposite. */
    public abstract CEntityExprOpposite NewEntityExprOpposite() ;
    /** Creates the entity sqlcommit. */
    public abstract CEntitySQLCommit NewEntitySQLCommit(int l) ;
    /** Creates the entity sqlroll back. */
    public abstract CEntitySQLRollBack NewEntitySQLRollBack(int l) ;
    /** Creates the entity parse string. */
    public abstract CEntityParseString NewEntityParseString(int l) ;
    /** Creates the entity multiply. */
    public abstract CEntityMultiply NewEntityMultiply(int l) ;
    /** Creates the entity divide. */
    public abstract CEntityDivide NewEntityDivide(int l) ;
    /** Creates the entity string concat. */
    public abstract CEntityStringConcat NewEntityStringConcat(int l) ;
    /** Creates the entity is field highlight. */
    public abstract CEntityIsFieldHighlight NewEntityIsFieldHighlight(CDataEntity ref) ;
    /** Creates the entity replace. */
    public abstract CEntityReplace NewEntityReplace(int l) ;
    /** Creates the entity data section. */
    public abstract CEntityDataSection NewEntityDataSection(int l, String name) ;
    /** Creates the entity is named condition. */
    public abstract CEntityIsNamedCondition NewEntityIsNamedCondition() ;
    /** Creates the entity subtract to. */
    public abstract CEntitySubtractTo NewEntitySubtractTo(int l) ;
    /** Creates the entity move reference. */
    public abstract CEntityMoveReference NewEntityMoveReference(int l) ;
    /** Creates the entity address reference. */
    public abstract CEntityAddressReference NewEntityAddressReference(CDataEntity ref) ;
    /** Creates the entity is field attribute. */
    public abstract CEntityIsFieldAttribute NewEntityIsFieldAttribute() ;
    /** Creates the entity is field color. */
    public abstract CEntityIsFieldColor NewEntityIsFieldColor() ;
    /** Creates the entity set constant. */
    public abstract CEntitySetConstant NewEntitySetConstant(int l) ;
    /** Creates the entity is field flag. */
    public abstract CEntityIsFieldFlag NewEntityIsFieldFlag() ;
    /** Creates the entity cond is constant. */
    public abstract CEntityCondIsConstant NewEntityCondIsConstant() ;
    /** Creates the entity cond is kind of. */
    public abstract CEntityCondIsKindOf NewEntityCondIsKindOf() ;
    /** Creates the entity cond is all. */
    public abstract CEntityCondIsAll NewEntityCondIsAll() ;
    /** Creates the entity string. */
    public abstract CEntityString NewEntityString(char[] value) ;
    /** Creates the entity string. */
    public CEntityString NewEntityString(String value)
    {
        char arr[] = value.toCharArray() ;
        return NewEntityString(arr) ;
    }
    /** Creates the entity cond or. */
    public abstract CEntityCondOr NewEntityCondOr() ;
    /** Creates the entity number. */
    public abstract CEntityNumber NewEntityNumber(String value) ;
    /** Creates the entity number. */
    public CEntityNumber NewEntityNumber(int value)
    {
        return NewEntityNumber(String.valueOf(value)) ;
    }
    /** Creates the entity expr terminal. */
    public abstract CEntityExprTerminal NewEntityExprTerminal(CDataEntity eData) ;
    /** Creates the entity expr sum. */
    public abstract CEntityExprSum NewEntityExprSum() ;
    /** Creates the entity expr prod. */
    public abstract CEntityExprProd NewEntityExprProd() ;
    /** Creates the entity cond not. */
    public abstract CEntityCondNot NewEntityCondNot() ;
    /** Creates the entity cond equals. */
    public abstract CEntityCondEquals NewEntityCondEquals() ;
    /** Creates the entity cond compare. */
    public abstract CEntityCondCompare NewEntityCondCompare() ;
    /** Creates the entity cond and. */
    public abstract CEntityCondAnd NewEntityCondAnd() ;
    /** Creates the entity class. */
    public abstract CEntityClass NewEntityClass(int l, String name) ;
    /** Creates the entity comment. */
    public abstract CEntityComment NewEntityComment(int l, String comment) ;
    /** Creates the entity attribute. */
    public abstract CEntityAttribute NewEntityAttribute(int l, String name) ;
    /** Creates the entity structure. */
    public abstract CEntityStructure NewEntityStructure(int l, String name, String level) ;
    /** Creates the entity procedure. */
    public abstract CEntityProcedure NewEntityProcedure(int l, String name, CEntityProcedureSection section) ;
    /** Creates the entity procedure section. */
    public abstract CEntityProcedureSection NewEntityProcedureSection(int l, String name) ;
    /** Creates the entity assign. */
    public abstract CEntityAssign NewEntityAssign(int l) ;
    /** Creates the entity assign special. */
    public abstract CEntityAssignSpecial NewEntityAssignSpecial(int l) ;
    /** Creates the entity external data structure. */
    public abstract CEntityExternalDataStructure NewEntityExternalDataStructure(int l, String name) ;
    /** Creates the entity inline. */
    public abstract CEntityInline NewEntityInline(int l, CBaseExternalEntity ext) ;
    /** Creates the entity condition. */
    public abstract CEntityCondition NewEntityCondition(int l) ;
    /** Creates the entity bloc. */
    public abstract CEntityBloc NewEntityBloc(int l) ;
    /** Creates the entity calcul. */
    public abstract CEntityCalcul NewEntityCalcul(int l) ;
    /** Creates the entity sqlon error goto. */
    public abstract CEntitySqlOnErrorGoto NewEntitySQLOnErrorGoto(int l, String ref) ;
    /** Creates the entity sqlon warning goto. */
    public abstract CEntitySqlOnErrorGoto NewEntitySQLOnWarningGoto(int l, String ref) ;
    /** Creates the entity exec. */
    public abstract CEntityExec NewEntityExec(int l, String statement) ;
    /** Creates the entity form container. */
    public abstract CEntityResourceFormContainer NewEntityFormContainer(int l, String name, boolean bSavCopy) ;
    /** Creates the entity form. */
    public abstract CEntityResourceForm NewEntityForm(int l, String name, boolean bSavCopy) ;
    /** Creates the entity entry field. */
    public abstract CEntityResourceField NewEntityEntryField(int l, String name) ;
    /** Creates the entity label field. */
    public abstract CEntityResourceField NewEntityLabelField(int l) ;
    /** Creates the entity call function. */
    public abstract CEntityCallFunction NewEntityCallFunction(int l, String reference, String refThru, CEntityProcedureSection section) ;
    /** Creates the entity initialize. */
    public abstract CEntityInitialize NewEntityInitialize(int l, CDataEntity data) ;
    /** Creates the entity return. */
    public abstract CEntityReturn NewEntityReturn(int l) ;
    /** Creates the entity call program. */
    public abstract CEntityCallProgram NewEntityCallProgram(int l, CDataEntity reference) ;
    /** Creates the entity switch case. */
    public abstract CEntitySwitchCase NewEntitySwitchCase(int l) ;
    /** Creates the entity case. */
    public abstract CEntityCase NewEntityCase(int l, int endline) ;
    /** Creates the entity sub string. */
    public abstract CSubStringAttributReference NewEntitySubString(int l) ;
    /** Creates the entity array reference. */
    public abstract CEntityArrayReference NewEntityArrayReference(int l) ;
    /** Creates the entity goto. */
    public abstract CEntityGoto NewEntityGoto(int l, String reference, CEntityProcedureSection section) ;
    /** Creates the entity goto depending. */
    public abstract CBaseActionEntity NewEntityGotoDepending(int l, List<String> refs, CDataEntity dep, CEntityProcedureSection section) ;
    /** Creates the entity loop while. */
    public abstract CEntityLoopWhile NewEntityLoopWhile(int l) ;
    /** Creates the entity loop iter. */
    public abstract CEntityLoopIter NewEntityLoopIter(int l) ;
    /** Creates the entity add to. */
    public abstract CEntityAddTo NewEntityAddTo(int l) ;
    /** Creates the entity continue. */
    public abstract CEntityContinue NewEntityContinue(int l) ;
    /** Creates the entity next sentence. */
    public abstract CEntityNextSentence NewEntityNextSentence(int l) ;
    /** Creates the entity named condition. */
    public abstract CEntityNamedCondition NewEntityNamedCondition(int l, String name) ;
    /** Creates the entity sqlsingle statement. */
    public abstract CEntitySQLSingleStatement NewEntitySQLSingleStatement(int l, String name) ;


    /** Creates the entity sqlselect statement. */
    public abstract CEntitySQLSelectStatement NewEntitySQLSelectStatement(
        int l,
        String name,
        Vector<CDataEntity> arrParameters,
        Vector<CDataEntity> arrInto,
        Vector<CDataEntity> arrInd) ;
    /** Creates the entity sqlcursor select statement. */
    public abstract CEntitySQLCursorSelectStatement NewEntitySQLCursorSelectStatement(int l) ;
    /** Creates the entity sqlfetch statement. */
    public abstract CEntitySQLFetchStatement NewEntitySQLFetchStatement(int l, CEntitySQLCursor cur) ;
    /** Creates the entity sqlopen statement. */
    public abstract CEntitySQLOpenStatement NewEntitySQLOpenStatement(int l, CEntitySQLCursor cur) ;
    /** Creates the entity sqlclose statement. */
    public abstract CEntitySQLCloseStatement NewEntitySQLCloseStatement(int l, CEntitySQLCursor cur) ;
    /** Creates the entity sqldelete statement. */
    public abstract CEntitySQLDeleteStatement NewEntitySQLDeleteStatement(int l, String csStatement, Vector<CDataEntity> arrParameters) ;
    /** Creates the entity sqlupdate statement. */
    public abstract CEntitySQLUpdateStatement NewEntitySQLUpdateStatement(
        int l,
        String csStatement,
        Vector<CDataEntity> arrSets,
        Vector<CDataEntity> arrParameters) ;
    /** Creates the entity sqlinsert statement. */
    public abstract CEntitySQLInsertStatement NewEntitySQLInsertStatement(int l) ;
    /** Creates the entity sqldeclare table. */
    public abstract CEntitySQLDeclareTable NewEntitySQLDeclareTable(
        int nLine,
        String csTableName,
        String csViewName,
        ArrayList arrTableColDescription);


    /** Creates the entity set color. */
    public abstract CEntitySetColor NewEntitySetColor(int l, CDataEntity field) ;
    /** Creates the entity field lengh. */
    public abstract CEntityFieldLength NewEntityFieldLengh(int l, String name, CDataEntity field) ;
    /** Creates the entity field data. */
    public abstract CEntityFieldData NewEntityFieldData(int l, String name, CDataEntity field) ;
    /** Creates the entity field color. */
    public abstract CEntityFieldColor NewEntityFieldColor(int l, String name, CDataEntity field) ;
    /** Creates the entity field attribute. */
    public abstract CEntityFieldAttribute NewEntityFieldAttribute(int l, String name, CDataEntity field) ;
    /** Creates the entity field highlight. */
    public abstract CEntityFieldHighlight NewEntityFieldHighlight(int l, String name, CDataEntity field) ;
    /** Creates the entity field flag. */
    public abstract CEntityFieldFlag NewEntityFieldFlag(int l, String name, CDataEntity field) ;
    /** Creates the entity field validated. */
    public abstract CEntityFieldValidated NewEntityFieldValidated(int l, String name, CDataEntity field) ;
    /** Creates the entity set highlight. */
    public abstract CEntitySetHighligh NewEntitySetHighlight(int l, CDataEntity field) ;
    /** Creates the entity set flag. */
    public abstract CEntitySetFlag NewEntitySetFlag(int l, CDataEntity field) ;
    /** Creates the entity set cursor. */
    public abstract CEntitySetCursor NewEntitySetCursor(int l, CDataEntity field) ;
    /** Creates the entity set attribute. */
    public abstract CEntitySetAttribute NewEntitySetAttribute(int l, CDataEntity field) ;

    /** Creates the entity assign with accessor. */
    public abstract CEntityAssignWithAccessor NewEntityAssignWithAccessor(int l) ;
    /** Creates the resource string. */
    public abstract CResourceStrings NewResourceString(int nbLines, int nbCols) ;
    /** Creates the entity environment variable. */
    public abstract CEntityEnvironmentVariable NewEntityEnvironmentVariable(String namev, String acc, boolean bNumeric) ;
    /** Creates the entity environment variable. */
    public abstract CEntityEnvironmentVariable NewEntityEnvironmentVariable(String namev, String acc, String write, boolean bNumeric) ;
//  public abstract CEntityFormAccessor NewEntityFormAccessor(int l, String name, CEntityResourceForm owner) ;
    /** Creates the entity working skip field. */
    public abstract CEntitySkipFields NewEntityWorkingSkipField(int l, String name, int nbFields, String level) ;
    /** Creates the entity field redefine. */
    public abstract CEntityFieldRedefine NewEntityFieldRedefine(int l, String name, String level) ;
    /** Creates the entity form redefine. */
    public abstract CEntityFormRedefine NewEntityFormRedefine(int l, String name, CDataEntity eForm, boolean bSaveMap) ;

    public CObjectCatalog programCatalog = null ;
    public String getOutputDirectory()
    {
        return "";
    }
    /** Returns the special constant value. */
    public abstract CDataEntity getSpecialConstantValue(String value) ;
    /** Returns the all special constant attributes. */
    public abstract Collection<CDataEntity> getAllSpecialConstantAttributes() ;
    /** Adds the special constant value. */
    public abstract void addSpecialConstantValue(String value, String constant) ;
    /** Creates the entity field array. */
    public abstract CEntityResourceFieldArray NewEntityFieldArray() ;
    /** Creates the entity sqlcode. */
    public abstract CEntitySQLCode NewEntitySQLCode(String name) ;
    /** Creates the entity sqlcode. */
    public abstract CEntitySQLCode NewEntitySQLCode(String name, CBaseEntityExpression eHistoryItem) ;
    /** Creates the entity cond is sqlcode. */
    public abstract CEntityCondIsSQLCode NewEntityCondIsSQLCode() ;
    /** Creates the entity routine emulation call. */
    public abstract CEntityRoutineEmulationCall NewEntityRoutineEmulationCall(int l) ;
    /** Creates the entity concat. */
    public abstract CEntityConcat NewEntityConcat(CDataEntity e1, CDataEntity e2) ;
    /** Creates the entity list. */
    public abstract CEntityList NewEntityList(String name) ;
    /** Creates the entity digits. */
    public abstract CEntityDigits NewEntityDigits(CDataEntity nel) ;
    /** Creates the entity search. */
    public abstract CEntitySearch NewEntitySearch(int line) ;
    /** Creates the entity internal bool. */
    public abstract CEntityInternalBool NewEntityInternalBool(String name) ;
    /** Creates the entity break. */
    public abstract CEntityBreak NewEntityBreak(int line) ;
    /** Creates the entity file descriptor. */
    public abstract CEntityFileDescriptor NewEntityFileDescriptor(int line, String name) ;
    /** Creates the entity sorted file descriptor. */
    public abstract CEntitySortedFileDescriptor NewEntitySortedFileDescriptor(int line, String name) ;
    /** Creates the entity file select. */
    public CEntityFileSelect NewEntityFileSelect(String ref) {
        return new CEntityFileSelect(ref, programCatalog);
    }
    /** Creates the entity open file. */
    public abstract CEntityOpenFile NewEntityOpenFile(int line) ;
    /** Creates the entity close file. */
    public abstract CEntityCloseFile NewEntityCloseFile(int line) ;
    /** Creates the entity read file. */
    public abstract CEntityReadFile NewEntityReadFile(int line) ;
    /** Creates the entity write file. */
    public abstract CEntityWriteFile NewEntityWriteFile(int line) ;
    /** Creates the entity accept. */
    public abstract CEntityAccept NewEntityAccept(int line) ;
    /** Creates the entity sort. */
    public abstract CEntitySort NewEntitySort(int line) ;
    /** Creates the entity sort release. */
    public abstract CEntitySortRelease NewEntitySortRelease(int line);
    /** Creates the entity sort return. */
    public abstract CEntitySortReturn NewEntitySortReturn(int line) ;
    /** Creates the entity value reference. */
    public CEntityValueReference NewEntityValueReference(CDataEntity dep)   {
        return new CEntityValueReference(programCatalog, dep);
    }
    /** Creates the entity rewrite file. */
    public abstract CEntityRewriteFile NewEntityRewriteFile(int line) ;

    /** Creates the entity field attribute reference. */
    public CEntityFieldAttributeReference NewEntityFieldAttributeReference(CDataEntity field)
    {
        return new CEntityFieldAttributeReference(programCatalog, field);
    }

    /** Creates the entity file buffer. */
    public CEntityFileBuffer NewEntityFileBuffer(String name, CEntityFileDescriptor att)    {
        return new CEntityFileBuffer(name, att, programCatalog) ;
    }

    /** Creates the entity address. */
    public abstract CEntityAddress NewEntityAddress(String csAddresse) ;
    /** Creates the entity function call. */
    public abstract CEntityFunctionCall NewEntityFunctionCall(String mehodName, CDataEntity object) ;
    /** Creates the entity cond is boolean. */
    public abstract CEntityCondIsBoolean NewEntityCondIsBoolean() ;
    /** Creates the entity sqllock. */
    public abstract CEntitySQLLock NewEntitySQLLock(int line) ;
    /** Creates the entity sqlsession declare. */
    public abstract CEntitySQLSessionDeclare NewEntitySQLSessionDeclare(int line) ;
    /** Creates the entity sqlsession drop. */
    public abstract CEntitySQLSessionDrop NewEntitySQLSessionDrop(int line) ;
    /** Creates the entity sqlexecute. */
    public abstract CEntitySQLExecute NewEntitySQLExecute(int line) ;
    /** Creates the entity formated var reference. */
    public abstract CEntityFormatedVarReference NewEntityFormatedVarReference(CDataEntity object, String format) ;
    /** Creates the entity inc. */
    public abstract CEntityInc NewEntityInc(int line) ;
    /** Creates the entity convert. */
    public abstract CEntityConvertReference NewEntityConvert(int line) ;
    /** Creates the entity is file eof. */
    public abstract CEntityIsFileEOF NewEntityIsFileEOF(CEntityFileDescriptor fb) ;
    /** Creates the entity constant. */
    public abstract CEntityConstant NewEntityConstant(CEntityConstant.Value val) ;
    /** Creates the entity file descriptor length dependency. */
    public abstract CEntityFileDescriptorLengthDependency NewEntityFileDescriptorLengthDependency(String name) ;
    /** Creates the entity sqlcall. */
    public abstract CEntitySQLCall NewEntitySQLCall(int line) ;

}
