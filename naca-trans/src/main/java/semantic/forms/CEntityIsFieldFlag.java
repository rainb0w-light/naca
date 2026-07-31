/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 19 ao�t 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.forms;

import parser.expression.CTerminal;
import semantic.CBaseEntityFactory;
import semantic.CDataEntity;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CUnitaryEntityCondition;


/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CEntityIsFieldFlag extends CUnitaryEntityCondition
{
	/*
	 * Pure read-only getters consumed by the recursive ST4 assembly contract
	 * (semantic.forms.CEntityIsFieldFlag -> recursiveIsFieldFlagEntity), preserved from
	 * the retired direct backend generate.java.forms.CJavaIsFieldFlag whose Export emitted
	 * one of four protected OnlineProgram condition calls selected by the isisSet and
	 * isopposite flags:
	 *   isFieldFlagSet(<reference>)      (isisSet && !isopposite)
	 *   isNotFieldFlagSet(<reference>)   (isisSet && isopposite)
	 *   isFieldFlag(<reference>, "v")    (!isisSet && !isopposite)
	 *   isNotFieldFlag(<reference>, "v") (!isisSet && isopposite)
	 * (contract operations bms.field.flag / bms.field.flag.not / bms.field.flag.set /
	 * bms.field.flag.setNot). getReference() exposes the inherited condition reference
	 * (SetIsFlag/SetIsFlagSet write this subclass's inherited slot); isSet()/isOpposite()
	 * expose the two branch flags; getValue() exposes the compared flag constant. No
	 * output protocol lives here: the getters only expose already-resolved state and the
	 * assembler renders the reference recursively. GetPriorityLevel/GetOppositeCondition
	 * move up from the retired backend; the opposite is a flag-flipped pure
	 * CEntityIsFieldFlag with var testing re-registered, exactly as the legacy backend did
	 * — no generate.* coupling in the semantic tree.
	 */
	public CDataEntity getReference()
	{
		return reference ;
	}

	public boolean isOpposite()
	{
		return isopposite ;
	}

	public boolean isSet()
	{
		return isisSet ;
	}

	public String getValue()
	{
		return value ;
	}

	public int GetPriorityLevel()
	{
		return 7;
	}

	public CBaseEntityCondition GetOppositeCondition()
	{
		CEntityIsFieldFlag not = new CEntityIsFieldFlag() ;
		not.reference = reference ;
		not.value = value ;
		not.isopposite = !isopposite;
		not.isisSet = isisSet;
		reference.RegisterVarTesting(not) ;
		return not;
	}

	public void SetIsFlag(CDataEntity eData, String cs)
	{
		value = cs ;
		isisSet = false ;
		reference = eData ;
	}
	protected String value = "" ;
	protected boolean isisSet = false ;
	//protected CDataEntity reference = null ; 
	protected boolean isopposite = false ;
	public void Clear()
	{
		super.Clear();
		reference = null ;
		isisSet = false ;
	}
	public CBaseEntityCondition getSimilarCondition(CBaseEntityFactory factory, CTerminal term)
	{
		if (term.IsReference())
		{
			ASSERT(null) ;
			return null ; 
		}
		else
		{
			CEntityIsFieldFlag eCond = factory.NewEntityIsFieldFlag();
			eCond.SetIsFlag(reference, term.GetValue());
			return eCond;
		}
	}
	public void SetOpposite()
	{
		isopposite = !isopposite;
	}
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
			field.UnRegisterVarTesting(this) ;
			var.RegisterVarTesting(this) ;
			reference = var ;
			return true ;
		}
		return false ;
	}
	/**
	 * @param refField
	 */
	public void SetIsFlagSet(CDataEntity refField)
	{
		value = "" ;
		isisSet = true ;
		reference = refField ;
	}
	public boolean isBinaryCondition()
	{
		return true;
	}

}	
