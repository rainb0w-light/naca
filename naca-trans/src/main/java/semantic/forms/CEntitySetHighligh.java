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
import semantic.CBaseActionEntity;
import semantic.CBaseEntityFactory;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntitySetHighligh extends CBaseActionEntity
{

//	public static class CFieldHighligh
//	{
//		protected CFieldHighligh(String s)
//		{
//			text = s ;
//		}
//		public String text = "" ;
//		public static CFieldHighligh NORMAL = new CFieldHighligh("Normal");
//	} 
	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntitySetHighligh(int line, CObjectCatalog cat, CBaseLanguageExporter out, CDataEntity field)
	{
		super(line, cat, out);
		refField = field ;
	}
	
	public void SetBlink()
	{
		bIsBlink = true ;
	}
	public void SetReverse()
	{
		bIsReverse = true ;
	}
	public void SetUnderlined()
	{
		bIsUnderlined = true ;
	}
	//protected CFieldHighligh m_highlight = null ;
	protected boolean bIsBlink = false ;
	protected boolean bIsReverse = false ;
	protected boolean bIsUnderlined = false ;
	protected boolean bIsNormal = false ;
	protected CDataEntity refField = null ;
	protected CDataEntity highLightValue = null ;
	public void Clear()
	{
		super.Clear();
		refField = null ;
		highLightValue = null ;
	}

	public void SetHighLight(CDataEntity entity)
	{
		highLightValue = entity ;		
	}

	public void SetNormal()
	{
		bIsNormal = true ;		
	}
	public boolean ignore()
	{
		if (refField == null || refField.ignore())
		{
			return true ;
		}
		else if (highLightValue != null && highLightValue.ignore())
		{
			return true ;
		}
		return false ;
	}
	public boolean IgnoreVariable(CDataEntity data)
	{
		if (data == refField)
		{
			refField = null ;
			data.UnRegisterWritingAction(this) ;
			return true ;
		}
		return false ;
	}
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		if (refField == field)
		{
			refField = var ;
			field.UnRegisterWritingAction(this) ;
			var.RegisterWritingAction(this) ;
			return true ;
		}
		return false ;
	}
	public CBaseActionEntity GetSpecialAssignement(String val, CBaseEntityFactory factory)
	{
		return CEntityFieldHighlight.intGetSpecialAssignment(val, refField, factory, getLine());
	}

	/**
	 * 
	 */
	public void Reset()
	{
		bIsBlink = false ;
		bIsNormal = false ;
		bIsReverse = false ;
		bIsUnderlined = false ;
		highLightValue = null ;		
	}
}
