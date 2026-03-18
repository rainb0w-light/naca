/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Oct 12, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.SQL;

import generate.CBaseLanguageExporter;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntitySQLCursor extends CDataEntity
{
	/**
	 * @param l
	 * @param name
	 * @param cat
	 * @param out
	 */
	public CEntitySQLCursor(String name, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(0, name, cat, out);
		programCatalog.RegisterSQLCursor(this);
		if (!name.equals(this.GetName()))
		{
			programCatalog.RegisterSQLCursor(name, this);
		}
	}

	public boolean HasAccessors()
	{
		return false;
	}
	public String ExportWriteAccessorTo(String value)
	{
		return "" ;
	}

	public void SetSelect(CEntitySQLCursorSelectStatement eSQL)
	{
		select = eSQL ;
	}
	
	protected CEntitySQLCursorSelectStatement select = null ;
	protected CDataEntity variableStatement = null ;

	public int GetNbColumns()
	{
		if (select == null)
			return 0 ;
		return select.GetNbColumns() ;
	}
	public boolean ignore()
	{
		return false ;
	}
	public String GetConstantValue()
	{
		return "" ;
	}

	/**
	 * 
	 */
	public CEntitySQLCursorSelectStatement getSelect()
	{
		return select ;
	}

	/**
	 * @param var
	 */
	public void setVariableStatement(CDataEntity var)
	{
		variableStatement = var ;
	}
	public CDataEntity getVariableStatement()
	{
		return variableStatement ;
	}

}
