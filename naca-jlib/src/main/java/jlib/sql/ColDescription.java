/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package jlib.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import jlib.log.Log;


/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: ColDescription.java,v 1.3 2006/09/19 13:12:02 u930cv Exp $
 */
public class ColDescription extends ColDescriptionInfo
{
	ColDescription()
	{
	}


	boolean fill(ResultSet col)	// Fill from a resiultSet of the catalog of a table; not a resultSet of the data themselves
	{
		try
		{
			csColName = col.getString("COLUMN_NAME");
			nTypeId = col.getInt("DATA_TYPE");
			nScale = col.getInt("DECIMAL_DIGITS");
			nPrecision = col.getInt("COLUMN_SIZE");
			
			return true;
		}
		catch (SQLException e)
		{
			Log.logCritical("Exception catched While filling DB table's Column Description:" + e.toString());
		}
		return false;
	}
	
	boolean fill(ResultSetMetaData mt, int nColId)	// Fill from the meta data of a result set
	{
		try
		{
			csColName = mt.getColumnName(nColId);
			nTypeId = mt.getColumnType(nColId);
			nPrecision = mt.getPrecision(nColId);
			nScale = mt.getScale(nColId);
			return true;
		}
		catch (SQLException e)
		{
			Log.logCritical("Exception catched While filling DB table's Column Description:" + e.toString());
		}
		return false;
	}
}
