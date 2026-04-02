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

public class ColValueDouble extends ColValue
{
	public ColValueDouble(String csName, double dValue)
	{
		super(csName);
		dValue = dValue;
	}
	
	public ColValue duplicate()
	{
		return new ColValueDouble(csName, value);
	}
	
	public void setParamSQLClause(SQLClause clause)
	{
		clause.param(value);
	}
	
	public void doFillWithResurltSetCol(ResultSet resultSet, int nCol)
		throws SQLException
	{
		value = resultSet.getDouble(nCol);
	}

	public String getValueAsString()
	{
		return String.valueOf(value);
	}
	
	String getDumpValueAsString()
	{
		return "(Double):'"+String.valueOf(value)+"'";
	}
	
	public int getValueAsInt()
	{
		return (int) value;
	}
	
	double getValueAsDouble()
	{
		return value;
	}
	
	String getType()
	{
		return "Double";
	}
	
	int getSQLType()
	{
		return Types.DOUBLE;
	}
	
	Object getValue()
	{
		return String.valueOf(Double.valueOf(value));
	}

	
	double value = 0.0;
}
