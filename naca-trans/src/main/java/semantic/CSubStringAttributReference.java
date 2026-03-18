/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 6 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic;

import generate.*;
import lexer.Cobol.CCobolConstantList;
import parser.expression.CTerminal;
import semantic.Verbs.*;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CBaseEntityExpression;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CSubStringAttributReference extends CBaseDataReference
{

	/**
	 * @param l
	 * @param name
	 * @param cat
	 * @param out
	 */
	public CSubStringAttributReference(int l, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(l, "", cat, out);
	}
	public void SetReference(CDataEntity ref, CBaseEntityExpression start, CBaseEntityExpression length)
	{
		reference = ref ;
		start = start ;
		length = length;
	}
	
	protected CBaseEntityExpression start = null ;
	protected CBaseEntityExpression length = null ;

	/* (non-Javadoc)
	 * @see semantic.CBaseDataEntity#GetSpecialAssignment(parser.expression.CTerminal)
	 */
	public CBaseActionEntity GetSpecialAssignment(CTerminal term, CBaseEntityFactory factory, int l)
	{
		String value = term.GetValue() ;
		CEntitySetConstant eAssign = factory.NewEntitySetConstant(l) ;
		eAssign.SetSubStringRef(start, length);
		if (value.equals(CCobolConstantList.ZERO.name) || value.equals(CCobolConstantList.ZEROS.name) || value.equals(CCobolConstantList.ZEROES.name))
		{
			eAssign.SetToZero(reference) ;
		}
		else if (value.equals(CCobolConstantList.SPACE.name) || value.equals(CCobolConstantList.SPACES.name))
		{
			eAssign.SetToSpace(reference) ;
		}
		else if (value.equals(CCobolConstantList.LOW_VALUE.name) || value.equals(CCobolConstantList.LOW_VALUES.name))
		{
			eAssign.SetToLowValue(reference) ;
		}
		else if (value.equals(CCobolConstantList.HIGH_VALUE.name) || value.equals(CCobolConstantList.HIGH_VALUES.name))
		{
			eAssign.SetToHighValue(reference) ;
		}
		else
		{
			return null ;
		}
		reference.RegisterWritingAction(eAssign) ;
		//RegisterWritingAction(eAssign) ;
		return eAssign ;
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseDataEntity#GetSpecialAssignment(semantic.CBaseDataEntity)
	 */
	public CBaseActionEntity GetSpecialAssignment(CDataEntity term, CBaseEntityFactory factory, int l)
	{
		return null;
	}
	public CBaseEntityCondition GetSpecialCondition(int nLine, String value, CBaseEntityCondition.EConditionType type, CBaseEntityFactory factory)
	{
		CBaseEntityCondition eCond = reference.GetSpecialCondition(getLine(), value, type, factory);
		if (eCond == null)
		{
			return null ;
		}
		else
		{
			CDataEntity eData = eCond.GetConditionReference() ;
			CSubStringAttributReference eSubStr = factory.NewEntitySubString(getLine()) ;
			eSubStr.length = length ;
			eSubStr.start = start ;
			eSubStr.reference = eData ;
			eCond.SetConditonReference(eSubStr);
			eSubStr.RegisterVarTesting(eCond) ;
			return eCond;
		}
	}
	public boolean ignore()
	{
		if (reference == null)
			return true ;
		return reference.ignore() ;
	}
	public String GetConstantValue()
	{
		return "" ;
	} 	 
	public void Clear()
	{
		super.Clear();
		length.Clear();
		length = null ;
		start.Clear() ;
		start = null ;
	}
	
}
