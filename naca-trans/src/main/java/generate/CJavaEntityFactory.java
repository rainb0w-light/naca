/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 2 ao�t 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate;

import generate.java.CJavaAddressReference;
import generate.java.CJavaArrayReference;
import generate.java.CJavaAttribute;
import generate.java.CJavaBloc;
import generate.java.CJavaClass;
import generate.java.CJavaComment;
import generate.java.CJavaCondition;
import generate.java.CJavaDataSection;
import generate.java.CJavaEnvironmentVariable;
import generate.java.CJavaExternalDataStructure;
import generate.java.CJavaFileDescriptor;
import generate.java.CJavaFileDescriptorLengthDependency;
import generate.java.CJavaIndex;
import generate.java.CJavaInline;
import generate.java.CJavaMoveReference;
import generate.java.CJavaNamedCondition;
import generate.java.CJavaProcedure;
import generate.java.CJavaProcedureDivision;
import generate.java.CJavaProcedureSection;
import generate.java.CJavaSortedFileDescriptor;
import generate.java.CJavaStructure;
import generate.java.CJavaSubStringReference;
import generate.java.CJavaUnknownReference;
import generate.java.CICS.CJavaCICSAbend;
import generate.java.CICS.CJavaCICSAddress;
import generate.java.CICS.CJavaCICSAskTime;
import generate.java.CICS.CJavaCICSAssign;
import generate.java.CICS.CJavaCICSDeQ;
import generate.java.CICS.CJavaCICSDelay;
import generate.java.CICS.CJavaCICSDeleteQ;
import generate.java.CICS.CJavaCICSEnQ;
import generate.java.CICS.CJavaCICSGetMain;
import generate.java.CICS.CJavaCICSHandleAID;
import generate.java.CICS.CJavaCICSHandleCondition;
import generate.java.CICS.CJavaCICSIgnoreCondition;
import generate.java.CICS.CJavaCICSInquire;
import generate.java.CICS.CJavaCICSLink;
import generate.java.CICS.CJavaCICSReWrite;
import generate.java.CICS.CJavaCICSRead;
import generate.java.CICS.CJavaCICSReadQ;
import generate.java.CICS.CJavaCICSReceiveMap;
import generate.java.CICS.CJavaCICSRetrieve;
import generate.java.CICS.CJavaCICSReturn;
import generate.java.CICS.CJavaCICSSendMap;
import generate.java.CICS.CJavaCICSSetTDQueue;
import generate.java.CICS.CJavaCICSStart;
import generate.java.CICS.CJavaCICSStartBrowse;
import generate.java.CICS.CJavaCICSSyncPoint;
import generate.java.CICS.CJavaCICSWrite;
import generate.java.CICS.CJavaCICSWriteQ;
import generate.java.CICS.CJavaCICSXctl;
import generate.java.SQL.CJavaCondIsSQLCode;
import generate.java.SQL.CJavaSQLCall;
import generate.java.SQL.CJavaSQLCloseStatement;
import generate.java.SQL.CJavaSQLCode;
import generate.java.SQL.CJavaSQLCommit;
import generate.java.SQL.CJavaSQLCursor;
import generate.java.SQL.CJavaSQLCursorSection;
import generate.java.SQL.CJavaSQLCursorSelectStatement;
import generate.java.SQL.CJavaSQLDeclareTable;
import generate.java.SQL.CJavaSQLDeleteStatement;
import generate.java.SQL.CJavaSQLExecute;
import generate.java.SQL.CJavaSQLFetchStatement;
import generate.java.SQL.CJavaSQLInsertStatement;
import generate.java.SQL.CJavaSQLLock;
import generate.java.SQL.CJavaSQLOpenStatement;
import generate.java.SQL.CJavaSQLRollBack;
import generate.java.SQL.CJavaSQLSelectStatement;
import generate.java.SQL.CJavaSQLSessionDeclare;
import generate.java.SQL.CJavaSQLSessionDrop;
import generate.java.SQL.CJavaSQLSingleStatement;
import generate.java.SQL.CJavaSQLUpdateStatement;
import generate.java.SQL.CJavaSqlOnErrorGoto;
import generate.java.expressions.CJavaAddressOf;
import generate.java.expressions.CJavaConcat;
import generate.java.expressions.CJavaCondAnd;
import generate.java.expressions.CJavaCondCompare;
import generate.java.expressions.CJavaCondEquals;
import generate.java.expressions.CJavaCondIsAll;
import generate.java.expressions.CJavaCondIsConstant;
import generate.java.expressions.CJavaCondIsKindOf;
import generate.java.expressions.CJavaCondNot;
import generate.java.expressions.CJavaCondOr;
import generate.java.expressions.CJavaConstant;
import generate.java.expressions.CJavaConstantValue;
import generate.java.expressions.CJavaCurrentDate;
import generate.java.expressions.CJavaDigits;
import generate.java.expressions.CJavaEntityNumber;
import generate.java.expressions.CJavaExprOpposite;
import generate.java.expressions.CJavaExprProd;
import generate.java.expressions.CJavaExprSum;
import generate.java.expressions.CJavaExprTerminal;
import generate.java.expressions.CJavaInternalBool;
import generate.java.expressions.CJavaIsNamedCondition;
import generate.java.expressions.CJavaLengthOf;
import generate.java.expressions.CJavaList;
import generate.java.expressions.CJavaString;
import generate.java.forms.CJavaField;
import generate.java.forms.CJavaFieldArray;
import generate.java.forms.CJavaFieldArrayReference;
import generate.java.forms.CJavaFieldAttribute;
import generate.java.forms.CJavaFieldColor;
import generate.java.forms.CJavaFieldData;
import generate.java.forms.CJavaFieldFlag;
import generate.java.forms.CJavaFieldHighligh;
import generate.java.forms.CJavaFieldLength;
import generate.java.forms.CJavaFieldOccurs;
import generate.java.forms.CJavaFieldRedefine;
import generate.java.forms.CJavaFieldValidated;
import generate.java.forms.CJavaForm;
import generate.java.forms.CJavaFormContainer;
import generate.java.forms.CJavaFormRedefine;
import generate.java.forms.CJavaGetKeyPressed;
import generate.java.forms.CJavaIsFieldAttribute;
import generate.java.forms.CJavaIsFieldColor;
import generate.java.forms.CJavaIsFieldCursor;
import generate.java.forms.CJavaIsFieldFlag;
import generate.java.forms.CJavaIsFieldHighlight;
import generate.java.forms.CJavaIsFieldModified;
import generate.java.forms.CJavaIsKeyPressed;
import generate.java.forms.CJavaKeyPressed;
import generate.java.forms.CJavaLabelField;
import generate.java.forms.CJavaResetKeyPressed;
import generate.java.forms.CJavaResourceStrings;
import generate.java.forms.CJavaSetAttribute;
import generate.java.forms.CJavaSetColor;
import generate.java.forms.CJavaSetCursor;
import generate.java.forms.CJavaSetFlag;
import generate.java.forms.CJavaSetHighlight;
import generate.java.forms.CJavaSkipField;
import generate.java.verbs.CJavaAccept;
import generate.java.verbs.CJavaAddTo;
import generate.java.verbs.CJavaAssign;
import generate.java.verbs.CJavaAssignWithAccessor;
import generate.java.verbs.CJavaBreak;
import generate.java.verbs.CJavaCalcul;
import generate.java.verbs.CJavaCallFunction;
import generate.java.verbs.CJavaCallProgram;
import generate.java.verbs.CJavaCase;
import generate.java.verbs.CJavaCloseFile;
import generate.java.verbs.CJavaContinue;
import generate.java.verbs.CJavaCount;
import generate.java.verbs.CJavaDisplay;
import generate.java.verbs.CJavaDivide;
import generate.java.verbs.CJavaExec;
import generate.java.verbs.CJavaGoto;
import generate.java.verbs.CJavaGotoDepending;
import generate.java.verbs.CJavaInitialize;
import generate.java.verbs.CJavaInspectConverting;
import generate.java.verbs.CJavaLoopIter;
import generate.java.verbs.CJavaLoopWhile;
import generate.java.verbs.CJavaMultiply;
import generate.java.verbs.CJavaNextSentence;
import generate.java.verbs.CJavaOpenFile;
import generate.java.verbs.CJavaParseString;
import generate.java.verbs.CJavaReadFile;
import generate.java.verbs.CJavaReplace;
import generate.java.verbs.CJavaReturn;
import generate.java.verbs.CJavaRewriteFile;
import generate.java.verbs.CJavaRoutineEmulationCall;
import generate.java.verbs.CJavaSearch;
import generate.java.verbs.CJavaSetConstant;
import generate.java.verbs.CJavaSort;
import generate.java.verbs.CJavaSortRelease;
import generate.java.verbs.CJavaSortReturn;
import generate.java.verbs.CJavaStringConcat;
import generate.java.verbs.CJavaSubtractTo;
import generate.java.verbs.CJavaSwitchCase;
import generate.java.verbs.CJavaWriteFile;

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
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaEntityFactory extends CBaseEntityFactory
{

	public void InitCustomGlobalEntities(CGlobalCatalog cat)
	{		
		// manage HEXZONE
		CObjectCatalog ocat = new CObjectCatalog(cat, null, null, null) ;
		CEntityExternalDataStructure structure = new CJavaExternalDataStructure(0, "HEXZONE", ocat, null);
		structure.SetInline(true) ;
		CEntityAttribute att1 = new CJavaAttribute(0, "HEX-0E04", ocat, null) ;
		att1.SetTypeString(2) ;
		att1.SetInitialValue(getSpecialConstantValue("\u000E\u009C")) ;
		structure.AddChild(att1) ;
		ocat.RegisterAttribute(att1) ;
		CEntityAttribute att2 = new CJavaAttribute(0, "HEX-FF", ocat, null) ;
		att2.SetTypeString(1) ;
		att2.SetInitialValue(new CJavaString(ocat, null, new char[] {'\u00FF'})) ;
		structure.AddChild(att2) ;
		ocat.RegisterAttribute(att2) ;
		CEntityAttribute att3 = new CJavaAttribute(0, "HEX-80", ocat, null) ;
		att3.SetTypeString(1) ;
		att3.SetInitialValue(new CJavaString(ocat, null, new char[] {'\u0080'})) ;
		structure.AddChild(att3) ;
		ocat.RegisterAttribute(att3) ;
		cat.RegisterExternalDataStructure(structure) ;
	}
	
	public void InitCustomCICSEntities()
	{
		// some entries are no longer defined here : look into Pub2000/NacaTransRules.xml	
		NewEntitySQLCode("SQLCODE") ;
		NewEntitySQLCode("SQLERRD") ;
		
		
	}

	/**
	 * @param cat
	 */
	public CJavaEntityFactory(CObjectCatalog cat, CBaseLanguageExporter out)	{
		super(cat, out);
	}
	
	public CEntitySQLSelectStatement NewEntitySQLSelectStatement(int nLine, String csStatement, Vector<CDataEntity> arrParameters, Vector<CDataEntity> arrInto, Vector<CDataEntity> arrInd)	{
		return new CJavaSQLSelectStatement(nLine, programCatalog, langOutput, csStatement, arrParameters, arrInto, arrInd);
	}
	public CEntitySQLCursorSelectStatement NewEntitySQLCursorSelectStatement(int nLine)	{
		return new CJavaSQLCursorSelectStatement(nLine, programCatalog, langOutput);
	}
	public CEntitySQLFetchStatement NewEntitySQLFetchStatement(int nLine, CEntitySQLCursor cur)	{
		return new CJavaSQLFetchStatement(nLine, programCatalog, langOutput, cur);
	}
	public CEntitySQLOpenStatement NewEntitySQLOpenStatement(int nLine, CEntitySQLCursor cur)	{
		return new CJavaSQLOpenStatement(nLine, programCatalog, langOutput, cur);
	}
	public CEntitySQLCloseStatement NewEntitySQLCloseStatement(int nLine, CEntitySQLCursor cur)	{
		return new CJavaSQLCloseStatement(nLine, programCatalog, langOutput, cur);
	}
	public CEntitySQLDeleteStatement NewEntitySQLDeleteStatement(int nLine, String csStatement, Vector<CDataEntity> arrParameters)	{
		return new CJavaSQLDeleteStatement(nLine, programCatalog, langOutput, csStatement, arrParameters);
	}
	public CEntitySQLUpdateStatement NewEntitySQLUpdateStatement(int nLine, String csStatement, Vector<CDataEntity> arrSets, Vector<CDataEntity> arrParameters)	{
		return new CJavaSQLUpdateStatement(nLine, programCatalog, langOutput, csStatement, arrSets, arrParameters);
	}
	public CEntitySQLInsertStatement NewEntitySQLInsertStatement(int nLine)	{
		return new CJavaSQLInsertStatement(nLine, programCatalog, langOutput);
	}
	public CEntitySQLDeclareTable NewEntitySQLDeclareTable(int nLine, String csTableName, String csViewName, ArrayList arrTableColDescription)	{
		return new CJavaSQLDeclareTable(nLine, programCatalog, langOutput, csTableName, csViewName, arrTableColDescription);
	}
	public CEntityClass NewEntityClass(int l, String name)	{
		return new CJavaClass(l, name, programCatalog, langOutput);
	}
	public CEntityComment NewEntityComment(int l, String comment)	{
		return new CJavaComment(l, programCatalog, langOutput, comment);
	}
	public CEntityAttribute NewEntityAttribute(int l, String name)	{
		return new CJavaAttribute(l, name, programCatalog, langOutput);
	}
	public CEntityStructure NewEntityStructure(int l, String name, String level)	{
		return new CJavaStructure(l, name, programCatalog, langOutput, level);
	}
	public CEntityProcedure NewEntityProcedure(int l, String name, CEntityProcedureSection section)	{
		return new CJavaProcedure(l, name, programCatalog, langOutput, section);
	}
	public CEntityProcedureSection NewEntityProcedureSection(int l, String name)	{
		return new CJavaProcedureSection(l, name, programCatalog, langOutput);
	}
	public CEntityAssign NewEntityAssign(int l)	{
		return new CJavaAssign(l, programCatalog, langOutput) ;
	}
	public CEntityExternalDataStructure NewEntityExternalDataStructure(int l, String name)	{
		return new CJavaExternalDataStructure(l, name, programCatalog, langOutput);
	}
	public CEntityInline NewEntityInline(int l, CBaseExternalEntity ext)	{
		return new CJavaInline(l, programCatalog, langOutput, ext);
	}
	public CEntityCondition NewEntityCondition(int l)	{
		return new CJavaCondition(l, programCatalog, langOutput);
	}
	public CEntityBloc NewEntityBloc(int l)	{
		return new CJavaBloc(l, programCatalog, langOutput);
	}
	public CEntityCalcul NewEntityCalcul(int l)	{
		return new CJavaCalcul(l, programCatalog, langOutput);
	}
	public CEntitySqlOnErrorGoto NewEntitySQLOnErrorGoto(int l, String ref)	{
		return new CJavaSqlOnErrorGoto(l, programCatalog, langOutput, ref, false) ;
	}
	public CEntitySqlOnErrorGoto NewEntitySQLOnWarningGoto(int l, String ref)	{
		return new CJavaSqlOnErrorGoto(l, programCatalog, langOutput, ref, true) ;
	}
	public CEntityExec NewEntityExec(int l, String statement)	{
		return new CJavaExec(l, programCatalog, langOutput, statement);
	}
	public CEntityResourceFormContainer NewEntityFormContainer(int l, String name, boolean bSave)	{
		return new CJavaFormContainer(l, name, programCatalog, langOutput, bSave);
	}
	public CEntityResourceForm NewEntityForm(int l, String name, boolean bSave)	{
		return new CJavaForm(l, name, programCatalog, langOutput, bSave);
	}
	public CEntityFieldAttribute NewEntityFieldAttribute(int l, String name, CDataEntity owner)	{
		return new CJavaFieldAttribute(l, name, programCatalog, langOutput, owner);
	}
	public CEntityCallFunction NewEntityCallFunction(int l, String reference, String csRefThru, CEntityProcedureSection section)	{
		return new CJavaCallFunction(l, programCatalog, langOutput, reference, csRefThru, section);
	}
	public CEntityInitialize NewEntityInitialize(int l, CDataEntity data)	{
		return new CJavaInitialize(l, programCatalog, langOutput, data);
	}
	public CEntityReturn NewEntityReturn(int l)	{
		return new CJavaReturn(l, programCatalog, langOutput) ;
	}
	public CEntityCallProgram NewEntityCallProgram(int l, CDataEntity reference)	{
		return new CJavaCallProgram(l, programCatalog, langOutput, reference);
	}
	public CEntitySwitchCase NewEntitySwitchCase(int l)	{
		return new CJavaSwitchCase(l, programCatalog, langOutput) ;
	}
	public CEntityCase NewEntityCase(int l, int endline)	{
		return new CJavaCase(l, programCatalog, langOutput, endline);
	}
	public CSubStringAttributReference NewEntitySubString(int l)	{
		return new CJavaSubStringReference(l, programCatalog, langOutput);
	}
	public CEntityArrayReference NewEntityArrayReference(int l)	{
		return new CJavaArrayReference(l, programCatalog, langOutput);
	}
	public CEntityGoto NewEntityGoto(int l, String Reference, CEntityProcedureSection section)	{
		return new CJavaGoto(l, programCatalog, langOutput, Reference, section) ;
	}
	public CJavaGotoDepending NewEntityGotoDepending(int l, List<String> refs, CDataEntity dep, CEntityProcedureSection section)	{
		return new CJavaGotoDepending(l, programCatalog, langOutput, refs, dep, section) ;
	}
	public CEntityLoopWhile NewEntityLoopWhile(int l)	{
		return new CJavaLoopWhile(l, programCatalog, langOutput);
	}
	public CEntityLoopIter NewEntityLoopIter(int l)	{
		return new CJavaLoopIter(l, programCatalog, langOutput);
	}
	public CEntityAddTo NewEntityAddTo(int l)	{
		return new CJavaAddTo(l, programCatalog, langOutput);
	}
	public CEntityContinue NewEntityContinue(int l)	{
		return new CJavaContinue(l, programCatalog, langOutput);
	}
	public CEntityNextSentence NewEntityNextSentence(int l)	{
		return new CJavaNextSentence(l, programCatalog, langOutput);
	}
	public CEntityNamedCondition NewEntityNamedCondition(int l, String name)	{
		return new CJavaNamedCondition(l, name, programCatalog, langOutput);
	}
	public CEntitySQLSingleStatement NewEntitySQLSingleStatement(int l, String st)	{
		return new CJavaSQLSingleStatement(l, programCatalog, langOutput, st);
	}
	public CEntitySetColor NewEntitySetColor(int l, CDataEntity field)	{
		programCatalog.addImportDeclaration("MAP") ;
		return new CJavaSetColor(l, programCatalog, langOutput, field) ;
	}
	public CEntityFieldLength NewEntityFieldLengh(int l, String name, CDataEntity field)	{
		return new CJavaFieldLength(l, name, programCatalog, langOutput, field) ;
	}
	public CEntityFieldColor NewEntityFieldColor(int l, String name, CDataEntity field)	{
		return new CJavaFieldColor(l, name, programCatalog, langOutput, field) ;
	}
	public CEntityFieldHighlight NewEntityFieldHighlight(int l, String name, CDataEntity field)	{
		return new CJavaFieldHighligh(l, name, programCatalog, langOutput, field) ;
	}
//	public CEntityFieldFlag NewEntityFieldFlag(int l, String name, CBaseDataEntity field)
//	{
//		return new CJavaFieldFlag(l, name, programCatalog, langOutput, field) ;
//	}
	public CEntityFieldFlag NewEntityFieldFlag(int l, String name, CDataEntity field)	{
		return new CJavaFieldFlag(l, name, programCatalog, langOutput, field) ;
	}
	public CEntitySetHighligh NewEntitySetHighlight(int l, CDataEntity field)	{
		programCatalog.addImportDeclaration("MAP") ;
		return new CJavaSetHighlight(l, programCatalog, langOutput, field) ;
	}
	public CEntitySetFlag NewEntitySetFlag(int l, CDataEntity field)	{
		return new CJavaSetFlag(l, programCatalog, langOutput, field) ;
	}
	public CEntitySetCursor NewEntitySetCursor(int l, CDataEntity field)	{
		return new CJavaSetCursor(l, programCatalog, langOutput, field) ;
	}
	public CEntitySetAttribute NewEntitySetAttribute(int l, CDataEntity field)	{
		programCatalog.addImportDeclaration("MAP") ;
		return new CJavaSetAttribute(l, programCatalog, langOutput, field) ;
	}
	public CEntityAssignWithAccessor NewEntityAssignWithAccessor(int l)	{
		return new CJavaAssignWithAccessor(l, programCatalog, langOutput) ;
	}
	public CEntityFieldData NewEntityFieldData(int l, String name, CDataEntity field)	{
		return new CJavaFieldData(l, name, programCatalog, langOutput, field);
	}
	public CResourceStrings NewResourceString(int nbLines, int nbCols)	{
		return new CJavaResourceStrings(nbLines, nbCols);
	}
	public CEntityEnvironmentVariable NewEntityEnvironmentVariable(String name, String acc, boolean bNumeric)	{
		return new CJavaEnvironmentVariable(0, name, programCatalog, langOutput, acc, bNumeric);
	}
	public CEntityEnvironmentVariable NewEntityEnvironmentVariable(String name, String acc, String write, boolean bNumeric)	{
		return new CJavaEnvironmentVariable(0, name, programCatalog, langOutput, acc, write, bNumeric);
	}
//	public CEntityFormAccessor NewEntityFormAccessor(int l, String name, CEntityResourceForm owner)	{
//		return new CJavaFormAccessor(l, name, programCatalog, langOutput, owner);
//	}
	public CEntitySkipFields NewEntityWorkingSkipField(int l, String name, int nbFields, String level)	{
		return new CJavaSkipField(l, name, programCatalog, langOutput, nbFields, level);
	}
	public CEntityResourceField NewEntityEntryField(int l, String name)	{
		return new CJavaField(l, name, programCatalog, langOutput);	
	}
	public CEntityResourceField NewEntityLabelField(int l)	{
		return new CJavaLabelField(l, programCatalog, langOutput);
	}
	public CEntityFieldRedefine NewEntityFieldRedefine(int l, String name, String level)	{
		return new CJavaFieldRedefine(l, name, programCatalog, langOutput, level);	
	}
	public CEntityFormRedefine NewEntityFormRedefine(int l, String name, CDataEntity eForm, boolean bSaveMap)	{
		//programCatalog.addImportDeclaration("MAP") ;
		return new CJavaFormRedefine(l, name, programCatalog, langOutput, eForm, bSaveMap);
	}
	public CEntityString NewEntityString(char[] value)	{
		CJavaString e = new CJavaString(programCatalog, langOutput, value) ;
		return e ;
	}
	public CEntityCondOr NewEntityCondOr()	{
		return new CJavaCondOr();
	}
	public CEntityNumber NewEntityNumber(String value)	{
		return new CJavaEntityNumber(programCatalog, langOutput, value) ;
	}
	public CEntityExprTerminal NewEntityExprTerminal(CDataEntity eData)	{
		return new CJavaExprTerminal(eData);
	}
	public CEntityExprSum NewEntityExprSum()	{
		return new CJavaExprSum();
	}
	public CEntityExprProd NewEntityExprProd()	{
		return new CJavaExprProd();
	}
	public CEntityCondNot NewEntityCondNot()	{
		return new CJavaCondNot();
	}
	public CEntityCondEquals NewEntityCondEquals()	{
		return new CJavaCondEquals();
	}
	public CEntityCondCompare NewEntityCondCompare()	{
		return new CJavaCondCompare();
	}
	public CEntityCondAnd NewEntityCondAnd()	{
		return new CJavaCondAnd();
	}
	public CEntityCondIsAll NewEntityCondIsAll()	{
		return new CJavaCondIsAll();
	}
	public CEntityCondIsKindOf NewEntityCondIsKindOf()	{
		return new CJavaCondIsKindOf();
	}
	public CEntityCondIsConstant NewEntityCondIsConstant()	{
		return new CJavaCondIsConstant() ;
	}
	public CEntityIsFieldFlag NewEntityIsFieldFlag()	{
		return new CJavaIsFieldFlag();
	}
	public CEntitySetConstant NewEntitySetConstant(int l)	{
		return new CJavaSetConstant(l, programCatalog, langOutput);
	}
	public CEntityIsFieldColor NewEntityIsFieldColor()	{
		programCatalog.addImportDeclaration("MAP") ;
		return new CJavaIsFieldColor();
	}
	public CEntityIsFieldAttribute NewEntityIsFieldAttribute()	{
		programCatalog.addImportDeclaration("MAP") ;
		return new CJavaIsFieldAttribute() ;
	}
	public CEntityAddressReference NewEntityAddressReference(CDataEntity ref)	{
		return new CJavaAddressReference(programCatalog, langOutput, ref);
	}
	public CEntityMoveReference NewEntityMoveReference(int l)	{
		return new CJavaMoveReference(l, programCatalog, langOutput) ;
	}
	public CEntitySubtractTo NewEntitySubtractTo(int l)	{
		return new CJavaSubtractTo(l, programCatalog, langOutput);
	}
	public CEntityIsNamedCondition NewEntityIsNamedCondition()	{
		return new CJavaIsNamedCondition();
	}
	public CEntityDataSection NewEntityDataSection(int l, String name)	{
		return new CJavaDataSection(l, name, programCatalog, langOutput);
	}
	public CEntityReplace NewEntityReplace(int l)	{
		return new CJavaReplace(l, programCatalog, langOutput);
	}
	public CEntityIsFieldHighlight NewEntityIsFieldHighlight(CDataEntity ref)	{
		programCatalog.addImportDeclaration("MAP") ;
		return new CJavaIsFieldHighlight(ref) ;
	}
	public CEntityFieldValidated NewEntityFieldValidated(int l, String name, CDataEntity field)	{
		return new CJavaFieldValidated(l, name, programCatalog, langOutput, field) ;
	}
	public CEntityStringConcat NewEntityStringConcat(int l)	{
		return new CJavaStringConcat(l, programCatalog, langOutput);
	}
	public CEntityDivide NewEntityDivide(int l)	{
		return new CJavaDivide(l, programCatalog, langOutput);
	}
	public CEntityMultiply NewEntityMultiply(int l)	{
		return new CJavaMultiply(l, programCatalog, langOutput);
	}
	public CEntityParseString NewEntityParseString(int l)	{
		return new CJavaParseString(l, programCatalog, langOutput);
	}
	public CEntitySQLRollBack NewEntitySQLRollBack(int l)	{
		return new CJavaSQLRollBack(l, programCatalog, langOutput);
	}
	public CEntitySQLCommit NewEntitySQLCommit(int l)	{
		return new CJavaSQLCommit(l, programCatalog, langOutput);
	}
	public CEntityExprOpposite NewEntityExprOpposite()	{
		return new CJavaExprOpposite();
	}
	public CEntityCICSXctl NewEntityCICSXctl(int l)	{
		return new CJavaCICSXctl(l, programCatalog, langOutput);
	}
	public CEntityCICSLink NewEntityCICSLink(int l)	{
		return new CJavaCICSLink(l, programCatalog, langOutput);
	}
	public CEntityCICSAddress NewEntityCICSAddress(int l) {
		return new CJavaCICSAddress(l, programCatalog, langOutput);
	}
	public CEntityCICSAskTime NewEntityCICSAskTime(int l)	{
		return new CJavaCICSAskTime(l, programCatalog, langOutput);
	}
	public CEntityCurrentDate NewEntityCurrentDate()	{
		return new CJavaCurrentDate(programCatalog, langOutput);
	}
	public CEntityAddressOf NewEntityAddressOf(CDataEntity data)	{
		return new CJavaAddressOf(programCatalog, langOutput, data);
	}
	public CEntityLengthOf NewEntityLengthOf(CDataEntity data)	{
		return new CJavaLengthOf(programCatalog, langOutput, data);
	}
	public CEntityCICSHandleCondition NewEntityCICSHandleCondition(int l)	{
		return new CJavaCICSHandleCondition(l, programCatalog, langOutput);
	}
	public CEntityCICSHandleAID NewEntityCICSHandleAID(int l)	{
		return new CJavaCICSHandleAID(l, programCatalog, langOutput);
	}
	public CEntityCICSIgnoreCondition NewEntityCICSIgnoreCondition(int l)	{
		return new CJavaCICSIgnoreCondition(l, programCatalog, langOutput);
	}
	public CEntityCICSRetrieve NewEntityCICSRetreive(int l, boolean bPointer)	{
		return new CJavaCICSRetrieve(l, programCatalog, langOutput, bPointer);
	}
	public CEntityCICSStart NewEntityCICSStart(int l, CDataEntity TID)	{
		return new CJavaCICSStart(l, programCatalog, langOutput, TID);
	}
	public CEntityCICSReturn NewEntityCICSReturn(int l)	{
		return new CJavaCICSReturn(l, programCatalog, langOutput);
	}
	public CEntityCICSSendMap NewEntityCICSSendMap(int l)	{
		return new CJavaCICSSendMap(l, programCatalog, langOutput);
	}
	public CEntityCICSWrite NewEntityCICSWrite(int l)	{
		return new CJavaCICSWrite(l, programCatalog, langOutput);
	}
	public CEntityCICSReceiveMap NewEntityCICSReceiveMap(int l, CDataEntity name)	{
		return new CJavaCICSReceiveMap(l, programCatalog, langOutput, name);
	}
	public CEntityIsFieldModified NewEntityIsFieldModified() {
		return new CJavaIsFieldModified();
	}
	public CEntityCICSSyncPoint NewEntityCICSSyncPoint(int l, boolean bRollBack)	{
		return new CJavaCICSSyncPoint(l, programCatalog, langOutput, bRollBack);
	}
	public CEntityCICSInquire NewEntityCICSInquire(int l)	{
		return new CJavaCICSInquire(l, programCatalog, langOutput);
	}
	public CEntityCICSAbend NewEntityCICSAbend(int l)	{
		return new CJavaCICSAbend(l, programCatalog, langOutput);
	}
	public CEntityCICSRead NewEntityCICSRead(int l, CEntityCICSRead.CEntityCICSReadMode mode)	{
		return new CJavaCICSRead(l, programCatalog, langOutput, mode);
	}
	public CEntityCICSStartBrowse NewEntityCICSStartBrowse(int l)	{
		return new CJavaCICSStartBrowse(l, programCatalog, langOutput);
	}
	public CEntityCICSDeleteQ NewEntityCICSDeleteQ(int l, boolean b)	{
		return new CJavaCICSDeleteQ(l, programCatalog, langOutput, b);
	}
	public CEntityCICSWriteQ NewEntityCICSWriteQ(int l, boolean b)	{
		return new CJavaCICSWriteQ(l, programCatalog, langOutput, b);
	}
	public CEntityCICSReadQ NewEntityCICSReadQ(int l, boolean b)	{
		return new CJavaCICSReadQ(l, programCatalog, langOutput, b);
	}
	public CEntityCICSAssign NewEntityCICSAssign(int l)	{
		return new CJavaCICSAssign(l, programCatalog, langOutput);
	}
	public CEntityDisplay NewEntityDisplay(int l, Upon t)	{
		return new CJavaDisplay(l, programCatalog, langOutput, t);
	}
	public CEntityCount NewEntityCount(int l)	{
		return new CJavaCount(l, programCatalog, langOutput);
	}
	public CEntityInspectConverting NewEntityInspectConverting(int l) {
		return new CJavaInspectConverting(l, programCatalog, langOutput);
	}
	public CEntityCICSReWrite NewEntityCICSReWrite(int l)	{
		return new CJavaCICSReWrite(l, programCatalog, langOutput);
	}
	public CEntityCICSDelay NewEntityCICSDelay(int l)	{
		return new CJavaCICSDelay(l, programCatalog, langOutput);
	}
	public CEntityCICSSetTDQueue NewEntityCICSSetTDQueue(int l)	{
		return new CJavaCICSSetTDQueue(l, programCatalog, langOutput);
	}
	public CEntityCICSDeQ NewEntityCICSDeQ(int l)	{
		return new CJavaCICSDeQ(l, programCatalog, langOutput);
	}
	public CEntityCICSEnQ NewEntityCICSEnQ(int l)	{
		return new CJavaCICSEnQ(l, programCatalog, langOutput);
	}
	public CEntityProcedureDivision NewEntityProcedureDivision(int l)	{
		return new CJavaProcedureDivision(l, programCatalog, langOutput);
	}
	public CEntitySQLCursorSection NewEntitySQLCursorSection()	{
		return new CJavaSQLCursorSection(programCatalog, langOutput);
	}
	public CEntityFieldArrayReference NewEntityFieldArrayReference(int l)	{
		return new CJavaFieldArrayReference(l, programCatalog, langOutput);
	}
	public CEntityIndex NewEntityIndex(String name)	{
		return new CJavaIndex(name, programCatalog, langOutput);
	}
	public CEntitySQLCursor NewEntitySQLCursor(String name)	{
		return new CJavaSQLCursor(name, programCatalog, langOutput);
	}
	public CEntityKeyPressed NewEntityKeyPressed(String name, String caption)	{
		//programCatalog.UseMapSupport() ;
		return new CJavaKeyPressed(0, name, programCatalog, langOutput, caption);
	}
	public CEntityGetKeyPressed NewEntityGetKeyPressed(String name)	{
		return new CJavaGetKeyPressed(name, programCatalog, langOutput);
	}
	public CEntityIsKeyPressed NewEntityIsKeyPressed()	{
		programCatalog.addImportDeclaration("KEYPRESSED") ;
		return new CJavaIsKeyPressed();
	}
	public CEntityFieldOccurs NewEntityFieldOccurs(int l, String name)	{
		return new CJavaFieldOccurs(l, name, programCatalog, langOutput);
	}
	public CEntityUnknownReference NewEntityUnknownReference(int nLine, String csName)
	{
		return new CJavaUnknownReference(nLine, csName, programCatalog, langOutput);
	}
	public CEntityCICSGetMain NewEntityCICSGetMain(int l)	{
		return new CJavaCICSGetMain(l, programCatalog, langOutput);
	}
	public CEntityResetKeyPressed NewEntityResetKeyPressed(int l)	{
		return new CJavaResetKeyPressed(l, programCatalog, langOutput);
	}
	public CEntityResourceFieldArray NewEntityFieldArray()	{
		return new CJavaFieldArray(0, "", programCatalog, langOutput);
	}
	public CEntitySQLCode NewEntitySQLCode(String name)	{
		return new CJavaSQLCode(name, programCatalog, langOutput);
	}
	public CEntitySQLCode NewEntitySQLCode(String name, CBaseEntityExpression eHistoryItem)	{
		return new CJavaSQLCode(name, programCatalog, langOutput, eHistoryItem);
	}
	public CEntityCondIsSQLCode NewEntityCondIsSQLCode()	{
		programCatalog.addImportDeclaration("SQL") ;
		return new CJavaCondIsSQLCode();
	}
	public CEntityRoutineEmulationCall NewEntityRoutineEmulationCall(int l)	{
		return new CJavaRoutineEmulationCall(l, programCatalog, langOutput) ;
	}

	protected Hashtable<String, CDataEntity> tabConstantValues = new Hashtable<String, CDataEntity>() ; 
	public void addSpecialConstantValue(String value, String constant)
	{
		tabConstantValues.put(value, new CJavaConstantValue(programCatalog, langOutput, constant));
	}
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
					return new CJavaConstantValue(programCatalog, langOutput, code) ;
				}
			}
			return null;
		}
	}
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
	public CEntityConcat NewEntityConcat(CDataEntity e1, CDataEntity e2)	{
		return new CJavaConcat(programCatalog, langOutput, e1, e2) ;
	}
	public CEntityIsFieldCursor NewEntityIsFieldCursor()	{
		return new CJavaIsFieldCursor() ;
	}
	public CEntityList NewEntityList(String name)	{
		return new CJavaList(name, programCatalog, langOutput);
	}
	public CEntityDigits NewEntityDigits(CDataEntity nel)	{
		return new CJavaDigits(programCatalog, langOutput, nel);
	}
	public CEntitySearch NewEntitySearch(int line)	{
		return new CJavaSearch(line, programCatalog, langOutput) ;
	}
	public CEntityInternalBool NewEntityInternalBool(String name)	{
		return new CJavaInternalBool(name, programCatalog, langOutput) ;
	}
	public CEntityBreak NewEntityBreak(int line)	{
		return new CJavaBreak(line, programCatalog, langOutput) ;
	}
	public CEntityFileDescriptor NewEntityFileDescriptor(int line, String name) {
		return new CJavaFileDescriptor(line, name, programCatalog, langOutput) ;
	}
	public CEntitySortedFileDescriptor NewEntitySortedFileDescriptor(int line, String name)	{
		return new CJavaSortedFileDescriptor(line, name, programCatalog, langOutput) ;
	}
	public CEntityOpenFile NewEntityOpenFile(int line) 	{
		return new CJavaOpenFile(line, programCatalog, langOutput);
	}
	public CEntityCloseFile NewEntityCloseFile(int line)	{
		return new CJavaCloseFile(line, programCatalog, langOutput);
	}
	public CEntityReadFile NewEntityReadFile(int line)	{
		return new CJavaReadFile(line, programCatalog, langOutput);
	}
	public CEntityWriteFile NewEntityWriteFile(int line) {
		return new CJavaWriteFile(line, programCatalog, langOutput);
	}
	public CEntityAccept NewEntityAccept(int line){
		return new CJavaAccept(line, programCatalog, langOutput);
	}
	public CEntitySort NewEntitySort(int line)	{
		return new CJavaSort(line, programCatalog, langOutput);
	}
	public CEntitySortRelease NewEntitySortRelease(int line)	{
		return new CJavaSortRelease(line, programCatalog, langOutput);
	}
	public CEntitySortReturn NewEntitySortReturn(int line)	{
		return new CJavaSortReturn(line, programCatalog, langOutput);
	}
	public CEntityRewriteFile NewEntityRewriteFile(int line)	{
		return new CJavaRewriteFile(line, programCatalog, langOutput);
	}
	public CEntityAddress NewEntityAddress(String csAddresse)	{
		throw new NacaTransAssertException("Method not implemented") ;
	}
	public CEntityFunctionCall NewEntityFunctionCall(String mehodName, CDataEntity object)	{
		throw new NacaTransAssertException("Method not implemented") ;
	}
	public CEntityCondIsBoolean NewEntityCondIsBoolean()	{
		throw new NacaTransAssertException("Method not implemented") ;
	}
	public CEntitySQLSessionDeclare NewEntitySQLSessionDeclare(int line)	{
		return new CJavaSQLSessionDeclare(line, programCatalog, langOutput) ;
	}
	public CEntitySQLSessionDrop NewEntitySQLSessionDrop(int line)	{
		return new CJavaSQLSessionDrop(line, programCatalog, langOutput) ;
	}
	public CEntitySQLLock NewEntitySQLLock(int line)	{
		return new CJavaSQLLock(line, programCatalog, langOutput) ;
	}
	public CEntitySQLExecute NewEntitySQLExecute(int line)	{
		return new CJavaSQLExecute(line, programCatalog, langOutput) ;
	}
	public CEntityFormatedVarReference NewEntityFormatedVarReference(CDataEntity object, String format)	{
		throw new NacaTransAssertException("Method not implemented") ;
	}
	public CEntityInc NewEntityInc(int line)	{
		throw new NacaTransAssertException("Method not implemented") ;
	}
	public CEntityConvertReference NewEntityConvert(int line)	{
		throw new NacaTransAssertException("Method not implemented") ;
	}
	public CEntityIsFileEOF NewEntityIsFileEOF(CEntityFileDescriptor fb)	{
		throw new NacaTransAssertException("Method not implemented") ;
	}
	public CEntityConstant NewEntityConstant(Value val) {
		return new CJavaConstant(val) ;
	}
	public CEntityFileDescriptorLengthDependency NewEntityFileDescriptorLengthDependency(String name)	{
		return new CJavaFileDescriptorLengthDependency(name, programCatalog, langOutput) ;
	}
	public CEntityAssignSpecial NewEntityAssignSpecial(int l)	{
		throw new NacaTransAssertException("Method not implemented") ;
	}
	public CEntitySQLCall NewEntitySQLCall(int line) {
		return new CJavaSQLCall(line, programCatalog, langOutput);
	}


}
