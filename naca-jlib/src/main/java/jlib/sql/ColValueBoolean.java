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
	public ColValueBoolean(String csName, boolean isvalue)
	{
		super(csName);
		isvalue = isvalue;
	}
	
	public ColValue duplicate()
	{
		return new ColValueBoolean(csName, isvalue);
	}
	
	public void setParamSQLClause(SQLClause clause)
	{
		clause.param(isvalue);
	}	
	
	public void doFillWithResurltSetCol(ResultSet resultSet, int nCol)
		throws SQLException
	{
		isvalue = resultSet.getBoolean(nCol);
	}

	public String getValueAsString()
	{
		return String.valueOf(isvalue);
	}
	
	String getDumpValueAsString()
	{
		return "(Boolean):'"+String.valueOf(isvalue)+"'";
	}
	
	public int getValueAsInt()
	{
		if(isvalue)
			return 1;
		return 0;
	}
	
	double getValueAsDouble()
	{
		if(isvalue)
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
		return String.valueOf(Boolean.valueOf(isvalue));
	}
	
	boolean isvalue = false;
}
