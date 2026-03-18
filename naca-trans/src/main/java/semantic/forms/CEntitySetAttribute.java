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

import parser.expression.CStringTerminal;
import parser.expression.CTerminal;
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
public abstract class CEntitySetAttribute extends CBaseActionEntity
{

	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntitySetAttribute(int line, CObjectCatalog cat, CBaseLanguageExporter out, CDataEntity field)
	{
		super(line, cat, out);
		refField = field ;
	}

	protected CDataEntity refField = null ;

	public void SetBright()
	{
		bBright = true ;		
	}
	protected boolean bBright = false ;

	public void SetModified()
	{
		bModified = true ;		
	}
	protected boolean bModified = false ; 

	public void SetNumeric()
	{
		bNumeric = true ; 		
	}
	protected boolean bNumeric = false ;

	public void SetProtected()
	{
		bProtected = true ;		
	}
	protected boolean bProtected = false ;

	public void SetUnprotected()
	{
		bUnProtected = true ;		
	}
	protected boolean bUnProtected = false ;

	public void SetAutoSkip()
	{
		bAutoSkip = true ;		
	}
	protected boolean bAutoSkip = false ;

	public void SetNormal()
	{
		bNormal = true ;		
	}
	protected boolean bNormal = false ;

	public void SetUnmodified()
	{
		bUnmodified = true;
	}
	protected boolean bUnmodified = false ;

	public void SetDark()
	{
		bDark = true ;
	}
	protected boolean bDark = false ;

	public void SetAttribute(CDataEntity entity)
	{
		attributeValue = entity ;		
	}
	protected CDataEntity attributeValue = null ; 
	public void Clear()
	{
		super.Clear();
		attributeValue = null ;
		refField = null ;
	}
	public boolean ignore()
	{
		if (refField == null || refField.ignore())
		{
			return true ;
		}
		else if (attributeValue != null && attributeValue.ignore())
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
		if (refField != null)
		{
			CTerminal term = new CStringTerminal(val) ;
			CBaseActionEntity act = CEntityFieldAttribute.intGetSpecialAssignment(refField, term, factory, getLine()) ;
			return act ;
		}
		return null;
	}
}
