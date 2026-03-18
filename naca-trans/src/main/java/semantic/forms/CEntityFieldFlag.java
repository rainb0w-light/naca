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

import generate.*;

import java.util.Vector;

import lexer.Cobol.CCobolConstantList;
import parser.expression.CTerminal;
import semantic.*;
import semantic.expression.CBaseEntityCondition;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityFieldFlag extends CBaseEntityFieldAttribute
{

	/**
	 * @param l
	 * @param name
	 * @param cat
	 * @param out
	 * @param type
	 * @param owner
	 */
	public CEntityFieldFlag(int l, String name, CObjectCatalog cat, CBaseLanguageExporter out, CDataEntity owner)
	{
		super(l, name, cat, out, CEntityFieldAttributeType.FLAG, owner);
	}
	public CBaseActionEntity GetSpecialAssignment(CTerminal term, CBaseEntityFactory factory, int l)
	{
		CEntityFieldAttributeReference ref = factory.NewEntityFieldAttributeReference(reference) ;
		CEntitySetFlag eSet = factory.NewEntitySetFlag(l, ref) ;
		String v = term.GetValue() ;
		if (v.equals(CCobolConstantList.LOW_VALUE.name) || v.equals(CCobolConstantList.LOW_VALUES.name))
		{
			eSet.ResetFlag() ;
		}
		else if (v.equals("1"))
		{
			eSet.SetFlag("1") ;
		}
		else if (v.equals("0"))
		{
			eSet.SetFlag("0") ;
		}
		else if (v.equals(CCobolConstantList.SPACE.name) || v.equals(CCobolConstantList.SPACES.name))
		{
			eSet.SetFlag("0") ;
		}
		else if (v.equals(CCobolConstantList.ZERO.name) || v.equals(CCobolConstantList.ZEROS.name) || v.equals(CCobolConstantList.ZEROES.name))
		{
			eSet.SetFlag("0") ;
		}
		else
		{
			return null ;
		}
		ref.RegisterWritingAction(eSet) ;
		return eSet;
	}
	public CDataEntity GetArrayReference(Vector v, CBaseEntityFactory factory) 
	{
		CDataEntity e = reference.GetArrayReference(v, factory) ;
		return factory.NewEntityFieldFlag(getLine(), "", e);
	};
	public CBaseEntityCondition GetSpecialCondition(int nLine, String value, CBaseEntityCondition.EConditionType type, CBaseEntityFactory factory)
	{
		CEntityIsFieldFlag eCond = factory.NewEntityIsFieldFlag() ;
		if (value.equals("1"))
		{
			eCond.SetIsFlag(reference, "1");
		}
		else if (value.equals("LOW-VALUE") || value.equals("LOW-VALUES"))
		{
			eCond.SetIsFlagSet(reference);
			eCond.SetOpposite() ;	 // if FIELD.Flag == LOW-VALUE  <=>  if flag not set
		}
		else if (value.equals("0")|| value.equals("ZERO") || value.equals("ZEROS") || value.equals("ZEROES"))
		{
			eCond.SetIsFlag(reference, "0");
		}
		else
		{
			return null ;
		}
		reference.RegisterVarTesting(eCond) ;
		if (type == CBaseEntityCondition.EConditionType.IS_EQUAL)
		{
			return eCond ;
		}
		else if (type == CBaseEntityCondition.EConditionType.IS_DIFFERENT)
		{
			eCond.SetOpposite() ;
			return eCond ;			
		}
		else
		{
			return null ;
		}
	}
}
