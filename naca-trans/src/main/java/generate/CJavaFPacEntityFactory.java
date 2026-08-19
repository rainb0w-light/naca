/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate;

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

    private NacaTransAssertException unsupportedOperation()
    {
        String operation = StackWalker.getInstance().walk(frames -> frames
            .map(StackWalker.StackFrame::getMethodName)
            .filter(method -> method.startsWith("NewEntity"))
            .findFirst()
            .orElse("unknown factory operation"));
        return new NacaTransAssertException(
            "FPac grammar does not support semantic factory operation " + operation);
    }

    @Override
    public CEntityIsFieldCursor NewEntityIsFieldCursor()
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityResetKeyPressed NewEntityResetKeyPressed(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSGetMain NewEntityCICSGetMain(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityUnknownReference NewEntityUnknownReference(int nLine, String csName)
    {
        CFPacUnknownReference entity =
            new CFPacUnknownReference(nLine, csName, programCatalog);
        return entity;
    }

    @Override
    public CEntityFieldOccurs NewEntityFieldOccurs(int i, String string)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityGetKeyPressed NewEntityGetKeyPressed(String name)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityIsKeyPressed NewEntityIsKeyPressed()
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityKeyPressed NewEntityKeyPressed(String name, String caption)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySQLCursor NewEntitySQLCursor(String name)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityIndex NewEntityIndex(String name)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityFieldArrayReference NewEntityFieldArrayReference(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySQLCursorSection NewEntitySQLCursorSection()
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityProcedureDivision NewEntityProcedureDivision(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSReWrite NewEntityCICSReWrite(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSDelay NewEntityCICSDelay(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSSetTDQueue NewEntityCICSSetTDQueue(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSDeQ NewEntityCICSDeQ(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSEnQ NewEntityCICSEnQ(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCount NewEntityCount(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityInspectConverting NewEntityInspectConverting(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityDisplay NewEntityDisplay(int l, Upon t)
    {
        // Pure target-neutral semantic entity rendered through the recursive ST4 binding
        // semantic.Verbs.CEntityDisplay -> recursiveFPacDisplayEntity (FPAC_REFERENCE role,
        // layered over the shared reference manifest by semantic-fpac-bindings.properties so
        // the frozen COBOL `display` binding is left untouched). The retired FPac direct display
        // backend wrote, per operand, "wto.display(" + LegacyDataRenderer.renderReference(item,
        // getLine()) + ") ;". That exact reference-rendering bridge is INJECTED here (the same
        // LegacyDataRenderer::renderReference idiom the BMS field attributes use) so the entity's
        // getDisplayReferences() reproduces the retired output byte-for-byte for every operand
        // shape CFPacWTO builds -- a positioned substring wrapping a conversion buffer, a numeric
        // or string literal, an undefined reference, or a plain field -- without the semantic tree
        // naming any backend type. Routing those operands through the shared assembler reference
        // walk instead would re-route the still-legacy FPac operand types to the COBOL templates
        // and diverge (or emit invalid Java). Replaced the retired generate.fpacjava display
        // backend; mirrors the conversion-reference retirement (pure entity + legacy output bind).
        CEntityDisplay e = new CEntityDisplay(l, programCatalog, t) ;
        return e ;
    }

    @Override
    public CEntityCICSAssign NewEntityCICSAssign(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSWriteQ NewEntityCICSWriteQ(int l, boolean b)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSReadQ NewEntityCICSReadQ(int l, boolean b)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSDeleteQ NewEntityCICSDeleteQ(int l, boolean b)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSStartBrowse NewEntityCICSStartBrowse(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSRead NewEntityCICSRead(int l, CEntityCICSReadMode mode)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSAbend NewEntityCICSAbend(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSInquire NewEntityCICSInquire(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSSyncPoint NewEntityCICSSyncPoint(int l, boolean bRollBack)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityIsFieldModified NewEntityIsFieldModified()
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSReceiveMap NewEntityCICSReceiveMap(int l, CDataEntity name)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSWrite NewEntityCICSWrite(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSSendMap NewEntityCICSSendMap(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSReturn NewEntityCICSReturn(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSStart NewEntityCICSStart(int l, CDataEntity tid)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSRetrieve NewEntityCICSRetreive(int l, boolean bPointer)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSIgnoreCondition NewEntityCICSIgnoreCondition(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSHandleCondition NewEntityCICSHandleCondition(int line)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSHandleAID NewEntityCICSHandleAID(int line)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCurrentDate NewEntityCurrentDate()
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityIntrinsicFunction NewEntityIntrinsicFunction(String functionName, List<CBaseEntityExpression> arguments)
    {
        // The FPac parser has no live intrinsic-function producer, but the factory
        // contract still returns the same pure semantic function used by COBOL. Its
        // shared recursive template is byte-identical to the retired backend.
        CEntityIntrinsicFunction entity =
            new CEntityIntrinsicFunction(programCatalog, functionName, arguments) ;
        return entity ;
    }

    @Override
    public CEntityAddressOf NewEntityAddressOf(CDataEntity data)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityLengthOf NewEntityLengthOf(CDataEntity data)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSXctl NewEntityCICSXctl(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSLink NewEntityCICSLink(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSAddress NewEntityCICSAddress(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCICSAskTime NewEntityCICSAskTime(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityExprOpposite NewEntityExprOpposite()
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySQLCommit NewEntitySQLCommit(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySQLRollBack NewEntitySQLRollBack(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityParseString NewEntityParseString(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityMultiply NewEntityMultiply(int l)
    {
        CEntityMultiply entity = new CEntityMultiply(l, programCatalog) ;
        return entity ;
    }

    @Override
    public CEntityDivide NewEntityDivide(int l)
    {
        // Pure target-neutral semantic entity rendered through the recursive ST4 binding
        // semantic.Verbs.CEntityDivide -> recursiveFPacDivideEntity (FPAC_REFERENCE role, layered
        // over the shared reference manifest by semantic-fpac-bindings.properties so the frozen
        // COBOL recursiveDivideEntity binding is left untouched). The retired FPac direct divide
        // backend wrote "divide(" + renderReference(what) + ", " + renderReference(by) + ").to(" +
        // renderReference(result) + ") ;" (the FPac parser's D operation calls SetDivide(var2, var1,
        // false), so result == what and the backend never emitted the rounded/remainder branches).
        // That exact reference-rendering bridge is INJECTED here (the same LegacyDataRenderer::
        // renderReference idiom the FPac display retirement uses) so the entity's
        // getDividendReference/getDivisorReference/getResultReference reproduce the retired output
        // byte-for-byte for every operand shape CFPacArithmeticOperation builds -- a positioned
        // substring wrapping a conversion buffer, a numeric or string literal, an undefined
        // reference, or a plain field -- without the semantic tree naming any backend type. Routing
        // those operands through the shared assembler reference walk instead would re-route the
        // still-legacy FPac operand types to the COBOL templates and diverge (or emit invalid Java).
        // Replaced the retired generate.fpacjava.CFPacJavaDivide backend.
        CEntityDivide e = new CEntityDivide(l, programCatalog) ;
        return e ;
    }

    @Override
    public CEntityStringConcat NewEntityStringConcat(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityIsFieldHighlight NewEntityIsFieldHighlight(CDataEntity ref)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityReplace NewEntityReplace(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityDataSection NewEntityDataSection(int l, String name)
    {
        // Pure target-neutral semantic entity shared with the COBOL pipeline. The FPac
        // declaration zone (parser/FPac/elements/CFPacDeclarationZone) lowers IPF/OPF/UPF
        // file declarations into a "DeclarationSection" data section whose ONLY children are
        // still-legacy FPac file descriptors (CFPacJavaFileDescriptor). The retired
        // generate.fpacjava.CFPacJavaDataSection direct backend carried no code of its own —
        // its DoExport was exactly exportChildren(this, false): a transparent container that
        // renders its children in place at the class-body block level. The FPac program-root
        // bridge (FPacTranscoderEngine.exportFpacProgramRoot) reproduces that byte-for-byte by
        // flattening this backend-less container: its file-descriptor children are driven by
        // reflection (FPacFileDescriptor NAME = declare.fpacFile("NAME").file() ;). It is
        // DELIBERATELY NOT routed through the recursive assembler: the shared
        // semantic.CEntityDataSection=dataSectionDeclaration binding would lower those
        // file-descriptor children under the frozen COBOL declaration binding
        // (FileDescriptor NAME = declare.file(...)), which does not compile against
        // nacaLib.fpacPrgEnv.FPacProgram. This converges to renderRoot(eSem, FPAC_ROOT) once
        // the FPac file-descriptor backends retire. The factory binds the legacy output
        // controller AT CREATION (AddChild does not propagate bindings and FPacTranscoderEngine
        // performs no whole-tree bind pass), mirroring the COBOL factory and every retired FPac
        // verb. Lowering stays in stage 1; the semantic entity stays generate-neutral.
        CEntityDataSection e = new CEntityDataSection(l, name, programCatalog);
        return e;
    }

    @Override
    public CEntityIsNamedCondition NewEntityIsNamedCondition()
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySubtractTo NewEntitySubtractTo(int l)
    {
        CEntitySubtractTo entity = new CEntitySubtractTo(l, programCatalog) ;
        return entity;
    }

    @Override
    public CEntityMoveReference NewEntityMoveReference(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityAddressReference NewEntityAddressReference(CDataEntity ref)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityIsFieldAttribute NewEntityIsFieldAttribute()
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityIsFieldColor NewEntityIsFieldColor()
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySetConstant NewEntitySetConstant(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityIsFieldFlag NewEntityIsFieldFlag()
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCondIsConstant NewEntityCondIsConstant()
    {
        // Direct backend CFPacJavaCondIsConstant retired: the pure semantic entity is
        // rendered by the SHARED recursive ST4 binding
        // (semantic.expression.CEntityCondIsConstant -> recursiveCondIsConstantEntity).
        // The parser (parser/FPac/CFPacGenericExpression, IF X = SPACE/LOW-VALUE/...)
        // populates it via SetIsZero/SetIsSpace/SetIsLowValue/SetIsHighValue + SetOpposite,
        // and the template emits is[Not](Zero|Space|LowValue|HighValue)(<reference>) —
        // byte-identical to the deleted Export(). The priority (7) and the opposite rebuild
        // (copy the reference, flip the flag) live on the target-neutral semantic entity.
        return new CEntityCondIsConstant() ;
    }

    @Override
    public CEntityCondIsKindOf NewEntityCondIsKindOf()
    {
        // Direct backend CFPacJavaCondIsKindOf retired: the pure semantic entity is
        // rendered by the SHARED recursive ST4 binding
        // (semantic.expression.CEntityCondIsKindOf -> recursiveCondIsKindOfEntity),
        // the canonical template COBOL already emits for this same target-neutral
        // entity. The FPac parser (parser/FPac/CFPacGenericExpression.AnalyseSingleOperand)
        // lowers "IF X NUMERIC" via SetIsNumeric, and the shared parser/condition
        // CCondIsNumeric adds setOpposite for the negated form; the template emits
        // is[Not](Numeric|Alphabetic|AlphabeticLower|AlphabeticUpper)(<reference>) —
        // byte-identical to the deleted Export() for the numeric/alphabetic kinds FPac
        // actually lowers. (The deleted backend rendered the lower/upper kinds — which
        // the FPac parser never produces — as a bare "is(<reference>)", invalid Java;
        // the shared canonical template emits the compilable isAlphabeticLower/
        // isAlphabeticUpper BaseProgram calls instead.) The priority (7) and the
        // opposite rebuild (copy the reference, flip the flag) live on the
        // target-neutral semantic entity; the is[Not]Numeric/is[Not]Alphabetic[Lower|
        // Upper] predicates are nacaLib.basePrgEnv.BaseProgram runtime calls that
        // FPacProgram inherits, so no FPac-specific runtime operation is introduced.
        return new CEntityCondIsKindOf() ;
    }

    @Override
    public CEntityCondIsAll NewEntityCondIsAll()
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityString NewEntityString(char[] value)
    {
        CEntityString entity = new CEntityString(programCatalog, value) ;
        return entity ;
    }

    @Override
    public CEntityCondOr NewEntityCondOr()
    {
        // Pure target-neutral semantic entity shared with the COBOL pipeline. It renders
        // through the SHARED recursive ST4 binding (semantic.expression.CEntityCondOr ->
        // recursiveCondOrEntity, priority-2 grouping, " \n|| " join, ignored-operand
        // collapse), NOT the retired generate.fpacjava.CFPacJavaCondOr direct backend.
        // The FPac parser lowers "cond1 OR cond2" through parser/FPac/CFPacElement ->
        // parser.condition.CCondOrStatement.AnalyseCondition, which calls NewEntityCondOr +
        // SetCondition (stage 1); the entity's getEffectiveLeft/Right +
        // isLeft/RightIgnored/Grouped getters consumed by the template are pure state reads
        // and mirror exactly the grouping rule the deleted backend applied via
        // CJavaExporter.ExportChildCondition(GetPriorityLevel(), op). The priority (2) and
        // the opposite rebuild (De Morgan: NOT(a OR b) == (NOT a) AND (NOT b)) live on the
        // target-neutral semantic entity, exactly as the retired backend's overrides did.
        return new CEntityCondOr() ;
    }

    @Override
    public CEntityNumber NewEntityNumber(String value)
    {
        CEntityNumber entity = new CEntityNumber(programCatalog, value) ;
        entity.preserveSourceLexeme() ;
        entity.setValueNeeded(false) ;
        return entity ;
    }

    @Override
    public CEntityExprTerminal NewEntityExprTerminal(CDataEntity eData)
    {
        // Pure target-neutral semantic entity shared with the COBOL pipeline. FPac's expression
        // terminals -- the single-operand leaves the FPac parser builds via
        // factory.NewEntityExprTerminal(term) (parser/FPac/CFPacGenericExpression, CFPacMove,
        // CFPacArithmeticOperation, CFPacDoLoop, CFPacWTO, CFPacCall, OperandDescription, plus the
        // shared CCondEqualsStatement/CTermExpression when the FPac factory is active) -- render
        // through the SHARED recursive ST4 binding
        // (semantic.expression.CEntityExprTerminal -> expressionTerminalEntity, "<entity.term>"),
        // NOT the retired generate.fpacjava.CFPacJavaExprTerminal direct backend. That backend's
        // Export() override (renderReference(term, getLine())) was DEAD on every reachable FPac
        // path: a terminal is only ever rendered as an operand child (term/left/right/condition) of
        // a condition/expression template, whose child role defaults to REFERENCE, so the assembler
        // dispatches on the target-neutral semantic class CEntityExprTerminal and walks the term
        // through the shared reference manifest -- byte-identical output with or without the
        // generate.fpacjava subclass (the binding resolves by superclass walk today already). The
        // The former reflective intrinsic-function consumer was unreachable from FPac:
        // no FPac parser node calls
        // NewEntityIntrinsicFunction. Mirrors the CEntityExprSum / CEntityAddress / CEntityCondIsBoolean
        // retirements (pure entity + shared assembler, dead Export removed). No FPac override belongs
        // in semantic-fpac-bindings.properties: FPac's terminal lowering is the bare term reference,
        // identical in shape to COBOL's, and the FPAC_REFERENCE role is never consulted for an operand
        // child (JavaTemplateAssembler.childRole routes term/left/right to REFERENCE). Lowering stays
        // in stage 1; the factory only constructs the entity the parser then populates.
        return new CEntityExprTerminal(eData) ;
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
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCondNot NewEntityCondNot()
    {
        throw unsupportedOperation() ;
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
    public CEntityClass NewEntityClass(int l, String name)  {
        // Pure target-neutral semantic root shared with the COBOL pipeline. The FPac program
        // wrapper renders through the FPac-recursive ST4 root binding (role FPAC_ROOT:
        // semantic.CEntityClass -> recursiveFPacClassEntity, "import nacaLib.fpacPrgEnv.* ;" +
        // "public class NAME extends FPacProgram" with the UPPERCASE fpacClassName), NOT the
        // retired generate.fpacjava.CFPacJavaClass direct backend and NOT the frozen COBOL
        // javaProgramRoot the shared ROOT manifest selects for this same class. Lowering stays
        // in stage 1: the entity is generate-neutral and its name/catalog are resolved here.
        CEntityClass e = new CEntityClass(l, name, programCatalog) ;
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
        return e ;
    }

    @Override
    public CEntityAttribute NewEntityAttribute(int l, String name)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityStructure NewEntityStructure(int l, String name, String Level)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityProcedure NewEntityProcedure(int l, String name,  CEntityProcedureSection section)
    {
        CEntityProcedure entity = new CEntityProcedure(l, name, programCatalog, section) ;
        return entity ;
    }

    @Override
    public CEntityProcedureSection NewEntityProcedureSection(int l, String name)
    {
        throw unsupportedOperation() ;
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
        return e;
    }

    @Override
    public CEntityExternalDataStructure NewEntityExternalDataStructure(int l,
                    String name)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityInline NewEntityInline(int l, CBaseExternalEntity ext)
    {
        throw unsupportedOperation() ;
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
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySqlOnErrorGoto NewEntitySQLOnErrorGoto(int l, String ref)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySqlOnErrorGoto NewEntitySQLOnWarningGoto(int l, String ref)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityExec NewEntityExec(int l, String statement)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityResourceFormContainer NewEntityFormContainer(int l,
                    String name, boolean bSavCopy)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityResourceForm NewEntityForm(int l, String name,
                    boolean bSavCopy)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityResourceField NewEntityEntryField(int l, String name)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityResourceField NewEntityLabelField(int l)
    {
        throw unsupportedOperation() ;
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
        return e ;
    }

    @Override
    public CEntityInitialize NewEntityInitialize(int l, CDataEntity data)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityReturn NewEntityReturn(int l)
    {
        CEntityReturn entity = new CEntityReturn(l, programCatalog) ;
        return entity ;
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
        return e ;
    }

    @Override
    public CEntitySwitchCase NewEntitySwitchCase(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCase NewEntityCase(int l, int endline)
    {
        return new CEntityCase(l, programCatalog, endline) ;
    }

    @Override
    public CSubStringAttributReference NewEntitySubString(int l)
    {
        CPositionedBufferReference entity =
            new CPositionedBufferReference(l, programCatalog) ;
        return entity ;
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
        return e;
    }

    @Override
    public CEntityGoto NewEntityGoto(int l, String Reference, CEntityProcedureSection section)
    {
        // FPac GOEND/GOLAST returns the reserved FPacProgram control token. Keep
        // the shared semantic GO TO node, but lower it through the FPAC_REFERENCE
        // override rather than COBOL's goTo(paragraph) template.
        CEntityGoto entity = new CEntityGoto(l, programCatalog, Reference, section) ;
        return entity ;
    }

    @Override
    public CEntityGoto NewEntityGotoDepending(int l, List<String> refs, CDataEntity dep, CEntityProcedureSection section)
    {
        throw unsupportedOperation() ;
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
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityNamedCondition NewEntityNamedCondition(int l, String name)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySQLSingleStatement NewEntitySQLSingleStatement(int l,
                    String name)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySQLSelectStatement NewEntitySQLSelectStatement(int l,
                    String name, Vector<CDataEntity> arrParameters,
                    Vector<CDataEntity> arrInto, Vector<CDataEntity> arrInd)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySQLCursorSelectStatement NewEntitySQLCursorSelectStatement(
                    int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySQLFetchStatement NewEntitySQLFetchStatement(int l,
                    CEntitySQLCursor cur)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySQLOpenStatement NewEntitySQLOpenStatement(int l,
                    CEntitySQLCursor cur)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySQLCloseStatement NewEntitySQLCloseStatement(int l,
                    CEntitySQLCursor cur)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySQLDeleteStatement NewEntitySQLDeleteStatement(int l,
                    String csStatement, Vector<CDataEntity> arrParameters)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySQLUpdateStatement NewEntitySQLUpdateStatement(int l,
                    String csStatement, Vector<CDataEntity> arrSets,
                    Vector<CDataEntity> arrParameters)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySQLInsertStatement NewEntitySQLInsertStatement(int l)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySQLDeclareTable NewEntitySQLDeclareTable(int nLine,
                    String csTableName, String csViewName,
                    ArrayList arrTableColDescription)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySetColor NewEntitySetColor(int l, CDataEntity field)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityFieldLength NewEntityFieldLengh(int l, String name,
                    CDataEntity field)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityFieldData NewEntityFieldData(int l, String name,
                    CDataEntity field)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityFieldColor NewEntityFieldColor(int l, String name,
                    CDataEntity field)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityFieldAttribute NewEntityFieldAttribute(int l, String name,
                    CDataEntity field)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityFieldHighlight NewEntityFieldHighlight(int l, String name,
                    CDataEntity field)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityFieldFlag NewEntityFieldFlag(int l, String name,
                    CDataEntity field)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityFieldValidated NewEntityFieldValidated(int l, String name,
                    CDataEntity field)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySetHighligh NewEntitySetHighlight(int l, CDataEntity field)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySetFlag NewEntitySetFlag(int l, CDataEntity field)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySetCursor NewEntitySetCursor(int l, CDataEntity field)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySetAttribute NewEntitySetAttribute(int l, CDataEntity field)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityAssignWithAccessor NewEntityAssignWithAccessor(int l)
    {
        // Pure target-neutral semantic entity shared with the COBOL pipeline: an accessor
        // assignment renders through the existing recursive ST4 binding
        // (semantic.Verbs.CEntityAssignWithAccessor -> recursiveAssignWithAccessorEntity),
        // not the retired generate.fpacjava direct backend CFPacJavaAssignWithAccessor.
        // The only FPac destination that carries accessors is a CEntityEnvironmentVariable
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
        return e;
    }

    @Override
    public CResourceStrings NewResourceString(int nbLines, int nbCols)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityEnvironmentVariable NewEntityEnvironmentVariable(
                    String namev, String acc, boolean bNumeric)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityEnvironmentVariable NewEntityEnvironmentVariable(String namev, String acc, String write, boolean bNumeric)
    {
        // Pure target-neutral semantic entity shared with the COBOL pipeline: an environment
        // variable reference renders through the existing recursive ST4 binding
        // (semantic.CEntityEnvironmentVariable -> environmentVariableEntity, "<entity.readAccessor>"),
        // not the retired generate.fpacjava direct backend CFPacJavaEnvironmentVariable. That
        // backend's ExportReference returned csAccessor, exactly what getReadAccessor() exposes,
        // and its HasAccessors()/isValNeeded() overrides were identical to the shared base, so the
        // shared binding is byte-equivalent for the read path. The write path (e.g. RETCD, writer
        // "setReturnCode(") lowers through the shared CEntityAssignWithAccessor ->
        // recursiveAssignWithAccessorEntity binding to "setReturnCode(<value>);" (see
        // NewEntityAssignWithAccessor above). The binding is inherited by the FPac reference role
        // verbatim (JavaSemanticTemplateBindings.loadFpac layers the shared concrete manifest), so
        // no FPac override is needed. The parser/rules engine precomputes accessor/writer/numeric
        // at construction; the template only reads entity.readAccessor.
        CEntityEnvironmentVariable e =
            new CEntityEnvironmentVariable(0, namev, programCatalog, acc, write, bNumeric);
        return e;
    }

    @Override
    public CEntitySkipFields NewEntityWorkingSkipField(int l, String name,
                    int nbFields, String level)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityFieldRedefine NewEntityFieldRedefine(int l, String name,
                    String level)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityFormRedefine NewEntityFormRedefine(int l, String name,
                    CDataEntity eForm, boolean bSaveMap)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CDataEntity getSpecialConstantValue(String value)
    {
        return null ;
    }

    @Override
    public CEntityResourceFieldArray NewEntityFieldArray()
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySQLCode NewEntitySQLCode(String name)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySQLCode NewEntitySQLCode(String name,
                    CBaseEntityExpression eHistoryItem)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityCondIsSQLCode NewEntityCondIsSQLCode()
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityRoutineEmulationCall NewEntityRoutineEmulationCall(int l)
    {
        CEntityRoutineEmulationCall entity =
            new CEntityRoutineEmulationCall(l, programCatalog) ;
        return entity ;
    }

    @Override
    public CEntityConcat NewEntityConcat(CDataEntity e1, CDataEntity e2)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityList NewEntityList(String name)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityDigits NewEntityDigits(CDataEntity nel)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySearch NewEntitySearch(int line)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityInternalBool NewEntityInternalBool(String name)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityBreak NewEntityBreak(int line)
    {
        return new CEntityBreak(line, programCatalog) ;
    }

    @Override
    public CEntityFileDescriptor NewEntityFileDescriptor(int line, String name)
    {
        // Pure target-neutral descriptor. FPac declaration syntax is selected by the
        // FPAC_REFERENCE manifest (recursiveFPacFileDescriptorEntity), while the shared
        // COBOL declaration binding remains unchanged. Bind the compatibility output at
        // construction because the transitional FPac root still forwards assembler text
        // through LegacyLanguageRenderer until the procedure container also retires.
        CEntityFileDescriptor entity = new CEntityFileDescriptor(line, name, programCatalog);
        return entity;
    }

    @Override
    public CEntitySortedFileDescriptor NewEntitySortedFileDescriptor(int line,
                    String name)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityOpenFile NewEntityOpenFile(int line)
    {
        CEntityOpenFile entity = new CEntityOpenFile(line, programCatalog) ;
        return entity ;
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
        return e;
    }

    @Override
    public CEntityReadFile NewEntityReadFile(int line)
    {
        CEntityReadFile entity = new CEntityReadFile(line, programCatalog) ;
        return entity ;
    }

    @Override
    public CEntityWriteFile NewEntityWriteFile(int line)
    {
        CEntityWriteFile entity = new CEntityWriteFile(line, programCatalog) ;
        return entity ;
    }

    @Override
    public CEntityAccept NewEntityAccept(int line)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySort NewEntitySort(int line)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySortRelease NewEntitySortRelease(int line)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntitySortReturn NewEntitySortReturn(int line)
    {
        throw unsupportedOperation() ;
    }

    @Override
    public CEntityRewriteFile NewEntityRewriteFile(int line)
    {
        throw unsupportedOperation() ;
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
            CEntityFunctionCall entity = new CEntityFunctionCall(programCatalog, object) ;
            entity.CallFunction(mehodName) ;
            return entity ;
        }
        else
        {
            throw unsupportedOperation() ;
        }
    }

    @Override
    public CEntityCondIsBoolean NewEntityCondIsBoolean()
    {
        // Pure target-neutral semantic entity shared with the COBOL pipeline (COBOL
        // itself never instantiates it — CJavaEntityFactory.NewEntityCondIsBoolean
        // throws — so FPac is the sole producer). It renders through the SHARED
        // recursive ST4 binding (semantic.expression.CEntityCondIsBoolean ->
        // recursiveCondIsBooleanEntity), emitting the bare data reference, prefixed
        // with "!" when the condition is negated — exactly the shape the retired
        // generate.fpacjava.CFPacJavaCondIsBoolean.Export() produced. No runtime
        // operation is emitted beyond the reference itself, which unfolds through the
        // assembler's reference binding. Lowering stays in stage 1: the parser sets
        // the reference and the true/false flag via setIsTrue/setIsFalse
        // (parser/FPac/elements/CFPacCodeBloc, the IF <file-buffer> READ-AND-TEST
        // pattern), and the entity's isTrueValue/getConditionReference getters
        // consumed by the template are pure state reads. GetOppositeCondition now
        // lives on the semantic entity and rebuilds a negated pure entity, exactly
        // as the retired backend's override did (copy reference, flip flag).
        return new CEntityCondIsBoolean() ;
    }

    /**
     * @see semantic.CBaseEntityFactory#NewEntitySQLLock(int)
     */
    @Override
    public CEntitySQLSessionDeclare NewEntitySQLSessionDeclare(int line)
    {
        throw unsupportedOperation() ;
    }

    /**
     * @see semantic.CBaseEntityFactory#NewEntitySQLLock(int)
     */
    @Override
    public CEntitySQLSessionDrop NewEntitySQLSessionDrop(int line)
    {
        throw unsupportedOperation() ;
    }

    /**
     * @see semantic.CBaseEntityFactory#NewEntitySQLLock(int)
     */
    @Override
    public CEntitySQLLock NewEntitySQLLock(int line)
    {
        throw unsupportedOperation() ;
    }

    /**
     * @see semantic.CBaseEntityFactory#NewEntitySQLExecute(int)
     */
    @Override
    public CEntitySQLExecute NewEntitySQLExecute(int line)
    {
        throw unsupportedOperation() ;
    }

    /**
     * @see semantic.CBaseEntityFactory#NewEntityFormatedVarReference(semantic.CDataEntity, java.lang.String)
     */
    @Override
    public CEntityFormatedVarReference NewEntityFormatedVarReference(CDataEntity object, String format)
    {
        // Direct backend CFPacJavaFormatedVarReference retired: the pure semantic
        // reference renders transparently through recursiveFPacFormatedVarReferenceEntity.
        // Its former value-parameterized ExportWriteAccessorTo protocol has no producer
        // or consumer in the parser/generator pipeline and is intentionally not retained
        // on the semantic value; writes belong to action entities.
        CEntityFormatedVarReference entity =
            new CEntityFormatedVarReference(object, programCatalog, format) ;
        return entity ;
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
        // Pure target-neutral semantic entity: the FPac conversion reference renders
        // through the recursive ST4 binding semantic.Verbs.CEntityConvertReference ->
        // recursiveFPacConvertReferenceEntity (REFERENCE role), reached via
        // LegacyDataRenderer.renderReference's reflective fallback. The conversion mode
        // and wrapped reference are populated by the parser via convertToPacked/
        // convertToAlphaNum; the entity's read-only getters drive the template. Replaced
        // the retired generate.fpacjava.CFPacJavaConvertReference direct backend. Mirrors
        // the CEntityArrayReference retirement (pure entity + legacy output binding).
        CEntityConvertReference e = new CEntityConvertReference(programCatalog) ;
        return e ;
    }

    /**
     * @see semantic.CBaseEntityFactory#NewEntityIsFileEOF(semantic.CEntityFileBuffer)
     */
    @Override
    public CEntityIsFileEOF NewEntityIsFileEOF(CEntityFileDescriptor fb)
    {
        CEntityIsFileEOF entity = new CEntityIsFileEOF(fb) ;
        return entity ;
    }

    /**
     * @see semantic.CBaseEntityFactory#NewEntityConstant(semantic.expression.CEntityConstant.Value)
     */
    @Override
    public CEntityConstant NewEntityConstant(Value val)
    {
        throw unsupportedOperation() ;
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
        throw unsupportedOperation() ;
    }

    /**
     * @see semantic.CBaseEntityFactory#NewEntityFileDescriptorLengthDependency(java.lang.String)
     */
    @Override
    public CEntityFileDescriptorLengthDependency NewEntityFileDescriptorLengthDependency(String name)
    {
        throw unsupportedOperation() ;
    }

    public CEntityAssignSpecial NewEntityAssignSpecial(int l)   {
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
        return e;
    }

    /**
     * @see semantic.CBaseEntityFactory#NewEntitySQLCall(int)
     */
    @Override
    public CEntitySQLCall NewEntitySQLCall(int line)
    {
        throw unsupportedOperation() ;
    }

}
