/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 11 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.forms;

import generate.CBaseLanguageExporter;

import java.util.Vector;

import parser.expression.CTerminal;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import semantic.CBaseEntityFactory;
import semantic.CSubStringAttributReference;
import semantic.Verbs.CEntityAssign;
import semantic.Verbs.CEntitySetConstant;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CEntityCondIsConstant;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityFieldData extends CBaseEntityFieldAttribute
{

	/**
	 * @param l
	 * @param name
	 * @param cat
	 * @param out
	 * @param type
	 * @param owner
	 */
	public CEntityFieldData(int l, String name, CObjectCatalog cat, CBaseLanguageExporter out, CDataEntity owner)
	{
		super(l, name, cat, out, CEntityFieldAttributeType.DATA, owner);
	}
	public CDataEntity GetArrayReference(Vector v, CBaseEntityFactory factory) 
	{
		CDataEntity e = reference.GetArrayReference(v, factory) ;
		return factory.NewEntityFieldData(getLine(), "", e);
	};
	public CDataEntity GetSubStringReference(CBaseEntityExpression start, CBaseEntityExpression length, CBaseEntityFactory factory) 
	{
		CSubStringAttributReference ref = factory.NewEntitySubString(getLine()) ;
		ref.SetReference(this, start, length) ;
		return ref ;
	};
	public CBaseEntityCondition GetSpecialCondition(int nLine, String value, CBaseEntityCondition.EConditionType type, CBaseEntityFactory factory)
	{
		CEntityCondIsConstant eCond = factory.NewEntityCondIsConstant() ;
		if (value.equals("ZERO") || value.equals("ZEROS") || value.equals("ZEROES"))
		{
			eCond.SetIsZero(reference);
		}
		else if (value.equals("SPACE") || value.equals("SPACES"))
		{
			eCond.SetIsSpace(reference);
		}
		else if (value.equals("LOW-VALUE") || value.equals("LOW-VALUES"))
		{
			eCond.SetIsLowValue(reference);
		}
		else if (value.equals("HIGH-VALUE") || value.equals("HIGH-VALUES"))
		{
			eCond.SetIsHighValue(reference);
		}
		else
		{
			return null ;
		}
		if (type == CBaseEntityCondition.EConditionType.IS_DIFFERENT)
		{
			eCond.SetOpposite();
			return eCond ;
		}
		else if (type == CBaseEntityCondition.EConditionType.IS_EQUAL)
		{
			return eCond ;
		}
		else
		{
			return null ;
		}
	}
	public CBaseActionEntity GetSpecialAssignment(CDataEntity term, CBaseEntityFactory factory, int l)
	{
		CEntityAssign eAssign = factory.NewEntityAssign(l) ;
		eAssign.SetValue(term);
		eAssign.AddRefTo(reference) ;
		reference.RegisterWritingAction(eAssign) ;
		return eAssign ;
	}
	public CBaseActionEntity GetSpecialAssignment(CTerminal term, CBaseEntityFactory factory, int l)
	{
		String value = term.GetValue() ;
		CEntitySetConstant eAssign = factory.NewEntitySetConstant(l) ;
		if (value.equals("ZERO") || value.equals("ZEROS") || value.equals("ZEROES"))
		{
			eAssign.SetToZero(reference) ;
		}
		else if (value.equals("SPACE") || value.equals("SPACES"))
		{
			eAssign.SetToSpace(reference) ;
		}
		else if (value.equals("LOW-VALUE") || value.equals("LOW-VALUES"))
		{	
			eAssign.SetToLowValue(reference) ;
		}
		else
		{
			return null ;
		}
		reference.RegisterWritingAction(eAssign) ;
		return eAssign ;
	}
	public boolean ignore()
	{
		return false ;
	}
}
