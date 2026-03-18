/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Sep 29, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.expression;

import generate.CBaseLanguageExporter;
import semantic.CBaseDataReference;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CBaseEntityFunction extends CBaseDataReference
{
	//protected CDataEntity dataRef = null ;
	public void Clear()
	{
		super.Clear() ;
		reference = null ;
	}
	
	public CBaseEntityFunction(CObjectCatalog cat, CBaseLanguageExporter out, CDataEntity data)
	{
		super(0, "", cat, out);
		reference = data ;
	}
	public CDataEntityType GetDataType()
	{
		if (reference != null)
		{
			return reference.GetDataType();
		}
		else
		{
			return null ;
		}
	}
	/* (non-Javadoc)
	 * @see semantic.CBaseDataEntity#HasAccessors()
	 */
	public boolean HasAccessors()
	{
		return false;
	}
	public String ExportWriteAccessorTo(String value)
	{
		// nothing
		return "" ;
	}
	protected void DoExport()
	{
		// nothing
	}
	public boolean ignore()
	{
		if (reference != null)
		{
			return reference.ignore() ;
		}
		else
		{
			return false ;
		}
	}
	public String GetConstantValue()
	{
		return "" ;
	} 	 
}
