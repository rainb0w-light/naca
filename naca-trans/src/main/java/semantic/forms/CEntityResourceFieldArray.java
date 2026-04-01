/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Jan 10, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.forms;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import generate.CBaseLanguageExporter;
import semantic.CBaseEntityFactory;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityResourceFieldArray extends CEntityResourceField
{

	/**
	 * @param l
	 * @param name
	 * @param cat
	 * @param lexp
	 */
	public CEntityResourceFieldArray(int l, String name, CObjectCatalog cat, CBaseLanguageExporter lexp)
	{
		super(l, name, cat, lexp);
	}

	public CDataEntityType GetDataType()
	{
		return CDataEntityType.FIELD ;
	}
	public boolean IsEntryField()
	{
		return false;
	}

	public void SetArray(int nbItems, int NbCol, boolean bVerticalFilling)
	{
		nbItems = nbItems ;
		nbColumns = NbCol ;
		bVerticalFilling = bVerticalFilling ;
	}
	
	protected int nbItems = 0 ;
	protected int nbColumns = 0 ;
	protected boolean isverticalFilling = false ;

	public void SetPosition(int Line, int Col)
	{
		nPosCol = Col ;
		nPosLine = Line ;		
	} 
	public void InitDependences(CBaseEntityFactory factory)
	{
		ListIterator iter = lstChildren.listIterator() ;
		try
		{
			CEntityResourceField field = (CEntityResourceField)iter.next() ;
			while (field != null)
			{
				field.InitDependences(factory) ;
				field = (CEntityResourceField)iter.next() ;
			}
		}
		catch (NoSuchElementException e)
		{
		}
	} 
}
