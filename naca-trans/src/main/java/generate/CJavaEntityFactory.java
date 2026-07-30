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
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaEntityFactory extends CBaseEntityFactory
{

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
		super(cat);
		langOutput = out;
	}

	protected CBaseLanguageExporter langOutput;

	@Override
	public String getOutputDirectory()
	{
		return langOutput == null ? "" : langOutput.getOutputDir();
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
		generate.LegacyLanguageRenderer.bind(e, langOutput);
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
		generate.LegacyLanguageRenderer.bind(e, langOutput);
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
		generate.LegacyLanguageRenderer.bind(e, langOutput);
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
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntitySQLCloseStatement NewEntitySQLCloseStatement(int nLine, CEntitySQLCursor cur)	{
		// Direct backend CJavaSQLCloseStatement retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler. The cursor remains a semantic child
		// and is recursively rendered through its reference binding; no generated
		// string is materialized in the factory.
		CEntitySQLCloseStatement e = new CEntitySQLCloseStatement(nLine, programCatalog, cur);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
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
		generate.LegacyLanguageRenderer.bind(e, langOutput);
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
		generate.LegacyLanguageRenderer.bind(e, langOutput);
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
		generate.LegacyLanguageRenderer.bind(e, langOutput);
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
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityClass NewEntityClass(int l, String name)	{
		CEntityClass e = new CEntityClass(l, name, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityComment NewEntityComment(int l, String comment)	{
		CEntityComment e = new CEntityComment(l, programCatalog, comment);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityAttribute NewEntityAttribute(int l, String name)	{
		CEntityAttribute e = new CEntityAttribute(l, name, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityStructure NewEntityStructure(int l, String name, String level)	{
		CEntityStructure entity =
			new CEntityStructure(l, name, programCatalog, level);
		generate.LegacyLanguageRenderer.bind(entity, langOutput);
		return entity;
	}
	public CEntityProcedure NewEntityProcedure(int l, String name, CEntityProcedureSection section)	{
		CEntityProcedure entity =
			new CEntityProcedure(l, name, programCatalog, section);
		generate.LegacyLanguageRenderer.bind(entity, langOutput);
		return entity;
	}
	public CEntityProcedureSection NewEntityProcedureSection(int l, String name)	{
		CEntityProcedureSection entity =
			new CEntityProcedureSection(l, name, programCatalog);
		generate.LegacyLanguageRenderer.bind(entity, langOutput);
		return entity;
	}
	public CEntityAssign NewEntityAssign(int l)	{
		CEntityAssign e = new CEntityAssign(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityExternalDataStructure NewEntityExternalDataStructure(int l, String name)	{
		CEntityExternalDataStructure e =
			new CEntityExternalDataStructure(l, name, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityInline NewEntityInline(int l, CBaseExternalEntity ext)	{
		CEntityInline entity = new CEntityInline(l, programCatalog, ext);
		generate.LegacyLanguageRenderer.bind(entity, langOutput);
		programCatalog.RegisterExternalDataStructure(ext);
		return entity;
	}
	public CEntityCondition NewEntityCondition(int l)	{
		return new CEntityCondition(l, programCatalog);
	}
	public CEntityBloc NewEntityBloc(int l)	{
		return new CEntityBloc(l, programCatalog);
	}
	public CEntityCalcul NewEntityCalcul(int l)	{
		CEntityCalcul e = new CEntityCalcul(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntitySqlOnErrorGoto NewEntitySQLOnErrorGoto(int l, String ref)	{
		// Direct backend CJavaSqlOnErrorGoto retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (recursiveSqlOnErrorGotoEntity
		// binding). The WHENEVER policy is a Stage-1 catalog side effect registered
		// here, in program order, during semantic analysis; the template emits no code.
		CEntitySqlOnErrorGoto e = new CEntitySqlOnErrorGoto(l, programCatalog, ref, false) ;
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		registerSqlWheneverPolicy(ref, false);
		return e;
	}
	public CEntitySqlOnErrorGoto NewEntitySQLOnWarningGoto(int l, String ref)	{
		// Direct backend CJavaSqlOnErrorGoto retired (see NewEntitySQLOnErrorGoto).
		CEntitySqlOnErrorGoto e = new CEntitySqlOnErrorGoto(l, programCatalog, ref, true) ;
		generate.LegacyLanguageRenderer.bind(e, langOutput);
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
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityResourceFormContainer NewEntityFormContainer(int l, String name, boolean bSave)	{
		return BmsJavaEntities.formContainer(l, name, programCatalog, langOutput, bSave);
	}
	public CEntityResourceForm NewEntityForm(int l, String name, boolean bSave)	{
		return BmsJavaEntities.form(l, name, programCatalog, langOutput, bSave);
	}
	public CEntityFieldAttribute NewEntityFieldAttribute(int l, String name, CDataEntity owner)	{
		return BmsJavaEntities.fieldAttribute(l, name, programCatalog, langOutput, owner);
	}
	public CEntityCallFunction NewEntityCallFunction(int l, String reference, String csRefThru, CEntityProcedureSection section)	{
		CEntityCallFunction e = new CEntityCallFunction(
			l, programCatalog, reference, csRefThru, section);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityInitialize NewEntityInitialize(int l, CDataEntity data)	{
		CEntityInitialize e = new CEntityInitialize(l, programCatalog, data);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityReturn NewEntityReturn(int l)	{
		CEntityReturn e = new CEntityReturn(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCallProgram NewEntityCallProgram(int l, CDataEntity reference)	{
		CEntityCallProgram e = new CEntityCallProgram(l, programCatalog, reference);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntitySwitchCase NewEntitySwitchCase(int l)	{
		return new CEntitySwitchCase(l, programCatalog) ;
	}
	public CEntityCase NewEntityCase(int l, int endline)	{
		return new CEntityCase(l, programCatalog, endline);
	}
	public CSubStringAttributReference NewEntitySubString(int l)	{
		CSubStringAttributReference entity =
			new CSubStringAttributReference(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(entity, langOutput);
		return entity;
	}
	public CEntityArrayReference NewEntityArrayReference(int l)	{
		CEntityArrayReference e = new CEntityArrayReference(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityGoto NewEntityGoto(int l, String Reference, CEntityProcedureSection section)	{
		CEntityGoto e = new CEntityGoto(l, programCatalog, Reference, section);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityGotoDepending NewEntityGotoDepending(int l, List<String> refs, CDataEntity dep, CEntityProcedureSection section)	{
		CEntityGotoDepending e =
			new CEntityGotoDepending(l, programCatalog, refs, dep, section);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
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
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityNamedCondition NewEntityNamedCondition(int l, String name)	{
		CEntityNamedCondition entity =
			new CEntityNamedCondition(l, name, programCatalog);
		generate.LegacyLanguageRenderer.bind(entity, langOutput);
		return entity;
	}
	public CEntitySQLSingleStatement NewEntitySQLSingleStatement(int l, String st)	{
		// Direct backend CJavaSQLSingleStatement retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (recursiveSQLSingleStatementEntity
		// binding). The raw statement text is carried by the entity and the template
		// wraps it verbatim in the legacy getDBConnection().execSQL("...") call; no
		// WHENEVER clause is chained and no generated string is materialized in the
		// factory.
		CEntitySQLSingleStatement e = new CEntitySQLSingleStatement(l, programCatalog, st);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntitySetColor NewEntitySetColor(int l, CDataEntity field)	{
		programCatalog.addImportDeclaration("MAP") ;
		return BmsJavaEntities.setColor(l, programCatalog, langOutput, field) ;
	}
	public CEntityFieldLength NewEntityFieldLengh(int l, String name, CDataEntity field)	{
		// Direct backend CJavaFieldLength retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (semantic.forms.CEntityFieldLength
		// -> valueReferenceEntity), reproducing the legacy ExportReference that
		// rendered the owner field reference. The legacy output controller stays
		// bound for the BMS traversal compatibility boundary.
		CEntityFieldLength e = new CEntityFieldLength(l, name, programCatalog, field) ;
		generate.LegacyLanguageRenderer.bind(e, langOutput) ;
		return e ;
	}
	public CEntityFieldColor NewEntityFieldColor(int l, String name, CDataEntity field)	{
		// Direct backend CJavaFieldColor retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (semantic.forms.CEntityFieldColor
		// -> valueReferenceEntity), reproducing the legacy ExportReference that
		// rendered the owner field reference. The legacy ExportWriteAccessorTo
		// (moveColor) had no live consumer in the recursive pipeline, so it retired
		// without a replacement. The legacy output controller stays bound for the
		// BMS traversal compatibility boundary.
		CEntityFieldColor e = new CEntityFieldColor(l, name, programCatalog, field) ;
		generate.LegacyLanguageRenderer.bind(e, langOutput) ;
		return e ;
	}
	public CEntityFieldHighlight NewEntityFieldHighlight(int l, String name, CDataEntity field)	{
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
		generate.LegacyLanguageRenderer.bind(e, langOutput) ;
		return e ;
	}
	public CEntityFieldFlag NewEntityFieldFlag(int l, String name, CDataEntity field)	{
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
		generate.LegacyLanguageRenderer.bind(e, langOutput) ;
		return e ;
	}
	public CEntitySetHighligh NewEntitySetHighlight(int l, CDataEntity field)	{
		// Direct backend CJavaSetHighlight retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (semantic.forms.CEntitySetHighligh
		// -> recursiveSetHighlightEntity), reproducing the legacy DoExport's
		// independent flag branches in order — blink -> setFieldBlink(<field>),
		// reverse -> setFieldReverse(<field>), underline -> setFieldUnderline(<field>),
		// normal -> setFieldUnhighlighted(<field>), a moved value ->
		// moveHighLighting(<value>, <field>) — and mapping the legacy reset branch's
		// resetFieldHighlighting(<field>) (which has no naca-rt signature and never
		// compiled) to the real OnlineProgram.setFieldUnhighlighted (highlighting
		// OFF). CJavaEntityFactoryST inherits this wiring; the legacy output
		// controller stays bound for the BMS traversal compatibility boundary.
		programCatalog.addImportDeclaration("MAP") ;
		CEntitySetHighligh e = new CEntitySetHighligh(l, programCatalog, field) ;
		generate.LegacyLanguageRenderer.bind(e, langOutput) ;
		return e ;
	}
	public CEntitySetFlag NewEntitySetFlag(int l, CDataEntity field)	{
		// Direct backend CJavaSetFlag retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (semantic.forms.CEntitySetFlag
		// -> recursiveSetFlagEntity), reproducing the legacy DoExport exactly —
		// a non-null flag value emits moveFlag("<value>", <field>) with the
		// constant quoted verbatim, otherwise resetFlag(<field>) (the protected
		// OnlineProgram flag calls contracted as bms.flag.move / bms.flag.reset).
		// CJavaEntityFactoryST inherits this wiring; the legacy output controller
		// stays bound for the BMS traversal compatibility boundary.
		CEntitySetFlag e = new CEntitySetFlag(l, programCatalog, field) ;
		generate.LegacyLanguageRenderer.bind(e, langOutput) ;
		return e ;
	}
	public CEntitySetCursor NewEntitySetCursor(int l, CDataEntity field)	{
		// Direct backend CJavaSetCursor retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (semantic.forms.CEntitySetCursor
		// -> recursiveSetCursorEntity), reproducing the legacy DoExport exactly —
		// a set reference value emits moveCursor(<value>, <field>), the remove
		// flag emits removeCursor(<field>), otherwise setCursor(<field>) (the
		// OnlineProgram cursor calls contracted as bms.cursor.set/remove/move).
		// CJavaEntityFactoryST inherits this wiring; the legacy output controller
		// stays bound for the BMS traversal compatibility boundary.
		CEntitySetCursor e = new CEntitySetCursor(l, programCatalog, field) ;
		generate.LegacyLanguageRenderer.bind(e, langOutput) ;
		return e ;
	}
	public CEntitySetAttribute NewEntitySetAttribute(int l, CDataEntity field)	{
		// Direct backend CJavaSetAttribute retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (semantic.forms.CEntitySetAttribute
		// -> recursiveSetAttributeEntity), reproducing the legacy DoExport exactly —
		// a set attribute value emits the single moveAttribute(<value>, <field>) and
		// stops, otherwise up to three moveAttribute(<constant>, <field>) calls are
		// emitted, one per attribute group in the legacy else-if precedence
		// (protection / intensity / modified; the protected OnlineProgram moveAttribute
		// overloads contracted as bms.attribute.protection / .intensity / .modified /
		// .move.var). CJavaEntityFactoryST inherits this wiring; the legacy output
		// controller stays bound for the BMS traversal compatibility boundary.
		programCatalog.addImportDeclaration("MAP") ;
		CEntitySetAttribute e = new CEntitySetAttribute(l, programCatalog, field) ;
		generate.LegacyLanguageRenderer.bind(e, langOutput) ;
		return e ;
	}
	public CEntityAssignWithAccessor NewEntityAssignWithAccessor(int l)	{
		CEntityAssignWithAccessor e = new CEntityAssignWithAccessor(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityFieldData NewEntityFieldData(int l, String name, CDataEntity field)	{
		return BmsJavaEntities.fieldData(l, name, programCatalog, langOutput, field);
	}
	public CResourceStrings NewResourceString(int nbLines, int nbCols)	{
		return BmsJavaEntities.resourceStrings(nbLines, nbCols);
	}
	public CEntityEnvironmentVariable NewEntityEnvironmentVariable(String name, String acc, boolean bNumeric)	{
		CEntityEnvironmentVariable e =
			new CEntityEnvironmentVariable(0, name, programCatalog, acc, "", bNumeric);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityEnvironmentVariable NewEntityEnvironmentVariable(String name, String acc, String write, boolean bNumeric)	{
		CEntityEnvironmentVariable e =
			new CEntityEnvironmentVariable(0, name, programCatalog, acc, write, bNumeric);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntitySkipFields NewEntityWorkingSkipField(int l, String name, int nbFields, String level)	{
		return BmsJavaEntities.skipFields(l, name, programCatalog, langOutput, nbFields, level);
	}
	public CEntityResourceField NewEntityEntryField(int l, String name)	{
		return BmsJavaEntities.entryField(l, name, programCatalog, langOutput);
	}
	public CEntityResourceField NewEntityLabelField(int l)	{
		return BmsJavaEntities.labelField(l, programCatalog, langOutput);
	}
	public CEntityFieldRedefine NewEntityFieldRedefine(int l, String name, String level)	{
		return BmsJavaEntities.fieldRedefine(l, name, programCatalog, langOutput, level);
	}
	public CEntityFormRedefine NewEntityFormRedefine(int l, String name, CDataEntity eForm, boolean bSaveMap)	{
		//programCatalog.addImportDeclaration("MAP") ;
		return BmsJavaEntities.formRedefine(l, name, programCatalog, langOutput, eForm, bSaveMap);
	}
	public CEntityString NewEntityString(char[] value)	{
		CEntityString e = new CEntityString(programCatalog, value) ;
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e ;
	}
	public CEntityCondOr NewEntityCondOr()	{
		return new CEntityCondOr();
	}
	public CEntityNumber NewEntityNumber(String value)	{
		CEntityNumber e = new CEntityNumber(programCatalog, value);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityExprTerminal NewEntityExprTerminal(CDataEntity eData)	{
		return new CEntityExprTerminal(eData);
	}
	public CEntityExprSum NewEntityExprSum()	{
		return new CEntityExprSum();
	}
	public CEntityExprProd NewEntityExprProd()	{
		return new CEntityExprProd();
	}
	public CEntityCondNot NewEntityCondNot()	{
		return new CEntityCondNot();
	}
	public CEntityCondEquals NewEntityCondEquals()	{
		return new CEntityCondEquals();
	}
	public CEntityCondCompare NewEntityCondCompare()	{
		return new CEntityCondCompare();
	}
	public CEntityCondAnd NewEntityCondAnd()	{
		return new CEntityCondAnd();
	}
	public CEntityCondIsAll NewEntityCondIsAll()	{
		return new CEntityCondIsAll();
	}
	public CEntityCondIsKindOf NewEntityCondIsKindOf()	{
		return new CEntityCondIsKindOf();
	}
	public CEntityCondIsConstant NewEntityCondIsConstant()	{
		return new CEntityCondIsConstant() ;
	}
	public CEntityIsFieldFlag NewEntityIsFieldFlag()	{
		return BmsJavaEntities.isFieldFlag();
	}
	public CEntitySetConstant NewEntitySetConstant(int l)	{
		CEntitySetConstant e = new CEntitySetConstant(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityIsFieldColor NewEntityIsFieldColor()	{
		programCatalog.addImportDeclaration("MAP") ;
		return BmsJavaEntities.isFieldColor();
	}
	public CEntityIsFieldAttribute NewEntityIsFieldAttribute()	{
		programCatalog.addImportDeclaration("MAP") ;
		return BmsJavaEntities.isFieldAttribute() ;
	}
	public CEntityAddressReference NewEntityAddressReference(CDataEntity ref)	{
		CEntityAddressReference e = new CEntityAddressReference(programCatalog, ref);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityMoveReference NewEntityMoveReference(int l)	{
		CEntityMoveReference entity = new CEntityMoveReference(l, programCatalog) ;
		generate.LegacyLanguageRenderer.bind(entity, langOutput);
		return entity ;
	}
	public CEntitySubtractTo NewEntitySubtractTo(int l)	{
		CEntitySubtractTo e = new CEntitySubtractTo(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityIsNamedCondition NewEntityIsNamedCondition()	{
		return new CEntityIsNamedCondition();
	}
	public CEntityDataSection NewEntityDataSection(int l, String name)	{
		CEntityDataSection e = new CEntityDataSection(l, name, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityReplace NewEntityReplace(int l)	{
		CEntityReplace e = new CEntityReplace(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityIsFieldHighlight NewEntityIsFieldHighlight(CDataEntity ref)	{
		programCatalog.addImportDeclaration("MAP") ;
		return BmsJavaEntities.isFieldHighlight(ref) ;
	}
	public CEntityFieldValidated NewEntityFieldValidated(int l, String name, CDataEntity field)	{
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
		generate.LegacyLanguageRenderer.bind(e, langOutput) ;
		return e ;
	}
	public CEntityStringConcat NewEntityStringConcat(int l)	{
		CEntityStringConcat e = new CEntityStringConcat(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityDivide NewEntityDivide(int l)	{
		CEntityDivide e = new CEntityDivide(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityMultiply NewEntityMultiply(int l)	{
		CEntityMultiply e = new CEntityMultiply(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityParseString NewEntityParseString(int l)	{
		CEntityParseString e = new CEntityParseString(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntitySQLRollBack NewEntitySQLRollBack(int l)	{
		// Direct backend CJavaSQLRollBack retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (recursiveSQLRollBackEntity
		// binding). The optional WHENEVER SQLWARNING/SQLERROR clause is a read-only
		// catalog lookup the template chains onto sqlRollback(); no generated string
		// is materialized in the factory.
		CEntitySQLRollBack e = new CEntitySQLRollBack(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntitySQLCommit NewEntitySQLCommit(int l)	{
		// Direct backend CJavaSQLCommit retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveSQLCommitEntity binding). The
		// optional WHENEVER SQLWARNING/SQLERROR clause is a read-only catalog lookup
		// the template chains onto sqlCommit(); no generated string is materialized
		// in the factory.
		CEntitySQLCommit e = new CEntitySQLCommit(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityExprOpposite NewEntityExprOpposite()	{
		return new CEntityExprOpposite();
	}
	public CEntityCICSXctl NewEntityCICSXctl(int l)	{
		// Direct backend CJavaCICSXctl retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveCICSXctlEntity binding).
		CEntityCICSXctl e = new CEntityCICSXctl(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCICSLink NewEntityCICSLink(int l)	{
		// Direct backend CJavaCICSLink retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveCICSLinkEntity binding).
		CEntityCICSLink e = new CEntityCICSLink(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCICSAddress NewEntityCICSAddress(int l) {
		// Direct backend CJavaCICSAddress retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveCICSAddressEntity binding).
		CEntityCICSAddress e = new CEntityCICSAddress(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCICSAskTime NewEntityCICSAskTime(int l)	{
		// Direct backend CJavaCICSAskTime retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveCICSAskTimeEntity binding).
		CEntityCICSAskTime e = new CEntityCICSAskTime(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCurrentDate NewEntityCurrentDate()	{
		CEntityCurrentDate e = new CEntityCurrentDate(programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityIntrinsicFunction NewEntityIntrinsicFunction(String functionName, List<CBaseEntityExpression> arguments)	{
		CEntityIntrinsicFunction e =
			new CEntityIntrinsicFunction(programCatalog, functionName, arguments);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityAddressOf NewEntityAddressOf(CDataEntity data)	{
		CEntityAddressOf e = new CEntityAddressOf(programCatalog, data);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityLengthOf NewEntityLengthOf(CDataEntity data)	{
		CEntityLengthOf e = new CEntityLengthOf(programCatalog, data);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCICSHandleCondition NewEntityCICSHandleCondition(int l)	{
		// Direct backend CJavaCICSHandleCondition retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (recursiveCICSHandleConditionEntity
		// binding).
		CEntityCICSHandleCondition e = new CEntityCICSHandleCondition(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCICSHandleAID NewEntityCICSHandleAID(int l)	{
		// Direct backend CJavaCICSHandleAID retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (recursiveCICSHandleAIDEntity
		// binding).
		CEntityCICSHandleAID e = new CEntityCICSHandleAID(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCICSIgnoreCondition NewEntityCICSIgnoreCondition(int l)	{
		CEntityCICSIgnoreCondition e = new CEntityCICSIgnoreCondition(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCICSRetrieve NewEntityCICSRetreive(int l, boolean bPointer)	{
		CEntityCICSRetrieve e = new CEntityCICSRetrieve(l, programCatalog, bPointer);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCICSStart NewEntityCICSStart(int l, CDataEntity TID)	{
		CEntityCICSStart e = new CEntityCICSStart(l, programCatalog, TID);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCICSReturn NewEntityCICSReturn(int l)	{
		// Direct backend CJavaCICSReturn retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveCICSReturnEntity binding).
		CEntityCICSReturn e = new CEntityCICSReturn(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCICSSendMap NewEntityCICSSendMap(int l)	{
		// Direct backend CJavaCICSSendMap retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (recursiveCICSSendMapEntity binding).
		CEntityCICSSendMap e = new CEntityCICSSendMap(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCICSWrite NewEntityCICSWrite(int l)	{
		CEntityCICSWrite e = new CEntityCICSWrite(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCICSReceiveMap NewEntityCICSReceiveMap(int l, CDataEntity name)	{
		// Direct backend CJavaCICSReceiveMap retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (recursiveCICSReceiveMapEntity binding).
		CEntityCICSReceiveMap e = new CEntityCICSReceiveMap(l, programCatalog, name);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityIsFieldModified NewEntityIsFieldModified() {
		return BmsJavaEntities.isFieldModified();
	}
	public CEntityCICSSyncPoint NewEntityCICSSyncPoint(int l, boolean bRollBack)	{
		// Direct backend CJavaCICSSyncPoint retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveCICSSyncPointEntity binding).
		CEntityCICSSyncPoint e = new CEntityCICSSyncPoint(l, programCatalog, bRollBack);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCICSInquire NewEntityCICSInquire(int l)	{
		// Direct backend CJavaCICSInquire retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveCICSInquireEntity binding).
		CEntityCICSInquire e = new CEntityCICSInquire(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCICSAbend NewEntityCICSAbend(int l)	{
		// Direct backend CJavaCICSAbend retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveCICSAbendEntity binding).
		CEntityCICSAbend e = new CEntityCICSAbend(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCICSRead NewEntityCICSRead(int l, CEntityCICSRead.CEntityCICSReadMode mode)	{
		CEntityCICSRead e = new CEntityCICSRead(l, programCatalog, mode);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCICSStartBrowse NewEntityCICSStartBrowse(int l)	{
		CEntityCICSStartBrowse e = new CEntityCICSStartBrowse(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCICSDeleteQ NewEntityCICSDeleteQ(int l, boolean b)	{
		CEntityCICSDeleteQ e = new CEntityCICSDeleteQ(l, programCatalog, b);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCICSWriteQ NewEntityCICSWriteQ(int l, boolean b)	{
		CEntityCICSWriteQ e = new CEntityCICSWriteQ(l, programCatalog, b);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCICSReadQ NewEntityCICSReadQ(int l, boolean b)	{
		CEntityCICSReadQ e = new CEntityCICSReadQ(l, programCatalog, b);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCICSAssign NewEntityCICSAssign(int l)	{
		// Direct backend CJavaCICSAssign retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveCICSAssignEntity binding).
		CEntityCICSAssign e = new CEntityCICSAssign(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityDisplay NewEntityDisplay(int l, Upon t)	{
		CEntityDisplay e = new CEntityDisplay(l, programCatalog, t);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCount NewEntityCount(int l)	{
		CEntityCount e = new CEntityCount(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityInspectConverting NewEntityInspectConverting(int l) {
		CEntityInspectConverting e = new CEntityInspectConverting(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCICSReWrite NewEntityCICSReWrite(int l)	{
		CEntityCICSReWrite e = new CEntityCICSReWrite(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCICSDelay NewEntityCICSDelay(int l)	{
		// Direct backend CJavaCICSDelay retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveCICSDelayEntity binding).
		CEntityCICSDelay e = new CEntityCICSDelay(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCICSSetTDQueue NewEntityCICSSetTDQueue(int l)	{
		CEntityCICSSetTDQueue e = new CEntityCICSSetTDQueue(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCICSDeQ NewEntityCICSDeQ(int l)	{
		// Direct backend CJavaCICSDeQ retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveCICSDeQEntity binding).
		CEntityCICSDeQ e = new CEntityCICSDeQ(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCICSEnQ NewEntityCICSEnQ(int l)	{
		// Direct backend CJavaCICSEnQ retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveCICSEnQEntity binding).
		CEntityCICSEnQ e = new CEntityCICSEnQ(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityProcedureDivision NewEntityProcedureDivision(int l)	{
		CEntityProcedureDivision entity =
			new CEntityProcedureDivision(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(entity, langOutput);
		return entity;
	}
	public CEntitySQLCursorSection NewEntitySQLCursorSection()	{
		// Direct backend CJavaSQLCursorSection retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (recursiveSQLCursorSectionEntity
		// declaration binding).
		CEntitySQLCursorSection e = new CEntitySQLCursorSection(programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityFieldArrayReference NewEntityFieldArrayReference(int l)	{
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
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityIndex NewEntityIndex(String name)	{
		CEntityIndex entity = new CEntityIndex(name, programCatalog);
		generate.LegacyLanguageRenderer.bind(entity, langOutput);
		return entity;
	}
	public CEntitySQLCursor NewEntitySQLCursor(String name)	{
		// Direct backend CJavaSQLCursor retired: the pure semantic entity renders
		// through the recursive ST4 assembler (semantic.SQL.CEntitySQLCursor =
		// dataReferenceEntity reference binding) and keeps its legacy formatted
		// ExportReference for the direct path.
		CEntitySQLCursor e = new CEntitySQLCursor(name, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityKeyPressed NewEntityKeyPressed(String name, String caption)	{
		//programCatalog.UseMapSupport() ;
		return BmsJavaEntities.keyPressed(0, name, programCatalog, langOutput, caption);
	}
	public CEntityGetKeyPressed NewEntityGetKeyPressed(String name)	{
		return BmsJavaEntities.getKeyPressed(name, programCatalog, langOutput);
	}
	public CEntityIsKeyPressed NewEntityIsKeyPressed()	{
		programCatalog.addImportDeclaration("KEYPRESSED") ;
		return BmsJavaEntities.isKeyPressed();
	}
	public CEntityFieldOccurs NewEntityFieldOccurs(int l, String name)	{
		return BmsJavaEntities.fieldOccurs(l, name, programCatalog, langOutput);
	}
	public CEntityUnknownReference NewEntityUnknownReference(int nLine, String csName)
	{
		CEntityUnknownReference entity =
			new CEntityUnknownReference(nLine, csName, programCatalog);
		generate.LegacyLanguageRenderer.bind(entity, langOutput);
		return entity;
	}
	public CEntityCICSGetMain NewEntityCICSGetMain(int l)	{
		// Direct backend CJavaCICSGetMain retired: the pure semantic entity is
		// rendered by the recursive ST4 assembler (recursiveCICSGetMainEntity
		// binding).
		CEntityCICSGetMain e = new CEntityCICSGetMain(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityResetKeyPressed NewEntityResetKeyPressed(int l)	{
		return BmsJavaEntities.resetKeyPressed(l, programCatalog, langOutput);
	}
	public CEntityResourceFieldArray NewEntityFieldArray()	{
		return BmsJavaEntities.fieldArray(0, "", programCatalog, langOutput);
	}
	public CEntitySQLCode NewEntitySQLCode(String name)	{
		// Direct backend CJavaSQLCode retired: the pure semantic entity is rendered
		// by the recursive ST4 assembler (recursiveSQLCodeEntity binding) and carries
		// its own getSQLCode()/resetSQLCode(...) data-reference protocol.
		CEntitySQLCode e = new CEntitySQLCode(name, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntitySQLCode NewEntitySQLCode(String name, CBaseEntityExpression eHistoryItem)	{
		CEntitySQLCode e = new CEntitySQLCode(name, programCatalog, eHistoryItem);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
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
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}

	protected Hashtable<String, CDataEntity> tabConstantValues = new Hashtable<String, CDataEntity>() ; 
	public void addSpecialConstantValue(String value, String constant)
	{
		tabConstantValues.put(value, new CEntityConstantValue(constant));
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
					return new CEntityConstantValue(code) ;
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
		CEntityConcat e = new CEntityConcat(programCatalog, e1, e2);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityIsFieldCursor NewEntityIsFieldCursor()	{
		return BmsJavaEntities.isFieldCursor() ;
	}
	public CEntityList NewEntityList(String name)	{
		CEntityList e = new CEntityList(name, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityDigits NewEntityDigits(CDataEntity nel)	{
		CEntityDigits e = new CEntityDigits(programCatalog, nel);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntitySearch NewEntitySearch(int line)	{
		CEntitySearch e = new CEntitySearch(line, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityInternalBool NewEntityInternalBool(String name)	{
		CEntityInternalBool e = new CEntityInternalBool(name, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityBreak NewEntityBreak(int line)	{
		return new CEntityBreak(line, programCatalog) ;
	}
	public CEntityFileDescriptor NewEntityFileDescriptor(int line, String name) {
		CEntityFileDescriptor e =
			new CEntityFileDescriptor(line, name, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntitySortedFileDescriptor NewEntitySortedFileDescriptor(int line, String name)	{
		CEntitySortedFileDescriptor entity =
			new CEntitySortedFileDescriptor(line, name, programCatalog);
		generate.LegacyLanguageRenderer.bind(entity, langOutput);
		return entity;
	}
	public CEntityOpenFile NewEntityOpenFile(int line) 	{
		CEntityOpenFile e = new CEntityOpenFile(line, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityCloseFile NewEntityCloseFile(int line)	{
		CEntityCloseFile e = new CEntityCloseFile(line, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityReadFile NewEntityReadFile(int line)	{
		CEntityReadFile e = new CEntityReadFile(line, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityWriteFile NewEntityWriteFile(int line) {
		CEntityWriteFile e = new CEntityWriteFile(line, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityAccept NewEntityAccept(int line){
		CEntityAccept e = new CEntityAccept(line, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntitySort NewEntitySort(int line)	{
		CEntitySort e = new CEntitySort(line, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntitySortRelease NewEntitySortRelease(int line)	{
		CEntitySortRelease e = new CEntitySortRelease(line, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntitySortReturn NewEntitySortReturn(int line)	{
		CEntitySortReturn e = new CEntitySortReturn(line, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}
	public CEntityRewriteFile NewEntityRewriteFile(int line)	{
		CEntityRewriteFile e = new CEntityRewriteFile(line, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
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
		generate.LegacyLanguageRenderer.bind(e, langOutput);
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
		generate.LegacyLanguageRenderer.bind(e, langOutput);
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
		generate.LegacyLanguageRenderer.bind(e, langOutput);
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
		generate.LegacyLanguageRenderer.bind(e, langOutput);
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
		return new CEntityConstant(val) ;
	}
	public CEntityFileDescriptorLengthDependency NewEntityFileDescriptorLengthDependency(String name)	{
		CEntityFileDescriptorLengthDependency entity =
			new CEntityFileDescriptorLengthDependency(name, programCatalog) ;
		generate.LegacyLanguageRenderer.bind(entity, langOutput);
		return entity ;
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
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}


}
