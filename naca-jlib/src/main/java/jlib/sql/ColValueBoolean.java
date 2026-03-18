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

public class ColValueBoolean extends ColValue
{
	public ColValueBoolean(String csName, boolean bValue)
	{
		super(csName);
		bValue = bValue;
	}
	
	public ColValue duplicate()
	{
		return new ColValueBoolean(csName, bValue);
	}
	
	public void setParamSQLClause(SQLClause clause)
	{
		clause.param(bValue);
	}	
	
	public void doFillWithResurltSetCol(ResultSet resultSet, int nCol)
		throws SQLException
	{
		bValue = resultSet.getBoolean(nCol);
	}

	public String getValueAsString()
	{
		return String.valueOf(bValue);
	}
	
	String getDumpValueAsString()
	{
		return "(Boolean):'"+String.valueOf(bValue)+"'";
	}
	
	public int getValueAsInt()
	{
		if(bValue)
			return 1;
		return 0;
	}
	
	double getValueAsDouble()
	{
		if(bValue)
			return 1.0;
		return 0.0;
	}
	
	String getType()
	{
		return "Boolean";
	}
	
	int getSQLType()
	{
		return Types.BOOLEAN;
	}
	
	Object getValue()
	{
		return String.valueOf(Boolean.valueOf(bValue));
	}
	
	boolean bValue = false;
}
