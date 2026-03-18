/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 20 août 04
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.SQL;
import generate.CBaseLanguageExporter;

import java.util.ArrayList;

import parser.Cobol.elements.SQL.CSQLTableColDescriptor;

import semantic.CBaseActionEntity;
import utils.CObjectCatalog;

/**
 * @author U930DI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public abstract class CEntitySQLDeclareTable extends CBaseActionEntity
{
	public CEntitySQLDeclareTable(int line, CObjectCatalog cat, CBaseLanguageExporter out, String csTableName, String csViewName, ArrayList arrTableColDescription)
	{
		super(line, cat, out);
		csViewName = csViewName ;
		csTableName = csTableName;
		if (csTableName == null)
		{
			int n=0; 
		}
		arrTableColDescription = arrTableColDescription;
		programCatalog.RegisterSQLTable(csViewName, this);
	}
	protected String csTableName = "";
	protected String csViewName = "" ;
	protected ArrayList arrTableColDescription = null;
	public void Clear()
	{
		super.Clear();
		arrTableColDescription.clear() ;
	}
	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#RegisterMySelfToCatalog()
	 */

	public String ExportColReferences()
	{
		String out = "" ;
		for (int i=0; i<arrTableColDescription.size();i++)
		{
			CSQLTableColDescriptor desc = (CSQLTableColDescriptor)arrTableColDescription.get(i);
			if (!out.equals(""))
			{
				out += ", " ;
			}
			out += desc.GetName();
		}
		return out;
	}
	public String ExportColReferences(String alias)
	{
		String out = "" ;
		for (int i=0; i<arrTableColDescription.size();i++)
		{
			CSQLTableColDescriptor desc = (CSQLTableColDescriptor)arrTableColDescription.get(i);
			if (!out.equals(""))
			{
				out += ", " ;
			}
			out += alias+"."+desc.GetName();
		}
		return out;
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#GetName()
	 */
	public String GetTableName()
	{
		return csTableName ; 
	}

	public int GetNbCols()
	{
		return arrTableColDescription.size();
	}
	public boolean ignore()
	{
		return false ;
	}
	/**
	 * @return
	 */
	public String GetViewName()
	{
		return csViewName ;
	}
	public String GetName()
	{
		return csViewName ;
	}

}
