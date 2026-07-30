/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 11 ao�t 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.forms;

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
public class CEntitySetHighligh extends CBaseActionEntity
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
	 */
	public CEntitySetHighligh(int line, CObjectCatalog cat, CDataEntity field)
	{
		super(line, cat);
		refField = field ;
	}

	/*
	 * Pure read-only getters consumed by the recursive ST4 assembly contract
	 * (semantic.forms.CEntitySetHighligh -> recursiveSetHighlightEntity), preserved
	 * from the retired direct backend generate.java.forms.CJavaSetHighlight whose
	 * DoExport selected the emitted runtime call(s) from exactly these slots, in
	 * this order: isisBlink -> setFieldBlink(<field>), isisReverse ->
	 * setFieldReverse(<field>), isisUnderlined -> setFieldUnderline(<field>),
	 * isisNormal -> setFieldUnhighlighted(<field>), a non-null highLightValue ->
	 * moveHighLighting(<value>, <field>), and when no slot is set a reset ->
	 * setFieldUnhighlighted(<field>). The legacy reset branch named a
	 * resetFieldHighlighting(<field>) call that has no naca-rt signature (it never
	 * compiled); the reset slot maps to the real OnlineProgram.setFieldUnhighlighted
	 * (highlighting OFF), the semantic equivalent. The flags are independent (the
	 * "6" highlight sets both reverse and underline), so more than one getter may be
	 * true at once. No output protocol lives here; the getters only expose the
	 * already-resolved semantic state and the assembler renders the child data
	 * references recursively.
	 */
	public CDataEntity getField()
	{
		return refField ;
	}
	public CDataEntity getHighLightValue()
	{
		return highLightValue ;
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
	public boolean isNormal()
	{
		return isisNormal ;
	}
	public boolean isReset()
	{
		return !isisBlink && !isisNormal && !isisUnderlined && !isisReverse
			&& highLightValue == null ;
	}

	public void SetBlink()
	{
		isisBlink = true ;
	}
	public void SetReverse()
	{
		isisReverse = true ;
	}
	public void SetUnderlined()
	{
		isisUnderlined = true ;
	}
	//protected CFieldHighligh m_highlight = null ;
	protected boolean isisBlink = false ;
	protected boolean isisReverse = false ;
	protected boolean isisUnderlined = false ;
	protected boolean isisNormal = false ;
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
		isisNormal = true ;
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
		isisBlink = false ;
		isisNormal = false ;
		isisReverse = false ;
		isisUnderlined = false ;
		highLightValue = null ;		
	}
}
