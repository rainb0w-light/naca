/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;

import generate.CBaseLanguageExporter;
import semantic.CBaseDataReference;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author S. Charton
 * @version $Id: CEntityConvertReference.java,v 1.2 2006/09/28 09:10:06 u930di Exp $
 */
public abstract class CEntityConvertReference extends CBaseDataReference
{

	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntityConvertReference(CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(0, "", cat, out);
	}

	/**
	 * @see semantic.CBaseLanguageEntity#DoExport()
	 */
	@Override
	protected void DoExport()
	{
		// unused
	}

	/**
	 * @see semantic.CDataEntity#GetDataType()
	 */
	@Override
	public CDataEntityType GetDataType()
	{
		return reference.GetDataType() ;
	}
	

	/**
	 * @see semantic.CDataEntity#isValNeeded()
	 */
	@Override
	public boolean isValNeeded()
	{
		return false;
	}

	/**
	 * @see semantic.CDataEntity#GetConstantValue()
	 */
	@Override
	public String GetConstantValue()
	{
		return null;
	}

	/**
	 * @param buffer
	 */
	public void convertToPacked(CDataEntity buffer)
	{
		isconvertToPacked = true ;
		isconvertToAlphaNum = false ;
		reference = buffer ;
	}	
	protected boolean isconvertToPacked = false ;
	protected boolean isconvertToAlphaNum = false ;
	
	public void convertToAlphaNum(CDataEntity working) {
		isconvertToAlphaNum = true ;
		isconvertToPacked = false ;
		reference = working ;
	}
}
