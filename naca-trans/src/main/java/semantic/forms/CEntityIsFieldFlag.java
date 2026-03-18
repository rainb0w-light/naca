/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 19 août 2004
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
public abstract class CEntityIsFieldFlag extends CUnitaryEntityCondition
{
	public void SetIsFlag(CDataEntity eData, String cs)
	{
		value = cs ;
		bIsSet = false ;
		reference = eData ;
	}
	protected String value = "" ;
	protected boolean bIsSet = false ;
	//protected CDataEntity reference = null ; 
	protected boolean bOpposite = false ;
	public void Clear()
	{
		super.Clear();
		reference = null ;
		bIsSet = false ;
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
		bOpposite = !bOpposite ;		
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
		bIsSet = true ;
		reference = refField ;
	}
	public boolean isBinaryCondition()
	{
		return true;
	}

}	
