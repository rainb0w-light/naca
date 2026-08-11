/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.forms;

import java.util.Vector;

import lexer.Cobol.CCobolConstantList;
import parser.expression.CTerminal;
import semantic.*;
import semantic.expression.CBaseEntityCondition;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 */
public class CEntityFieldHighlight extends CBaseEntityFieldAttribute
{
	public CEntityFieldHighlight(int l, String name, CObjectCatalog cat, CDataEntity owner)
	{
		super(l, name, cat, CEntityFieldAttributeType.HIGHLIGHT, owner) ;
	}
	/*
	 * Semantic predicates preserved from the retired direct backend
	 * (generate.java.forms.CJavaFieldHighligh): pure read-only getters, consumed
	 * by the recursive ST4 assembly contract and the BMS traversal; no output
	 * protocol lives here.
	 */
	/* (non-Javadoc)
	 * @see semantic.CDataEntity#GetDataType()
	 */
	public CDataEntityType GetDataType()
	{
		return CDataEntityType.FIELD ;
	}
	public boolean HasAccessors()
	{
		return true ;
	}
	public boolean isValNeeded()
	{
		return false ;
	}
	public CBaseActionEntity GetSpecialAssignment(CTerminal term, CBaseEntityFactory factory, int l)
	{
		return intGetSpecialAssignment(term.GetValue(), reference, factory, l) ;
	}
	/* (non-Javadoc)
	 * @see semantic.CBaseDataEntity#GetSpecialAssignment(parser.expression.CTerminal)
	 */
	public static CBaseActionEntity intGetSpecialAssignment(String v, CDataEntity eField, CBaseEntityFactory factory, int l)
	{
		CEntityFieldAttributeReference ref = factory.NewEntityFieldAttributeReference(eField) ;
		CEntitySetHighligh eSet = factory.NewEntitySetHighlight(l, ref);
		if (v.equals(CCobolConstantList.HIGH_VALUE.name) || v.equals(CCobolConstantList.HIGH_VALUES.name) || v.equals("\u00FF") || v.equals("\u009F"))
		{
			eSet.SetNormal();
		}
		else if (v.equals(CCobolConstantList.LOW_VALUE.name) || v.equals(CCobolConstantList.LOW_VALUES.name))
		{
			eSet.Reset() ;
		}
		else if (v.equals("0"))
		{
			eSet.SetNormal();
		}
		else if (v.equals("1"))
		{
			eSet.SetBlink();
		}
		else if (v.equals("2"))
		{
			eSet.SetReverse();
		}
		else if (v.equals("4"))
		{
			eSet.SetUnderlined();
		}
		else if (v.equals("6"))
		{
			eSet.SetReverse();
			eSet.SetUnderlined();
		}
		else
		{
			return null ;
		}
		ref.RegisterWritingAction(eSet) ;
		return eSet;
	}
	public CBaseActionEntity GetSpecialAssignment(CDataEntity val, CBaseEntityFactory factory, int l)
	{
		CEntityFieldAttributeReference ref = factory.NewEntityFieldAttributeReference(reference) ;
		CEntitySetHighligh eSet = factory.NewEntitySetHighlight(l, ref);
		eSet.SetHighLight(val);
		ref.RegisterWritingAction(eSet) ;
		return eSet;
	}
	public CBaseEntityCondition GetSpecialCondition(int nLine, String value, CBaseEntityCondition.EConditionType type, CBaseEntityFactory factory)
	{
		CEntityIsFieldHighlight eCond  ;
		if (value.equals("4"))
		{
			eCond = factory.NewEntityIsFieldHighlight(reference) ;
			eCond.IsUnderlined();
			reference.RegisterVarTesting(eCond);
		}
		else if (value.equals("2"))
		{
			eCond = factory.NewEntityIsFieldHighlight(reference) ;
			eCond.IsReverse();
			reference.RegisterVarTesting(eCond);
		}
		else if (value.equals("1"))
		{
			eCond = factory.NewEntityIsFieldHighlight(reference) ;
			eCond.IsBlink() ;
			reference.RegisterVarTesting(eCond);
		}
		else if (value.equals("HIGH-VALUE") || value.equals("HIGH-VALUES") || value.equals("\u009F"))
		{
			eCond = factory.NewEntityIsFieldHighlight(reference) ;
			eCond.IsNormal() ;
			reference.RegisterVarTesting(eCond);
		}
		else
		{
			return null ;
		}
		if (type == CBaseEntityCondition.EConditionType.IS_DIFFERENT)
		{
			eCond.setOpposite() ;
		}
		return eCond ;
	}
	public CDataEntity GetArrayReference(Vector v, CBaseEntityFactory factory)
	{
		CDataEntity e = reference.GetArrayReference(v, factory) ;
		return factory.NewEntityFieldHighlight(getLine(), "", e);
	};
	public boolean ignore()
	{
		return false ;
	}
}
