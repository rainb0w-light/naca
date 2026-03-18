/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 22 oct. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.forms;

import generate.CBaseLanguageExporter;


import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityFieldOccurs extends CEntityResourceField
{
	protected CDataEntity occurs = null ;
	protected String csLevel = "" ;
	public void Clear()
	{
		super.Clear() ;
		occurs = null ;
	}
	
	public CEntityFieldOccurs(int l, String name, CObjectCatalog cat, CBaseLanguageExporter lexp)
	{
		super(l, name, cat, lexp);
		if (name.equals(""))
		{
			name = GetDefaultName() ;
			if (!name.equals(""))
			{
				SetName(name) ;
			}
		}
	}
	public void SetFieldOccurs(String level, CDataEntity occurs)
	{
		occurs = occurs ;
		csLevel = level ;
	}

	public boolean IsEntryField()
	{
		return false;
	}

	public String GetTypeDecl()
	{
		return null;
	}

	public CDataEntityType GetDataType()
	{
		return CDataEntityType.FIELD;
	}
	protected void RegisterMySelfToCatalog()
	{
		String name = GetName() ;
		programCatalog.RegisterDataEntity(name, this) ;
//		programCatalog.RegisterDataEntity(name+"I", this) ;
//		programCatalog.RegisterDataEntity(name+"O", this) ;
	}
}
