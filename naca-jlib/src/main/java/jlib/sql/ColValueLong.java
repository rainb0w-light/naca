/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import jlib.misc.NumberParser;

public class ColValueLong extends ColValue
{
	public ColValueLong(String csName, long lValue)
	{
		super(csName);
		lValue = lValue;
	}
	
	public ColValue duplicate()
	{
		return new ColValueLong(csName, lValue);
	}
	
	public void setParamSQLClause(SQLClause clause)
	{
		clause.param(lValue);
	}
	
	public void doFillWithResurltSetCol(ResultSet resultSet, int nCol)
		throws SQLException
	{
		lValue = resultSet.getLong(nCol);
	}

	public String getValueAsString()
	{
		return String.valueOf(lValue);
	}
	
	public int getValueAsInt()
	{
		return (int)lValue;
	}
	
	double getValueAsDouble()
	{
		return (double)lValue;
	}
	
	String getDumpValueAsString()
	{
		return "(long):'"+String.valueOf(lValue)+"'";
	}
	
	String getType()
	{
		return "long";
	}
	
	int getSQLType()
	{
		return Types.INTEGER;
	}
	
	Object getValue()
	{
		return String.valueOf(Long.valueOf(lValue));
	}
	
	long lValue = 0L;
}
