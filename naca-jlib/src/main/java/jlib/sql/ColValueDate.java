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
import java.util.Date;

import jlib.misc.DateUtil;


public class ColValueDate extends ColValue
{
	ColValueDate(String csName, String csReplacement, Date dateValue)
	{
		super(csName, csReplacement);
		dateValue = dateValue;
	}
	
	ColValueDate(String csName, Date dateValue)
	{
		super(csName);
		dateValue = dateValue;
	}
	
	public ColValue duplicate()
	{
		return new ColValueDate(csName, dateValue);
	}
	
	public void setParamSQLClause(SQLClause clause)
	{
		clause.param(dateValue);
	}	
	
	public void doFillWithResurltSetCol(ResultSet resultSet, int nCol)
		throws SQLException
	{
		dateValue = resultSet.getDate(nCol);
	}

	public String getValueAsString()
	{	
		if(dateValue == null)	// Now
		{
			return null;
//			Date date = new Date();
//			return new DateUtil("yyyyMMdd HH:mm:ss", date).toString();
//			return String.valueOf(date);
		}
		return new DateUtil("yyyyMMdd HH:mm:ss", dateValue).toString();
//		return String.valueOf(dateValue);		
	}
	
	public int getValueAsInt()
	{
		return 0;
	}
	
	double getValueAsDouble()
	{
		return 0.0;
	}
	
	String getDumpValueAsString()
	{
		if(dateValue == null)	// Now
		{
			Date date = new Date();
			return "(Date now):'"+String.valueOf(date)+"'";
		}
		return "(Date):'"+String.valueOf(dateValue)+"'";		
	}	
	
	String getType()
	{
		return "Date";
	}
	
	int getSQLType()
	{
		return Types.DATE;
	}
	
	Object getValue()
	{
//		if(dateValue == null)	// Now
//		{
//			Date date = new Date();
//			return date;
//		}
		return dateValue;
	}

	
	Date dateValue = null;
}
