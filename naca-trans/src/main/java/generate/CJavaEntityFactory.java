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
import generate.java.CJavaClass;
import generate.java.CJavaComment;
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
import generate.java.expressions.CJavaIntrinsicFunction;
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
		// Direct backend CJavaSQLSelectStatement retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (the recursiveSQLSelectStatementEntity
		// binding). Every host-variable parameter, INTO target and optional INDICATOR
		// remain semantic children rendered through their reference bindings; the
		// SQLWARNING/SQLERROR clause is a read-only catalog lookup the template chains
		// onto the sql(...) runtime call. No generated string is materialized in the
		// factory.
		CEntitySQLSelectStatement e = new CEntitySQLSelectStatement(nLine, programCatalog, csStatement, arrParameters, arrInto, arrInd);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntitySQLCursorSelectStatement NewEntitySQLCursorSelectStatement(int nLine)	{
		// Direct backend CJavaSQLCursorSelectStatement retired: the pure semantic
		// entity is rendered by the recursive ST4 assembler (the
		// recursiveSQLCursorSelectStatementEntity binding) in place of the OPEN
		// <cursor> statement. The cursor and each host-variable parameter remain
		// semantic children rendered through their reference bindings; no generated
		// string is materialized in the factory.
		CEntitySQLCursorSelectStatement e = new CEntitySQLCursorSelectStatement(nLine, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntitySQLFetchStatement NewEntitySQLFetchStatement(int nLine, CEntitySQLCursor cur)	{
		// Direct backend CJavaSQLFetchStatement retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (the recursiveSQLFetchStatementEntity
		// binding). The cursor, every INTO target and every optional INDICATOR remain
		// semantic children rendered through their reference bindings; the
		// SQLWARNING/SQLERROR clause is a read-only catalog lookup the template chains
		// onto cursorFetch(...). No generated string is materialized in the factory.
		CEntitySQLFetchStatement e = new CEntitySQLFetchStatement(nLine, programCatalog, cur);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntitySQLOpenStatement NewEntitySQLOpenStatement(int nLine, CEntitySQLCursor cur)	{
		// Direct backend CJavaSQLOpenStatement retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (the recursiveSQLOpenStatementEntity
		// binding). The cursor and the optional USING descriptor/host variable remain
		// semantic children rendered through their reference bindings; a cursor with a
		// bound SELECT renders that SELECT child in place of the OPEN statement; the
		// SQLWARNING/SQLERROR clause is a read-only catalog lookup the template chains
		// onto cursorOpen(...). No generated string is materialized in the factory.
		CEntitySQLOpenStatement e = new CEntitySQLOpenStatement(nLine, programCatalog, cur);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntitySQLCloseStatement NewEntitySQLCloseStatement(int nLine, CEntitySQLCursor cur)	{
		// Direct backend CJavaSQLCloseStatement retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler. The cursor remains a semantic child
		// and is recursively rendered through its reference binding; no generated
		// string is materialized in the factory.
		CEntitySQLCloseStatement e = new CEntitySQLCloseStatement(nLine, programCatalog, cur);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntitySQLDeleteStatement NewEntitySQLDeleteStatement(int nLine, String csStatement, Vector<CDataEntity> arrParameters)	{
		// Direct backend CJavaSQLDeleteStatement retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (the recursiveSQLDeleteStatementEntity
		// binding). The bound cursor and each host-variable parameter remain semantic
		// children rendered through their reference bindings; the SQLWARNING/SQLERROR
		// clause is a read-only catalog lookup the template chains onto the
		// sql(...)/cursorDeleteCurrent(...) runtime call. No generated string is
		// materialized in the factory.
		CEntitySQLDeleteStatement e = new CEntitySQLDeleteStatement(nLine, programCatalog, csStatement, arrParameters);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntitySQLUpdateStatement NewEntitySQLUpdateStatement(int nLine, String csStatement, Vector<CDataEntity> arrSets, Vector<CDataEntity> arrParameters)	{
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
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntitySQLInsertStatement NewEntitySQLInsertStatement(int nLine)	{
		// Direct backend CJavaSQLInsertStatement retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (the recursiveSQLInsertStatementEntity
		// binding). The full INSERT INTO ... text is assembled in Stage 1; the
		// non-inlined VALUES entries and the INSERT...SELECT host parameters remain
		// semantic children rendered through their reference bindings as 1-based
		// .value(N, <ref>) calls, and the SQLWARNING/SQLERROR clause is a read-only
		// catalog lookup the template chains onto the sql(...) runtime call. No
		// generated string is materialized in the factory.
		CEntitySQLInsertStatement e = new CEntitySQLInsertStatement(nLine, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntitySQLDeclareTable NewEntitySQLDeclareTable(int nLine, String csTableName, String csViewName, ArrayList arrTableColDescription)	{
		// Direct backend CJavaSQLDeclareTable retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (recursiveSQLDeclareTableEntity
		// binding). The statement emits no code; its effect is the Stage-1 catalog
		// side effect RegisterSQLTable(csViewName, this) applied in the entity
		// constructor during semantic analysis. Mirrors the CEntitySqlOnErrorGoto
		// (WHENEVER) retirement.
		CEntitySQLDeclareTable e = new CEntitySQLDeclareTable(nLine, programCatalog, csTableName, csViewName, arrTableColDescription);
		e.setLanguageExporter(langOutput);
		return e;
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
		CEntityAssign e = new CEntityAssign(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityExternalDataStructure NewEntityExternalDataStructure(int l, String name)	{
		return new CJavaExternalDataStructure(l, name, programCatalog, langOutput);
	}
	public CEntityInline NewEntityInline(int l, CBaseExternalEntity ext)	{
		return new CJavaInline(l, programCatalog, langOutput, ext);
	}
	public CEntityCondition NewEntityCondition(int l)	{
		return new CEntityCondition(l, programCatalog);
	}
	public CEntityBloc NewEntityBloc(int l)	{
		return new CEntityBloc(l, programCatalog);
	}
	public CEntityCalcul NewEntityCalcul(int l)	{
		CEntityCalcul e = new CEntityCalcul(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntitySqlOnErrorGoto NewEntitySQLOnErrorGoto(int l, String ref)	{
		// Direct backend CJavaSqlOnErrorGoto retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (recursiveSqlOnErrorGotoEntity
		// binding). The WHENEVER policy is a Stage-1 catalog side effect registered
		// here, in program order, during semantic analysis; the template emits no code.
		CEntitySqlOnErrorGoto e = new CEntitySqlOnErrorGoto(l, programCatalog, ref, false) ;
		e.setLanguageExporter(langOutput);
		registerSqlWheneverPolicy(ref, false);
		return e;
	}
	public CEntitySqlOnErrorGoto NewEntitySQLOnWarningGoto(int l, String ref)	{
		// Direct backend CJavaSqlOnErrorGoto retired (see NewEntitySQLOnErrorGoto).
		CEntitySqlOnErrorGoto e = new CEntitySqlOnErrorGoto(l, programCatalog, ref, true) ;
		e.setLanguageExporter(langOutput);
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
	protected void registerSqlWheneverPolicy(String ref, boolean onWarning)	{
		if (onWarning)	{
			if (ref.equals(""))	{
				programCatalog.registerSQLWarningContinue(null);
			}
			else	{
				programCatalog.registerSQLWarningGoto(langOutput.FormatIdentifier(ref));
			}
		}
		else	{
			if (ref.equals(""))	{
				programCatalog.RegisterSQLErrorContinue(null);
			}
			else	{
				programCatalog.registerSQLErrorGoto(langOutput.FormatIdentifier(ref));
			}
		}
	}
	public CEntityExec NewEntityExec(int l, String statement)	{
		CEntityExec e = new CEntityExec(l, programCatalog, statement);
		e.setLanguageExporter(langOutput);
		return e;
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
		CEntityCallFunction e = new CEntityCallFunction(
			l, programCatalog, reference, csRefThru, section);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityInitialize NewEntityInitialize(int l, CDataEntity data)	{
		CEntityInitialize e = new CEntityInitialize(l, programCatalog, data);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityReturn NewEntityReturn(int l)	{
		CEntityReturn e = new CEntityReturn(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCallProgram NewEntityCallProgram(int l, CDataEntity reference)	{
		CEntityCallProgram e = new CEntityCallProgram(l, programCatalog, reference);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntitySwitchCase NewEntitySwitchCase(int l)	{
		return new CEntitySwitchCase(l, programCatalog) ;
	}
	public CEntityCase NewEntityCase(int l, int endline)	{
		return new CEntityCase(l, programCatalog, endline);
	}
	public CSubStringAttributReference NewEntitySubString(int l)	{
		return new CJavaSubStringReference(l, programCatalog, langOutput);
	}
	public CEntityArrayReference NewEntityArrayReference(int l)	{
		return new CJavaArrayReference(l, programCatalog, langOutput);
	}
	public CEntityGoto NewEntityGoto(int l, String Reference, CEntityProcedureSection section)	{
		CEntityGoto e = new CEntityGoto(l, programCatalog, Reference, section);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityGotoDepending NewEntityGotoDepending(int l, List<String> refs, CDataEntity dep, CEntityProcedureSection section)	{
		CEntityGotoDepending e =
			new CEntityGotoDepending(l, programCatalog, refs, dep, section);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityLoopWhile NewEntityLoopWhile(int l)	{
		return new CEntityLoopWhile(l, programCatalog);
	}
	public CEntityLoopIter NewEntityLoopIter(int l)	{
		return new CEntityLoopIter(l, programCatalog);
	}
	public CEntityAddTo NewEntityAddTo(int l)	{
		return new CEntityAddTo(l, programCatalog);
	}
	public CEntityContinue NewEntityContinue(int l)	{
		return new CEntityContinue(l, programCatalog);
	}
	public CEntityNextSentence NewEntityNextSentence(int l)	{
		CEntityNextSentence e = new CEntityNextSentence(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityNamedCondition NewEntityNamedCondition(int l, String name)	{
		return new CJavaNamedCondition(l, name, programCatalog, langOutput);
	}
	public CEntitySQLSingleStatement NewEntitySQLSingleStatement(int l, String st)	{
		// Direct backend CJavaSQLSingleStatement retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (recursiveSQLSingleStatementEntity
		// binding). The raw statement text is carried by the entity and the template
		// wraps it verbatim in the legacy getDBConnection().execSQL("...") call; no
		// WHENEVER clause is chained and no generated string is materialized in the
		// factory.
		CEntitySQLSingleStatement e = new CEntitySQLSingleStatement(l, programCatalog, st);
		e.setLanguageExporter(langOutput);
		return e;
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
		CEntityAssignWithAccessor e = new CEntityAssignWithAccessor(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
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
		CEntitySetConstant e = new CEntitySetConstant(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
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
		CEntitySubtractTo e = new CEntitySubtractTo(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityIsNamedCondition NewEntityIsNamedCondition()	{
		return new CJavaIsNamedCondition();
	}
	public CEntityDataSection NewEntityDataSection(int l, String name)	{
		return new CJavaDataSection(l, name, programCatalog, langOutput);
	}
	public CEntityReplace NewEntityReplace(int l)	{
		CEntityReplace e = new CEntityReplace(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityIsFieldHighlight NewEntityIsFieldHighlight(CDataEntity ref)	{
		programCatalog.addImportDeclaration("MAP") ;
		return new CJavaIsFieldHighlight(ref) ;
	}
	public CEntityFieldValidated NewEntityFieldValidated(int l, String name, CDataEntity field)	{
		return new CJavaFieldValidated(l, name, programCatalog, langOutput, field) ;
	}
	public CEntityStringConcat NewEntityStringConcat(int l)	{
		CEntityStringConcat e = new CEntityStringConcat(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityDivide NewEntityDivide(int l)	{
		CEntityDivide e = new CEntityDivide(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityMultiply NewEntityMultiply(int l)	{
		CEntityMultiply e = new CEntityMultiply(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityParseString NewEntityParseString(int l)	{
		CEntityParseString e = new CEntityParseString(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntitySQLRollBack NewEntitySQLRollBack(int l)	{
		// Direct backend CJavaSQLRollBack retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (recursiveSQLRollBackEntity
		// binding). The optional WHENEVER SQLWARNING/SQLERROR clause is a read-only
		// catalog lookup the template chains onto sqlRollback(); no generated string
		// is materialized in the factory.
		CEntitySQLRollBack e = new CEntitySQLRollBack(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntitySQLCommit NewEntitySQLCommit(int l)	{
		// Direct backend CJavaSQLCommit retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveSQLCommitEntity binding). The
		// optional WHENEVER SQLWARNING/SQLERROR clause is a read-only catalog lookup
		// the template chains onto sqlCommit(); no generated string is materialized
		// in the factory.
		CEntitySQLCommit e = new CEntitySQLCommit(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityExprOpposite NewEntityExprOpposite()	{
		return new CJavaExprOpposite();
	}
	public CEntityCICSXctl NewEntityCICSXctl(int l)	{
		// Direct backend CJavaCICSXctl retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveCICSXctlEntity binding).
		CEntityCICSXctl e = new CEntityCICSXctl(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCICSLink NewEntityCICSLink(int l)	{
		// Direct backend CJavaCICSLink retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveCICSLinkEntity binding).
		CEntityCICSLink e = new CEntityCICSLink(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCICSAddress NewEntityCICSAddress(int l) {
		// Direct backend CJavaCICSAddress retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveCICSAddressEntity binding).
		CEntityCICSAddress e = new CEntityCICSAddress(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCICSAskTime NewEntityCICSAskTime(int l)	{
		// Direct backend CJavaCICSAskTime retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveCICSAskTimeEntity binding).
		CEntityCICSAskTime e = new CEntityCICSAskTime(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCurrentDate NewEntityCurrentDate()	{
		return new CJavaCurrentDate(programCatalog, langOutput);
	}
	public CEntityIntrinsicFunction NewEntityIntrinsicFunction(String functionName, List<CBaseEntityExpression> arguments)	{
		return new CJavaIntrinsicFunction(programCatalog, langOutput, functionName, arguments);
	}
	public CEntityAddressOf NewEntityAddressOf(CDataEntity data)	{
		return new CJavaAddressOf(programCatalog, langOutput, data);
	}
	public CEntityLengthOf NewEntityLengthOf(CDataEntity data)	{
		return new CJavaLengthOf(programCatalog, langOutput, data);
	}
	public CEntityCICSHandleCondition NewEntityCICSHandleCondition(int l)	{
		// Direct backend CJavaCICSHandleCondition retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (recursiveCICSHandleConditionEntity
		// binding).
		CEntityCICSHandleCondition e = new CEntityCICSHandleCondition(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCICSHandleAID NewEntityCICSHandleAID(int l)	{
		// Direct backend CJavaCICSHandleAID retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (recursiveCICSHandleAIDEntity
		// binding).
		CEntityCICSHandleAID e = new CEntityCICSHandleAID(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCICSIgnoreCondition NewEntityCICSIgnoreCondition(int l)	{
		CEntityCICSIgnoreCondition e = new CEntityCICSIgnoreCondition(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCICSRetrieve NewEntityCICSRetreive(int l, boolean bPointer)	{
		CEntityCICSRetrieve e = new CEntityCICSRetrieve(l, programCatalog, bPointer);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCICSStart NewEntityCICSStart(int l, CDataEntity TID)	{
		CEntityCICSStart e = new CEntityCICSStart(l, programCatalog, TID);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCICSReturn NewEntityCICSReturn(int l)	{
		// Direct backend CJavaCICSReturn retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveCICSReturnEntity binding).
		CEntityCICSReturn e = new CEntityCICSReturn(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCICSSendMap NewEntityCICSSendMap(int l)	{
		// Direct backend CJavaCICSSendMap retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (recursiveCICSSendMapEntity binding).
		CEntityCICSSendMap e = new CEntityCICSSendMap(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCICSWrite NewEntityCICSWrite(int l)	{
		CEntityCICSWrite e = new CEntityCICSWrite(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCICSReceiveMap NewEntityCICSReceiveMap(int l, CDataEntity name)	{
		// Direct backend CJavaCICSReceiveMap retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (recursiveCICSReceiveMapEntity binding).
		CEntityCICSReceiveMap e = new CEntityCICSReceiveMap(l, programCatalog, name);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityIsFieldModified NewEntityIsFieldModified() {
		return new CJavaIsFieldModified();
	}
	public CEntityCICSSyncPoint NewEntityCICSSyncPoint(int l, boolean bRollBack)	{
		// Direct backend CJavaCICSSyncPoint retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveCICSSyncPointEntity binding).
		CEntityCICSSyncPoint e = new CEntityCICSSyncPoint(l, programCatalog, bRollBack);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCICSInquire NewEntityCICSInquire(int l)	{
		// Direct backend CJavaCICSInquire retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveCICSInquireEntity binding).
		CEntityCICSInquire e = new CEntityCICSInquire(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCICSAbend NewEntityCICSAbend(int l)	{
		// Direct backend CJavaCICSAbend retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveCICSAbendEntity binding).
		CEntityCICSAbend e = new CEntityCICSAbend(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCICSRead NewEntityCICSRead(int l, CEntityCICSRead.CEntityCICSReadMode mode)	{
		CEntityCICSRead e = new CEntityCICSRead(l, programCatalog, mode);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCICSStartBrowse NewEntityCICSStartBrowse(int l)	{
		CEntityCICSStartBrowse e = new CEntityCICSStartBrowse(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCICSDeleteQ NewEntityCICSDeleteQ(int l, boolean b)	{
		CEntityCICSDeleteQ e = new CEntityCICSDeleteQ(l, programCatalog, b);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCICSWriteQ NewEntityCICSWriteQ(int l, boolean b)	{
		CEntityCICSWriteQ e = new CEntityCICSWriteQ(l, programCatalog, b);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCICSReadQ NewEntityCICSReadQ(int l, boolean b)	{
		CEntityCICSReadQ e = new CEntityCICSReadQ(l, programCatalog, b);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCICSAssign NewEntityCICSAssign(int l)	{
		// Direct backend CJavaCICSAssign retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveCICSAssignEntity binding).
		CEntityCICSAssign e = new CEntityCICSAssign(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityDisplay NewEntityDisplay(int l, Upon t)	{
		CEntityDisplay e = new CEntityDisplay(l, programCatalog, t);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCount NewEntityCount(int l)	{
		CEntityCount e = new CEntityCount(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityInspectConverting NewEntityInspectConverting(int l) {
		CEntityInspectConverting e = new CEntityInspectConverting(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCICSReWrite NewEntityCICSReWrite(int l)	{
		CEntityCICSReWrite e = new CEntityCICSReWrite(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCICSDelay NewEntityCICSDelay(int l)	{
		// Direct backend CJavaCICSDelay retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveCICSDelayEntity binding).
		CEntityCICSDelay e = new CEntityCICSDelay(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCICSSetTDQueue NewEntityCICSSetTDQueue(int l)	{
		CEntityCICSSetTDQueue e = new CEntityCICSSetTDQueue(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCICSDeQ NewEntityCICSDeQ(int l)	{
		// Direct backend CJavaCICSDeQ retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveCICSDeQEntity binding).
		CEntityCICSDeQ e = new CEntityCICSDeQ(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCICSEnQ NewEntityCICSEnQ(int l)	{
		// Direct backend CJavaCICSEnQ retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveCICSEnQEntity binding).
		CEntityCICSEnQ e = new CEntityCICSEnQ(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityProcedureDivision NewEntityProcedureDivision(int l)	{
		return new CJavaProcedureDivision(l, programCatalog, langOutput);
	}
	public CEntitySQLCursorSection NewEntitySQLCursorSection()	{
		// Direct backend CJavaSQLCursorSection retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (recursiveSQLCursorSectionEntity
		// declaration binding).
		CEntitySQLCursorSection e = new CEntitySQLCursorSection(programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityFieldArrayReference NewEntityFieldArrayReference(int l)	{
		return new CJavaFieldArrayReference(l, programCatalog, langOutput);
	}
	public CEntityIndex NewEntityIndex(String name)	{
		return new CJavaIndex(name, programCatalog, langOutput);
	}
	public CEntitySQLCursor NewEntitySQLCursor(String name)	{
		// Direct backend CJavaSQLCursor retired: the pure semantic entity renders
		// through the recursive ST4 assembler (semantic.SQL.CEntitySQLCursor =
		// dataReferenceEntity reference binding) and keeps its legacy formatted
		// ExportReference for the direct path.
		CEntitySQLCursor e = new CEntitySQLCursor(name, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
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
		// Direct backend CJavaCICSGetMain retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (recursiveCICSGetMainEntity
		// binding).
		CEntityCICSGetMain e = new CEntityCICSGetMain(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityResetKeyPressed NewEntityResetKeyPressed(int l)	{
		return new CJavaResetKeyPressed(l, programCatalog, langOutput);
	}
	public CEntityResourceFieldArray NewEntityFieldArray()	{
		return new CJavaFieldArray(0, "", programCatalog, langOutput);
	}
	public CEntitySQLCode NewEntitySQLCode(String name)	{
		// Direct backend CJavaSQLCode retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveSQLCodeEntity binding) and carries
		// its own getSQLCode()/resetSQLCode(...) data-reference protocol.
		CEntitySQLCode e = new CEntitySQLCode(name, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntitySQLCode NewEntitySQLCode(String name, CBaseEntityExpression eHistoryItem)	{
		CEntitySQLCode e = new CEntitySQLCode(name, programCatalog, eHistoryItem);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCondIsSQLCode NewEntityCondIsSQLCode()	{
		// Direct backend CJavaCondIsSQLCode retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (recursiveCondIsSQLCodeEntity
		// binding); the addImportDeclaration("SQL") Stage-1 side effect (emits
		// 'import nacaLib.sqlSupport.* ;' for the SQLCode.* constants) is preserved.
		programCatalog.addImportDeclaration("SQL") ;
		return new CEntityCondIsSQLCode();
	}
	public CEntityRoutineEmulationCall NewEntityRoutineEmulationCall(int l)	{
		CEntityRoutineEmulationCall e = new CEntityRoutineEmulationCall(l, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
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
		CEntitySearch e = new CEntitySearch(line, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityInternalBool NewEntityInternalBool(String name)	{
		return new CJavaInternalBool(name, programCatalog, langOutput) ;
	}
	public CEntityBreak NewEntityBreak(int line)	{
		return new CEntityBreak(line, programCatalog) ;
	}
	public CEntityFileDescriptor NewEntityFileDescriptor(int line, String name) {
		return new CJavaFileDescriptor(line, name, programCatalog, langOutput) ;
	}
	public CEntitySortedFileDescriptor NewEntitySortedFileDescriptor(int line, String name)	{
		return new CJavaSortedFileDescriptor(line, name, programCatalog, langOutput) ;
	}
	public CEntityOpenFile NewEntityOpenFile(int line) 	{
		CEntityOpenFile e = new CEntityOpenFile(line, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityCloseFile NewEntityCloseFile(int line)	{
		CEntityCloseFile e = new CEntityCloseFile(line, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityReadFile NewEntityReadFile(int line)	{
		CEntityReadFile e = new CEntityReadFile(line, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityWriteFile NewEntityWriteFile(int line) {
		CEntityWriteFile e = new CEntityWriteFile(line, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityAccept NewEntityAccept(int line){
		CEntityAccept e = new CEntityAccept(line, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntitySort NewEntitySort(int line)	{
		CEntitySort e = new CEntitySort(line, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntitySortRelease NewEntitySortRelease(int line)	{
		CEntitySortRelease e = new CEntitySortRelease(line, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntitySortReturn NewEntitySortReturn(int line)	{
		CEntitySortReturn e = new CEntitySortReturn(line, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityRewriteFile NewEntityRewriteFile(int line)	{
		CEntityRewriteFile e = new CEntityRewriteFile(line, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
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
		// Direct backend CJavaSQLSessionDeclare retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (recursiveSQLSessionDeclareEntity
		// binding). The full "DECLARE GLOBAL ..." statement text is carried by the
		// entity (set by the parser via setSql) and the optional WHENEVER
		// SQLWARNING/SQLERROR clause is a read-only catalog lookup the template
		// chains onto the sql(...) runtime call; no generated string is materialized
		// in the factory.
		CEntitySQLSessionDeclare e = new CEntitySQLSessionDeclare(line, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntitySQLSessionDrop NewEntitySQLSessionDrop(int line)	{
		// Direct backend CJavaSQLSessionDrop retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (recursiveSQLSessionDropEntity
		// binding). The full "DROP ..." statement text is carried by the entity
		// (set by the parser via setSql) and the optional WHENEVER
		// SQLWARNING/SQLERROR clause is a read-only catalog lookup the template
		// chains onto the sql(...) runtime call; no generated string is materialized
		// in the factory.
		CEntitySQLSessionDrop e = new CEntitySQLSessionDrop(line, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntitySQLLock NewEntitySQLLock(int line)	{
		// Direct backend CJavaSQLLock retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveSQLLockEntity binding). The full
		// "LOCK TABLE <table> IN EXCLUSIVE MODE" text is assembled in Stage 1 and
		// the optional WHENEVER SQLWARNING/SQLERROR clause is a read-only catalog
		// lookup the template chains onto the sql(...) runtime call; no generated
		// string is materialized in the factory.
		CEntitySQLLock e = new CEntitySQLLock(line, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntitySQLExecute NewEntitySQLExecute(int line)	{
		// Direct backend CJavaSQLExecute retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveSQLExecuteEntity binding). The host
		// variable stays a semantic child rendered through its reference binding, and
		// the optional WHENEVER SQLWARNING/SQLERROR clause is a read-only catalog
		// lookup the template chains onto sql("EXECUTE IMMEDIATE #1").param(1, ...);
		// no generated string is materialized in the factory.
		CEntitySQLExecute e = new CEntitySQLExecute(line, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}
	public CEntityFormatedVarReference NewEntityFormatedVarReference(CDataEntity object, String format)	{
		throw new NacaTransAssertException("Method not implemented") ;
	}
	public CEntityInc NewEntityInc(int line)	{
		return new CEntityInc(line, programCatalog) ;
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
		// Direct backend CJavaSQLCall retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveSQLCallEntity binding). The called
		// program reference and each host-variable parameter remain semantic children
		// and are recursively rendered through their reference bindings; no generated
		// string is materialized in the factory.
		CEntitySQLCall e = new CEntitySQLCall(line, programCatalog);
		e.setLanguageExporter(langOutput);
		return e;
	}


}
