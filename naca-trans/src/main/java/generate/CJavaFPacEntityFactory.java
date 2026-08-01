/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate;

import generate.fpacjava.*;
import generate.fpacjava.CFPacJavaRoutineEmulationCall;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import semantic.*;
import semantic.CICS.*;
import semantic.CICS.CEntityCICSRead.CEntityCICSReadMode;
import semantic.SQL.*;
import semantic.Verbs.*;
import semantic.Verbs.CEntityDisplay.Upon;
import semantic.expression.*;
import semantic.expression.CEntityConstant.Value;
import semantic.forms.*;
import utils.CObjectCatalog;
import utils.NacaTransAssertException;

public class CJavaFPacEntityFactory extends CBaseEntityFactory
{

	public CJavaFPacEntityFactory(CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(cat);
		langOutput = out;
	}

	private final CBaseLanguageExporter langOutput;

	@Override
	public String getOutputDirectory()
	{
		return langOutput == null ? "" : langOutput.getOutputDir();
	}

	@Override
	public CEntityIsFieldCursor NewEntityIsFieldCursor()
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityResetKeyPressed NewEntityResetKeyPressed(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSGetMain NewEntityCICSGetMain(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityUnknownReference NewEntityUnknownReference(int nLine, String csName)
	{
		return new CFPacJavaUnknownReference(nLine, csName, programCatalog, langOutput);
	}

	@Override
	public CEntityFieldOccurs NewEntityFieldOccurs(int i, String string)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityGetKeyPressed NewEntityGetKeyPressed(String name)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityIsKeyPressed NewEntityIsKeyPressed()
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityKeyPressed NewEntityKeyPressed(String name, String caption)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySQLCursor NewEntitySQLCursor(String name)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityIndex NewEntityIndex(String name)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityFieldArrayReference NewEntityFieldArrayReference(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySQLCursorSection NewEntitySQLCursorSection()
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityProcedureDivision NewEntityProcedureDivision(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSReWrite NewEntityCICSReWrite(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSDelay NewEntityCICSDelay(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSSetTDQueue NewEntityCICSSetTDQueue(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSDeQ NewEntityCICSDeQ(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSEnQ NewEntityCICSEnQ(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCount NewEntityCount(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityInspectConverting NewEntityInspectConverting(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityDisplay NewEntityDisplay(int l, Upon t)
	{
		return new CFPacJavaDisplay(l, programCatalog, langOutput, t)  ;
	}

	@Override
	public CEntityCICSAssign NewEntityCICSAssign(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSWriteQ NewEntityCICSWriteQ(int l, boolean b)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSReadQ NewEntityCICSReadQ(int l, boolean b)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSDeleteQ NewEntityCICSDeleteQ(int l, boolean b)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSStartBrowse NewEntityCICSStartBrowse(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSRead NewEntityCICSRead(int l, CEntityCICSReadMode mode)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSAbend NewEntityCICSAbend(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSInquire NewEntityCICSInquire(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSSyncPoint NewEntityCICSSyncPoint(int l, boolean bRollBack)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityIsFieldModified NewEntityIsFieldModified()
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSReceiveMap NewEntityCICSReceiveMap(int l, CDataEntity name)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSWrite NewEntityCICSWrite(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSSendMap NewEntityCICSSendMap(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSReturn NewEntityCICSReturn(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSStart NewEntityCICSStart(int l, CDataEntity TID)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSRetrieve NewEntityCICSRetreive(int l, boolean bPointer)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSIgnoreCondition NewEntityCICSIgnoreCondition(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSHandleCondition NewEntityCICSHandleCondition(int line)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSHandleAID NewEntityCICSHandleAID(int line)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCurrentDate NewEntityCurrentDate()
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityIntrinsicFunction NewEntityIntrinsicFunction(String functionName, List<CBaseEntityExpression> arguments)
	{
		return new CFPacJavaIntrinsicFunction(
			programCatalog, langOutput, functionName, arguments);
	}

	@Override
	public CEntityAddressOf NewEntityAddressOf(CDataEntity data)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityLengthOf NewEntityLengthOf(CDataEntity data)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSXctl NewEntityCICSXctl(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSLink NewEntityCICSLink(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSAddress NewEntityCICSAddress(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCICSAskTime NewEntityCICSAskTime(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityExprOpposite NewEntityExprOpposite()
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySQLCommit NewEntitySQLCommit(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySQLRollBack NewEntitySQLRollBack(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityParseString NewEntityParseString(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityMultiply NewEntityMultiply(int l)
	{
		return new CFPacJavaMultiply(l, programCatalog, langOutput) ;
	}

	@Override
	public CEntityDivide NewEntityDivide(int l)
	{
		return new CFPacJavaDivide(l, programCatalog, langOutput) ;
	}

	@Override
	public CEntityStringConcat NewEntityStringConcat(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityIsFieldHighlight NewEntityIsFieldHighlight(CDataEntity ref)
	{
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityReplace NewEntityReplace(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityDataSection NewEntityDataSection(int l, String name)
	{
		return new CFPacJavaDataSection(l, name, programCatalog, langOutput);
	}

	@Override
	public CEntityIsNamedCondition NewEntityIsNamedCondition()
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySubtractTo NewEntitySubtractTo(int l)
	{
		return new CFPacJavaSubtractTo(l, programCatalog, langOutput);
	}

	@Override
	public CEntityMoveReference NewEntityMoveReference(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityAddressReference NewEntityAddressReference(CDataEntity ref)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityIsFieldAttribute NewEntityIsFieldAttribute()
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityIsFieldColor NewEntityIsFieldColor()
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySetConstant NewEntitySetConstant(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityIsFieldFlag NewEntityIsFieldFlag()
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCondIsConstant NewEntityCondIsConstant()
	{
		return new CFPacJavaCondIsConstant() ;
	}

	@Override
	public CEntityCondIsKindOf NewEntityCondIsKindOf()
	{
		return new CFPacJavaCondIsKindOf() ;
	}

	@Override
	public CEntityCondIsAll NewEntityCondIsAll()
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityString NewEntityString(char[] value)
	{
		return new CFPacJavaString(programCatalog, langOutput, value) ;
	}

	@Override
	public CEntityCondOr NewEntityCondOr()
	{
		return new CFPacJavaCondOr() ;
	}

	@Override
	public CEntityNumber NewEntityNumber(String value)
	{
		return new CFPacJavaNumber(programCatalog, langOutput, value) ;
	}

	@Override
	public CEntityExprTerminal NewEntityExprTerminal(CDataEntity eData)
	{
		return new CFPacJavaExprTerminal(eData) ;
	}

	@Override
	public CEntityExprSum NewEntityExprSum()
	{
		// FPac shares the target-neutral semantic expression model: a sum is a pure
		// CEntityExprSum whose operands are precomputed by the parser. Rendering goes
		// through the recursive ST4 assembler (recursiveExprSumEntity) via the
		// LegacyDataRenderer bridge; the retired CFPacExprSum backend only carried a
		// dead Export() override that no production path ever invoked.
		return new CEntityExprSum() ;
	}

	@Override
	public CEntityExprProd NewEntityExprProd()
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCondNot NewEntityCondNot()
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCondEquals NewEntityCondEquals()
	{
		// Pure target-neutral semantic entity shared with the COBOL pipeline. It renders
		// through the SHARED recursive ST4 binding (semantic.expression.CEntityCondEquals ->
		// recursiveCondEqualsEntity), emitting "isEqual(left, right)" / "isDifferent(left,
		// right)" (with the same "[UNDEFINED]" fallback for a missing right operand) —
		// exactly the shape the retired generate.fpacjava.CFPacJavaCondEquals.Export()
		// produced. The runtime comparison predicates (isEqual/isDifferent) live on
		// nacaLib.basePrgEnv.BaseProgram, which nacaLib.fpacPrgEnv.FPacProgram extends, so
		// the emitted calls are runtime-legal for FPac. Lowering stays in stage 1: the
		// parser sets the operands and the equal/different flag via
		// SetEqualCondition/SetDifferentCondition (parser/FPac/CFPacGenericExpression),
		// and the entity's getLeft/getRight/isDifferent getters consumed by the template
		// are pure state reads. No FPac override is needed in
		// semantic-fpac-bindings.properties because FPac's equality lowering is identical
		// in shape to COBOL's.
		return new CEntityCondEquals() ;
	}

	@Override
	public CEntityCondCompare NewEntityCondCompare()
	{
		// Pure target-neutral semantic entity shared with the COBOL pipeline. It renders
		// through the SHARED recursive ST4 binding (semantic.expression.CEntityCondCompare ->
		// recursiveCondCompareEntity), emitting "isGreater[OrEqual](left, right)" /
		// "isLess[OrEqual](left, right)" — exactly the shape the retired
		// generate.fpacjava.CFPacJavaCondCompare.Export() produced. The runtime comparison
		// methods (isGreater/isLess/isGreaterOrEqual/isLessOrEqual) live on
		// nacaLib.basePrgEnv.BaseProgram, which nacaLib.fpacPrgEnv.FPacProgram extends, so
		// the emitted calls are runtime-legal for FPac. Lowering stays in stage 1: the
		// operands (op1/op2) and the isisGreater/isisOrEquals flags are set by the parser
		// via the SetGreaterThan/SetLessThan/... methods, and the entity's
		// getLeft/getRight/isGreater/isOrEqual getters consumed by the template are pure
		// state reads. No FPac override is needed in semantic-fpac-bindings.properties
		// because FPac's ordered-comparison lowering is identical in shape to COBOL's.
		return new CEntityCondCompare() ;
	}

	@Override
	public CEntityCondAnd NewEntityCondAnd()
	{
		// Pure target-neutral semantic entity shared with the COBOL pipeline. It renders
		// through the SHARED recursive ST4 binding (semantic.expression.CEntityCondAnd ->
		// recursiveCondAndEntity, priority-1 grouping, " \n&& " join, ignored-operand
		// collapse), NOT the retired generate.fpacjava.CFPacJavaCondAnd direct backend.
		// Lowering stays in stage 1: the operands are set by the parser and the entity's
		// getEffectiveLeft/Right + isLeft/RightIgnored/Grouped getters are pure state reads.
		return new CEntityCondAnd() ;
	}

	@Override
	public CEntityClass NewEntityClass(int l, String name)	{
		// Pure target-neutral semantic root shared with the COBOL pipeline. The FPac program
		// wrapper renders through the FPac-recursive ST4 root binding (role FPAC_ROOT:
		// semantic.CEntityClass -> recursiveFPacClassEntity, "import nacaLib.fpacPrgEnv.* ;" +
		// "public class NAME extends FPacProgram" with the UPPERCASE fpacClassName), NOT the
		// retired generate.fpacjava.CFPacJavaClass direct backend and NOT the frozen COBOL
		// javaProgramRoot the shared ROOT manifest selects for this same class. Lowering stays
		// in stage 1: the entity is generate-neutral and its name/catalog are resolved here.
		CEntityClass e = new CEntityClass(l, name, programCatalog) ;
		generate.LegacyLanguageRenderer.bind(e, langOutput) ;
		return e ;
	}

	@Override
	public CEntityComment NewEntityComment(int l, String comment)
	{
		// Pure target-neutral semantic entity shared with the COBOL pipeline: a comment
		// renders through the SHARED recursive ST4 binding
		// (semantic.CEntityComment -> javaComment, "// <entity.comment; format=javaCommentText>")
		// which loadFpac() layers VERBATIM into the FPAC_REFERENCE role — no FPac-specific
		// override is required in semantic-fpac-bindings.properties, because the retired
		// generate.fpacjava.CFPacJavaComment.DoExport emitted exactly the same shape COBOL's
		// retired CJavaComment did ("// " + text, newlines escaped to "0x000A"/"Ox000D"
		// behind the historical indexOf > 0 gate, reproduced by the javaCommentText atomic
		// renderer). FPac header comments reach this template through the shared
		// CBaseLanguageExporter.renderComment bridge (role REFERENCE), and any comment child
		// of a retired verb container lowers through FPAC_REFERENCE; the template only reads
		// entity.comment, a pure getter (no FormatIdentifier/output work).
		CEntityComment e = new CEntityComment(l, programCatalog, comment) ;
		generate.LegacyLanguageRenderer.bind(e, langOutput) ;
		return e ;
	}

	@Override
	public CEntityAttribute NewEntityAttribute(int l, String name)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityStructure NewEntityStructure(int l, String name, String Level)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityProcedure NewEntityProcedure(int l, String name,	CEntityProcedureSection section)
	{
		return new CFPacJavaProcedure(l, name, programCatalog, langOutput, section) ;
	}

	@Override
	public CEntityProcedureSection NewEntityProcedureSection(int l, String name)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityAssign NewEntityAssign(int l)
	{
		// Pure target-neutral semantic entity shared with the COBOL pipeline: an assign
		// renders through the existing recursive ST4 binding
		// (semantic.Verbs.CEntityAssign -> recursiveMoveEntity, "move(<value>, <destination>);"),
		// not the retired generate.fpacjava direct backend CFPacJavaAssign. FPac only ever
		// populates value + destinations (CFPacAssign/CFPacMove/CFPacArithmeticOperation/
		// CFPacConvert call SetValue + AddRefTo; SetFillAll/SetAssignCorresponding are never
		// invoked from FPac), so the shared template always lowers to the plain "move(...)"
		// statement COBOL already emits for this same semantic entity. The template only reads
		// entity.value / entity.destinations; the parser/factory precomputes both.
		CEntityAssign e = new CEntityAssign(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}

	@Override
	public CEntityExternalDataStructure NewEntityExternalDataStructure(int l,
					String name)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityInline NewEntityInline(int l, CBaseExternalEntity ext)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCondition NewEntityCondition(int l)
	{
		return new CEntityCondition(l, programCatalog) ;
	}

	@Override
	public CEntityBloc NewEntityBloc(int l)
	{
		return new CEntityBloc(l, programCatalog) ;
	}

	@Override
	public CEntityCalcul NewEntityCalcul(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySqlOnErrorGoto NewEntitySQLOnErrorGoto(int l, String ref)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySqlOnErrorGoto NewEntitySQLOnWarningGoto(int l, String ref)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityExec NewEntityExec(int l, String statement)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityResourceFormContainer NewEntityFormContainer(int l,
					String name, boolean bSavCopy)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityResourceForm NewEntityForm(int l, String name,
					boolean bSavCopy)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityResourceField NewEntityEntryField(int l, String name)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityResourceField NewEntityLabelField(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCallFunction NewEntityCallFunction(int l, String reference,
					String refThru, CEntityProcedureSection section)
	{
		// Pure target-neutral semantic entity shared with the COBOL pipeline. FPac's
		// DOSUBR (CFPacDoSubr passes refThru=null, section=null) renders through the
		// FPac-recursive ST4 binding (semantic.Verbs.CEntityCallFunction ->
		// recursiveFPacCallFunctionEntity, "<name>() ;"), NOT the retired generate.fpacjava
		// direct backend CFPacJavaCallFunction and NOT the COBOL PERFORM template the shared
		// REFERENCE manifest selects for this same class. Mirroring the retired backend, the
		// call is built with an empty refThru and a null section (FPac never performs THRU a
		// range nor scopes calls to a section); passing the parser's null refThru straight
		// through would NPE in the entity constructor. Lowering stays in the factory; the
		// semantic entity stays generate-neutral and the reference is resolved in stage 1.
		CEntityCallFunction e = new CEntityCallFunction(l, programCatalog, reference, "", null) ;
		generate.LegacyLanguageRenderer.bind(e, langOutput) ;
		return e ;
	}

	@Override
	public CEntityInitialize NewEntityInitialize(int l, CDataEntity data)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityReturn NewEntityReturn(int l)
	{
		return new CFPacJavaReturn(l, programCatalog, langOutput) ;
	}

	@Override
	public CEntityCallProgram NewEntityCallProgram(int l, CDataEntity reference)
	{
		// Pure target-neutral semantic entity shared with the COBOL pipeline. FPac's CALL
		// (CFPacCall builds reference + checked flag + by-ref/by-value parameters) renders
		// through the FPac-recursive ST4 binding (semantic.Verbs.CEntityCallProgram ->
		// recursiveFPacCallProgramEntity), NOT the retired generate.fpacjava direct backend
		// CFPacJavaCallProgram and NOT the frozen COBOL recursiveCallProgramEntity the shared
		// REFERENCE manifest selects for this same class. FPac reuses the exact COBOL lowering
		// SHAPE — call(<program>).using(...).executeCall(); — but names generated program classes
		// UPPERCASE (CFPacJavaClass.DoExport), so the FPac template references the program class
		// with the fpacClassName format (call(PROG.class)) instead of COBOL's title-case
		// javaClassName (call(Prog.class)), which would not compile against "public class PROG
		// extends FPacProgram". Lowering stays in the factory; the semantic entity stays
		// generate-neutral and the reference/parameters are resolved in stage 1.
		CEntityCallProgram e = new CEntityCallProgram(l, programCatalog, reference) ;
		generate.LegacyLanguageRenderer.bind(e, langOutput) ;
		return e ;
	}

	@Override
	public CEntitySwitchCase NewEntitySwitchCase(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCase NewEntityCase(int l, int endline)
	{
		return new CEntityCase(l, programCatalog, endline) ;
	}

	@Override
	public CSubStringAttributReference NewEntitySubString(int l)
	{
		return new CFPacJavaSubStringAttributeReference(l, programCatalog,langOutput) ;
	}

	@Override
	public CEntityArrayReference NewEntityArrayReference(int l)
	{
		// Pure target-neutral semantic entity shared with the COBOL pipeline: an array
		// reference renders through the existing recursive ST4 binding
		// (semantic.CEntityArrayReference -> arrayReferenceEntity, "<reference>.getAt(<indexes>)"),
		// not the retired generate.fpacjava direct backend CFPacJavaArrayReference. That
		// backend's legacy "reference(idx)" output was not valid Java array access; the
		// canonical, compilable form is the naca-rt ".getAt(...)" accessor COBOL already
		// emits for this same semantic entity. The factory populates reference + indexes
		// (CEntityStructure.GetArrayReference et al.); the template only reads entity.*.
		CEntityArrayReference e = new CEntityArrayReference(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}

	@Override
	public CEntityGoto NewEntityGoto(int l, String Reference, CEntityProcedureSection section)
	{
		return new CFPacJavaGoto(l, programCatalog, langOutput, Reference, section) ;
	}

	@Override
	public CEntityGoto NewEntityGotoDepending(int l, List<String> refs, CDataEntity dep, CEntityProcedureSection section)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityLoopWhile NewEntityLoopWhile(int l)
	{
		return new CEntityLoopWhile(l, programCatalog) ;
	}

	@Override
	public CEntityLoopIter NewEntityLoopIter(int l)
	{
		return new CEntityLoopIter(l, programCatalog) ;
	}

	@Override
	public CEntityAddTo NewEntityAddTo(int l)
	{
		return new CEntityAddTo(l, programCatalog);
	}

	@Override
	public CEntityContinue NewEntityContinue(int l)
	{
		return new CEntityContinue(l, programCatalog) ;
	}

	@Override
	public CEntityNextSentence NewEntityNextSentence(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityNamedCondition NewEntityNamedCondition(int l, String name)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySQLSingleStatement NewEntitySQLSingleStatement(int l,
					String name)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySQLSelectStatement NewEntitySQLSelectStatement(int l,
					String name, Vector<CDataEntity> arrParameters,
					Vector<CDataEntity> arrInto, Vector<CDataEntity> arrInd)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySQLCursorSelectStatement NewEntitySQLCursorSelectStatement(
					int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySQLFetchStatement NewEntitySQLFetchStatement(int l,
					CEntitySQLCursor cur)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySQLOpenStatement NewEntitySQLOpenStatement(int l,
					CEntitySQLCursor cur)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySQLCloseStatement NewEntitySQLCloseStatement(int l,
					CEntitySQLCursor cur)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySQLDeleteStatement NewEntitySQLDeleteStatement(int l,
					String csStatement, Vector<CDataEntity> arrParameters)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySQLUpdateStatement NewEntitySQLUpdateStatement(int l,
					String csStatement, Vector<CDataEntity> arrSets,
					Vector<CDataEntity> arrParameters)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySQLInsertStatement NewEntitySQLInsertStatement(int l)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySQLDeclareTable NewEntitySQLDeclareTable(int nLine,
					String csTableName, String csViewName,
					ArrayList arrTableColDescription)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySetColor NewEntitySetColor(int l, CDataEntity field)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityFieldLength NewEntityFieldLengh(int l, String name,
					CDataEntity field)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityFieldData NewEntityFieldData(int l, String name,
					CDataEntity field)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityFieldColor NewEntityFieldColor(int l, String name,
					CDataEntity field)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityFieldAttribute NewEntityFieldAttribute(int l, String name,
					CDataEntity field)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityFieldHighlight NewEntityFieldHighlight(int l, String name,
					CDataEntity field)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityFieldFlag NewEntityFieldFlag(int l, String name,
					CDataEntity field)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityFieldValidated NewEntityFieldValidated(int l, String name,
					CDataEntity field)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySetHighligh NewEntitySetHighlight(int l, CDataEntity field)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySetFlag NewEntitySetFlag(int l, CDataEntity field)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySetCursor NewEntitySetCursor(int l, CDataEntity field)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySetAttribute NewEntitySetAttribute(int l, CDataEntity field)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityAssignWithAccessor NewEntityAssignWithAccessor(int l)
	{
		// Pure target-neutral semantic entity shared with the COBOL pipeline: an accessor
		// assignment renders through the existing recursive ST4 binding
		// (semantic.Verbs.CEntityAssignWithAccessor -> recursiveAssignWithAccessorEntity),
		// not the retired generate.fpacjava direct backend CFPacJavaAssignWithAccessor.
		// The only FPac destination that carries accessors is a CFPacJavaEnvironmentVariable
		// built from the rules engine (e.g. RETCD, writer "setReturnCode("), so the shared
		// template's environment branch lowers to "setReturnCode(<value>);" — the canonical
		// shape COBOL emits for this same entity. The retired backend appended a redundant
		// ".getInt()" plus a stray " ;"; both are dropped because BaseProgram declares a
		// setReturnCode(Var) overload that coerces the value internally. FPac never populates
		// fillAll and never builds a CEntitySQLCode, so the fillAll/SQLCode branches of the
		// shared template are unreachable from FPac. The template only reads
		// entity.environmentWriteAccessor / entity.value; the parser/factory precomputes both
		// (CFPacAssign calls SetAssign, CFPacMove calls SetRefTo + SetValue).
		CEntityAssignWithAccessor e = new CEntityAssignWithAccessor(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}

	@Override
	public CResourceStrings NewResourceString(int nbLines, int nbCols)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityEnvironmentVariable NewEntityEnvironmentVariable(
					String namev, String acc, boolean bNumeric)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityEnvironmentVariable NewEntityEnvironmentVariable(String namev, String acc, String write, boolean bNumeric)
	{
		return new CFPacJavaEnvironmentVariable(0, namev, programCatalog, langOutput, acc, write, bNumeric) ;
	}

	@Override
	public CEntitySkipFields NewEntityWorkingSkipField(int l, String name,
					int nbFields, String level)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityFieldRedefine NewEntityFieldRedefine(int l, String name,
					String level)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityFormRedefine NewEntityFormRedefine(int l, String name,
					CDataEntity eForm, boolean bSaveMap)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CDataEntity getSpecialConstantValue(String value)
	{
		return null ;
	}

	@Override
	public CEntityResourceFieldArray NewEntityFieldArray()
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySQLCode NewEntitySQLCode(String name)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySQLCode NewEntitySQLCode(String name,
					CBaseEntityExpression eHistoryItem)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityCondIsSQLCode NewEntityCondIsSQLCode()
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityRoutineEmulationCall NewEntityRoutineEmulationCall(int l)
	{
		return new CFPacJavaRoutineEmulationCall(l, programCatalog, langOutput) ;
	}

	@Override
	public CEntityConcat NewEntityConcat(CDataEntity e1, CDataEntity e2)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityList NewEntityList(String name)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityDigits NewEntityDigits(CDataEntity nel)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySearch NewEntitySearch(int line)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityInternalBool NewEntityInternalBool(String name)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityBreak NewEntityBreak(int line)
	{
		return new CEntityBreak(line, programCatalog) ;
	}

	@Override
	public CEntityFileDescriptor NewEntityFileDescriptor(int line, String name)
	{
		return new CFPacJavaFileDescriptor(line, name, programCatalog, langOutput) ;
	}

	@Override
	public CEntitySortedFileDescriptor NewEntitySortedFileDescriptor(int line,
					String name)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityOpenFile NewEntityOpenFile(int line)
	{
		return new CFpacJavaOpenFile(line, programCatalog, langOutput) ;
	}

	@Override
	public CEntityCloseFile NewEntityCloseFile(int line)
	{
		// Pure target-neutral semantic entity shared with the COBOL pipeline: an FPac CLOSE-
		// renders through the existing recursive ST4 binding
		// (semantic.Verbs.CEntityCloseFile -> recursiveCloseFileEntity,
		// "<entity.fileDescriptor>.close();"), NOT the retired generate.fpacjava direct
		// backend CFPacJavaCloseFile. FPac's lowering is the SAME shape COBOL already emits
		// for this same semantic entity (descriptor.getFormattedName() + ".close()"), so the
		// shared reference binding is reused verbatim — no FPac override is needed in
		// semantic-fpac-bindings.properties (unlike CEntityCallFunction/CEntityCallProgram,
		// whose FPac lowering genuinely differs). The parser populates the descriptor
		// (CFPacClose.DoCustomSemanticAnalysis and CFPacCodeBloc call setFileDescriptor); the
		// template only reads entity.fileDescriptor, a pure getter (getFormattedName performs
		// no FormatIdentifier/output work). The factory binds the legacy output controller AT
		// CREATION: CBaseLanguageEntity.AddChild does NOT propagate output bindings and
		// FPacTranscoderEngine performs no whole-tree bind pass, so without this bind a CLOSE-
		// in any FPac program would throw "No legacy output controller bound to
		// semantic.Verbs.CEntityCloseFile" once CFPacJavaProcedure.DoExport routes the retired
		// verb through the assembler (exportChildren(this, false, FPAC_REFERENCE)).
		CEntityCloseFile e = new CEntityCloseFile(line, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}

	@Override
	public CEntityReadFile NewEntityReadFile(int line)
	{
		return new CFPacJavaReadFile(line, programCatalog, langOutput) ;
	}

	@Override
	public CEntityWriteFile NewEntityWriteFile(int line)
	{
		return new CFPacJavaWriteFile(line, programCatalog, langOutput) ;
	}

	@Override
	public CEntityAccept NewEntityAccept(int line)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySort NewEntitySort(int line)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySortRelease NewEntitySortRelease(int line)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntitySortReturn NewEntitySortReturn(int line)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityRewriteFile NewEntityRewriteFile(int line)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	@Override
	public CEntityAddress NewEntityAddress(String csAddress)
	{
		// Pure target-neutral semantic entity: the address literal is fully resolved
		// here and rendered through the recursive ST4 binding (addressExpressionEntity),
		// not a generate.fpacjava direct backend.
		return new CEntityAddress(programCatalog, csAddress);
	}

	@Override
	public CEntityFunctionCall NewEntityFunctionCall(String mehodName, CDataEntity object)
	{
		if (mehodName.equalsIgnoreCase("ReadAndTestFile"))
		{
			return new CFPacJavaReadAndTestFile(programCatalog, langOutput, object) ;
		}
		else
		{
			throw new NacaTransAssertException("Method not implemented") ;
		}
	}

	@Override
	public CEntityCondIsBoolean NewEntityCondIsBoolean()
	{
		return new CFPacJavaCondIsBoolean() ;
	}
	
	/**
	 * @see semantic.CBaseEntityFactory#NewEntitySQLLock(int)
	 */
	@Override
	public CEntitySQLSessionDeclare NewEntitySQLSessionDeclare(int line)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}
	
	/**
	 * @see semantic.CBaseEntityFactory#NewEntitySQLLock(int)
	 */
	@Override
	public CEntitySQLSessionDrop NewEntitySQLSessionDrop(int line)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	/**
	 * @see semantic.CBaseEntityFactory#NewEntitySQLLock(int)
	 */
	@Override
	public CEntitySQLLock NewEntitySQLLock(int line)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	/**
	 * @see semantic.CBaseEntityFactory#NewEntitySQLExecute(int)
	 */
	@Override
	public CEntitySQLExecute NewEntitySQLExecute(int line)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	/**
	 * @see semantic.CBaseEntityFactory#NewEntityFormatedVarReference(semantic.CDataEntity, java.lang.String)
	 */
	@Override
	public CEntityFormatedVarReference NewEntityFormatedVarReference(CDataEntity object, String format)
	{
		return new CFPacJavaFormatedVarReference(object, programCatalog, langOutput, format) ;
	}
	@Override
	public CEntityInc NewEntityInc(int line)
	{
		return new CEntityInc(line, programCatalog);
	}

	/**
	 * @see semantic.CBaseEntityFactory#NewEntityConvert(int)
	 */
	@Override
	public CEntityConvertReference NewEntityConvert(int line)
	{
		return new CFPacJavaConvertReference(programCatalog, langOutput) ;
	}

	/**
	 * @see semantic.CBaseEntityFactory#NewEntityIsFileEOF(semantic.CEntityFileBuffer)
	 */
	@Override
	public CEntityIsFileEOF NewEntityIsFileEOF(CEntityFileDescriptor fb)
	{
		return new CFPacJavaIsFileEOF(fb) ;
	}

	/**
	 * @see semantic.CBaseEntityFactory#NewEntityConstant(semantic.expression.CEntityConstant.Value)
	 */
	@Override
	public CEntityConstant NewEntityConstant(Value val)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}

	/**
	 * @see semantic.CBaseEntityFactory#addSpecialConstantValue(java.lang.String, java.lang.String)
	 */
	@Override
	public void addSpecialConstantValue(String value, String constant)
	{
		
	}

	/**
	 * @see semantic.CBaseEntityFactory#getAllSpecialConstantAttributes()
	 */
	@Override
	public Collection<CDataEntity> getAllSpecialConstantAttributes()
	{
		throw new NacaTransAssertException("Method not implemented") ;
	}

	/**
	 * @see semantic.CBaseEntityFactory#NewEntityFileDescriptorLengthDependency(java.lang.String)
	 */
	@Override
	public CEntityFileDescriptorLengthDependency NewEntityFileDescriptorLengthDependency(String name)
	{
		throw new NacaTransAssertException("Method not implemented") ;
	}

	public CEntityAssignSpecial NewEntityAssignSpecial(int l)	{
		// Pure target-neutral semantic entity: a packed FPac move renders through the
		// declarative recursive ST4 binding
		// (semantic.Verbs.CEntityAssignSpecial -> recursiveMovePackedEntity,
		// "movePacked(<source>, <destination>);"), not the retired generate.fpacjava
		// direct backend CFPacJavaAssignSpecial. FPac populates source + destination +
		// arithmeticAssign (CFPacMove's packed branch calls setDestination + setSource
		// + setArithmeticAssign); the template only reads entity.source /
		// entity.destination, which the parser precomputes. The runtime call is legal:
		// nacaLib.fpacPrgEnv.FPacProgram declares movePacked(Var, Var), delegating to
		// nacaLib.basePrgEnv.BaseProgram.move(Var, Var).
		CEntityAssignSpecial e = new CEntityAssignSpecial(l, programCatalog);
		generate.LegacyLanguageRenderer.bind(e, langOutput);
		return e;
	}

	/**
	 * @see semantic.CBaseEntityFactory#NewEntitySQLCall(int)
	 */
	@Override
	public CEntitySQLCall NewEntitySQLCall(int line)
	{
		// TODO Auto-generated method stub
		throw new NacaTransAssertException("Method not implemented") ;
	}
	
}
