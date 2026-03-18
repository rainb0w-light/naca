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

public class ColValueString extends ColValue
{
	public ColValueString(String csName, String csReplacement, String csValue)
	{
		super(csName,csReplacement);
		csValue = csValue;
	}
	
	public ColValueString(String csName,  String csValue)
	{
		super(csName);
		csValue = csValue;
	}
	
	public ColValue duplicate()
	{
		return new ColValueString(csName, csValue);
	}
	
	public void setParamSQLClause(SQLClause clause)
	{
		clause.param(csValue);
	}
	
	public void doFillWithResurltSetCol(ResultSet resultSet, int nCol)
		throws SQLException
	{
		csValue = resultSet.getString(nCol);
	}
	
	public String getValueAsString()
	{
		return csValue;
	}
	
	public int getValueAsInt()
	{
		return NumberParser.getAsInt(csValue);
	}
	
	double getValueAsDouble()
	{
		return NumberParser.getAsDouble(csValue);
	}

	String getDumpValueAsString()
	{
		return "(String):'"+csValue+"'";
	}
		
	Object getValue()
	{
		return csValue;
	}

	String getType()
	{
		return "String";
	}
	
	int getSQLType()
	{
		return Types.CHAR;
	}
	
	String csValue = null;
}
