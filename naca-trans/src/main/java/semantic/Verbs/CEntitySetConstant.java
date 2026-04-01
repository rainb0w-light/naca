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
package semantic.Verbs;

import generate.CBaseLanguageExporter;
import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import semantic.expression.CBaseEntityExpression;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntitySetConstant extends CBaseActionEntity
{

	/* (non-Javadoc)
	 * @see semantic.CBaseActionEntity#getValueAssigned()
	 */
	@Override
	public CDataEntity getValueAssigned()
	{
		return csteValue ;
	}

	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntitySetConstant(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}

	public void SetToZero(CDataEntity var)
	{
		variable = var ;
		if (var == null)
		{
			int n=0 ;
		}
		issetToZero = true ;
		issetToSpace = false ;
		issetToLowValue = false ;
		issetToHighValue = false ;
	}
	public void SetToSpace(CDataEntity var)
	{
		if (var == null)
		{
			int n=0 ;
		}
		variable = var ;
		issetToZero = false ;
		issetToSpace = true ;
		issetToLowValue = false ;
		issetToHighValue = false ;
	}
	public void SetToHighValue(CDataEntity var)
	{
		if (var == null)
		{
			int n=0 ;
		}
		variable = var ;
		issetToZero = false ;
		issetToSpace = false ;
		issetToLowValue = false ;
		issetToHighValue = true ;
	}
	public void SetToLowValue(CDataEntity var)
	{
		if (var == null)
		{
			int n=0 ;
		}
		variable = var ;
		issetToZero = false ;
		issetToSpace = false ;
		issetToHighValue = false ;
		issetToLowValue = true ;
	}
	public void SetCsteValue(CDataEntity var, CDataEntity val)
	{
		if (var == null)
		{
			int n=0 ;
		}
		variable = var ;
		issetToZero = false ;
		issetToSpace = false ;
		issetToLowValue = false ;
		issetToHighValue = false ;
		csteValue = val ;
	}

	public void SetSubStringRef(CBaseEntityExpression s, CBaseEntityExpression l)
	{
		subStringRefStart = s ;
		subStringRefLength = l ;
	}
	public void SetCondition(CDataEntity cond, boolean bCond)
	{
		if (cond == null)
		{
			int n=0 ;
		}
		variable = cond ;
		issetToZero = false ;
		issetToSpace = false ;
		issetToLowValue = false ;
		issetToHighValue = false ;
		csteValue = null ;
		issetToTrue = bCond ;
		issetToFalse = !bCond ;
	}
	
	protected CDataEntity variable = null ;
	protected boolean issetToZero = false ;
	protected boolean issetToSpace = false ;
	protected boolean issetToLowValue = false ;
	protected boolean issetToHighValue = false ;
	protected boolean issetToTrue = false ;
	protected boolean issetToFalse = false ;

	protected CDataEntity csteValue = null ; 
	protected CBaseEntityExpression subStringRefStart = null ;
	protected CBaseEntityExpression subStringRefLength = null ;
	public void Clear()
	{
		super.Clear() ;
		csteValue = null ;
		variable =  null ;
		if (subStringRefLength!=null)
		{
			subStringRefLength.Clear() ;
			subStringRefLength = null ;
		}
		if (subStringRefStart!=null)
		{
			subStringRefStart.Clear() ;
			subStringRefStart = null ;
		}
	}
	public boolean ignore()
	{
		return variable == null ;
	}
	public boolean IgnoreVariable(CDataEntity data)
	{
		if (data == variable)
		{
			variable = null ;
			data.UnRegisterWritingAction(this) ;
			return true ;
		}
		return false  ;
	}
	public boolean ReplaceVariable(CDataEntity var, CDataEntity by)
	{
		if (variable == var)
		{
			variable = by ;
			var.UnRegisterWritingAction(this);
			by.RegisterWritingAction(this) ;
			return true ;
		}
		return false ;
	}
}
