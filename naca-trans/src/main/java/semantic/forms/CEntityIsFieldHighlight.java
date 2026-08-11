/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.forms;

import semantic.CBaseEntityFactory;
import semantic.CDataEntity;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CUnitaryEntityCondition;

/**
 * @author sly
 *
 */
public class CEntityIsFieldHighlight extends CUnitaryEntityCondition
{

	public CEntityIsFieldHighlight(CDataEntity ref)
	{
		reference = ref ;
	}

	public void IsBlink()
	{
		isisBlink = true ;
	}
	public void IsReverse()
	{
		isisReverse = true ;
	}
	public void IsUnderlined()
	{
		isisUnderlined = true ;
	}
	//protected CFieldHighligh m_highlight = null ;
	protected boolean isisBlink = false ;
	protected boolean isisReverse = false ;
	protected boolean isisUnderlined = false ;
	protected boolean isopposite = false ;

	public boolean ignore()
	{
		return reference.ignore() ;
	}
	public CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace)
	{
		return reference.GetSpecialCondition(getLine(), val, EConditionType.IS_EQUAL, fact);
	}
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		if (reference == field)
		{
			reference = var ;
			field.UnRegisterVarTesting(this) ;
			var.RegisterVarTesting(this) ;
			return true ;
		}
		return false ;
	}

	/**
	 *
	 */
	public void IsNormal()
	{
		isisBlink = false ;
		isisReverse = false ;
		isisUnderlined = false ;
	}
	public boolean isBinaryCondition()
	{
		return true;
	}

	public void setOpposite()
	{
		isopposite = !isopposite;
	}

	/*
	 * Pure read-only getters consumed by the recursive ST4 assembly contract
	 * (semantic.forms.CEntityIsFieldHighlight -> recursiveIsFieldHighlightEntity),
	 * preserved from the retired direct backend
	 * generate.java.forms.CJavaIsFieldHighlight whose Export built "is" + ("Not" when
	 * isopposite) + one of "FieldUnderlined" / "FieldBlink" / "FieldReverse" /
	 * "FieldHighlightNormal" (that precedence order) + "(<reference>)": the protected
	 * OnlineProgram condition calls isFieldUnderlined(Edit), isFieldBlink(Edit),
	 * isFieldReverse(Edit) and isFieldHighlightNormal(Edit) and their isNot* opposites
	 * (contract operations bms.field.highlight.*). IF <FIELD>H = <code> keeps the
	 * positive flag; IF <FIELD>H <> <code> lowers through setOpposite (negation is
	 * carried by this entity's own isopposite flag, and GetOppositeCondition flips it,
	 * rather than a wrapping CEntityCondNot), so isOpposite() exposes the flag and the
	 * template branches on it to select the positive/negated runtime call. One legacy
	 * output is NOT reproduced byte-for-byte: the retired backend's blink + opposite
	 * branch emitted isNotFieldBlink(<reference>), a call with no naca-rt implementation
	 * (OnlineProgram declares isFieldBlink but no isNotFieldBlink) — invalid generated
	 * Java on a production-reachable branch (IF <FIELD>H <> 1 lowers through
	 * CEntityFieldHighlight.GetSpecialCondition("1", IS_DIFFERENT) -> IsBlink +
	 * setOpposite). The template instead emits the Java negation !isFieldBlink(<ref>)
	 * of the same contracted call: semantically identical (OnlineProgram's own
	 * isNotField* helpers are implemented as exactly such a negation) and compilable,
	 * as the contract gate requires for every reachable template branch. isBlink() /
	 * isReverse() / isUnderlined() expose the three mutually-exclusive mode flags set
	 * by IsBlink/IsReverse/IsUnderlined (IsNormal clears all three); getReference()
	 * only exposes the already-resolved inherited condition reference and the assembler
	 * renders it recursively. Every getter is a pure field read — no FormatIdentifier,
	 * no data-reference resolution, no lowering. No output protocol lives here.
	 * GetPriorityLevel/GetOppositeCondition move up from the retired backend; the
	 * opposite is a flag-flipped CEntityIsFieldHighlight (rendered through the same
	 * binding) with var testing re-registered, exactly as the legacy backend did — no
	 * generate.* coupling in the semantic tree.
	 */
	public CDataEntity getReference()
	{
		return reference ;
	}

	public boolean isOpposite()
	{
		return isopposite ;
	}

	public boolean isBlink()
	{
		return isisBlink ;
	}

	public boolean isReverse()
	{
		return isisReverse ;
	}

	public boolean isUnderlined()
	{
		return isisUnderlined ;
	}

	public int GetPriorityLevel()
	{
		return 7;
	}

	public CBaseEntityCondition GetOppositeCondition()
	{
		CEntityIsFieldHighlight not = new CEntityIsFieldHighlight(reference) ;
		not.isisBlink = isisBlink;
		not.isisReverse = isisReverse;
		not.isisUnderlined = isisUnderlined;
		not.isopposite = !isopposite;
		reference.RegisterVarTesting(not) ;
		return not;
	}
}
