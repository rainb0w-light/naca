/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.SQL;

import generate.CBaseLanguageExporter;

import java.util.Vector;

import semantic.CBaseActionEntity;
import utils.CObjectCatalog;

import java.util.ArrayList;

public abstract class CEntitySQLInsertStatement extends CBaseActionEntity
{
	public CEntitySQLInsertStatement(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}
	
	public void SetInsert(CEntitySQLDeclareTable table, Vector arrVal)
	{
		table = table ;
		arrValues = arrVal;
	}
	public void SetInsert(String tableName, ArrayList<String> arrColumns, Vector arrVal)
	{
		csTable = tableName;
		ASSERT(arrColumns) ;
		arrCollumns = arrColumns ;
		ASSERT(arrVal) ;
		arrValues = arrVal ;
	}
	public void SetInsert(String tablename, String clause, Vector arrParam)
	{
		csTable = tablename ;
		selectClause = clause ;
		arrSelectParameters = arrParam ;	
	}
	
	public void setSessionTable(boolean bSessionTable)
	{
		bSessionTable = bSessionTable;
	}
	
	protected String csTable = "" ;
	protected boolean bSessionTable = false;
	protected CEntitySQLDeclareTable table = null ;
	protected ArrayList<String> arrCollumns = null;
	protected Vector arrValues = null;
	protected String selectClause = "" ;
	protected Vector arrSelectParameters = null ;
	
	public void Clear()
	{
		super.Clear();
		if (table != null)
		{
			table.Clear() ;
		}
		if (arrValues != null)
		{
			arrValues.clear() ;
		}
		if (arrSelectParameters != null)
		{
			arrSelectParameters.clear() ;
		}
	}
	public boolean ignore()
	{
		return false ;
	}	
	
}
