/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.sql;

import java.util.Hashtable;

public class DbDataCacheManager
{
	protected Hashtable<String, DbDataCacheTable> tabTables = null;
	
	public DbDataCacheManager()
	{
		tabTables = new Hashtable<String, DbDataCacheTable>() ;	// hash collection of CSQLDataCacheTable indexed by csTableName
	}
	
	protected class DbDataCacheTable
	{
		public Hashtable<String, Object> tabData = new Hashtable<String, Object>() ;
		public String csTableName = "" ; 
	}
	
	public void RegisterData(String csTableName, String csKey, Object val)
	{
		DbDataCacheTable table = tabTables.get(csTableName) ;
		if (table == null)
		{
			table = new DbDataCacheTable() ;
			table.csTableName = csTableName ;
			tabTables.put(csTableName, table) ;
		}
		table.tabData.put(csKey, val) ;
	}
	
	public Object getData(String csTableName, String csKey)
	{
		DbDataCacheTable table = tabTables.get(csTableName) ;
		if (table == null)
		{
			return null ;
		}
		if (table.tabData.containsKey(csKey))
		{
			return table.tabData.get(csKey) ;
		}
		return null ;
	} 
}
