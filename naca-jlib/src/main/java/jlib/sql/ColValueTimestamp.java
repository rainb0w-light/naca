/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

import jlib.misc.NumberParser;


public class ColValueTimestamp extends ColValue
{
	public ColValueTimestamp(String csName,  Timestamp timestampValue)
	{
		super(csName);
		timestampValue = timestampValue;
	}
	
	public ColValue duplicate()
	{
		return new ColValueTimestamp(csName, timestampValue);
	}
	
	public void setParamSQLClause(SQLClause clause)
	{
		clause.param(timestampValue);
	}	
	
	public void doFillWithResurltSetCol(ResultSet resultSet, int nCol)
		throws SQLException
	{
		timestampValue = resultSet.getTimestamp(nCol);
	}

	public String getValueAsString()
	{
		if(timestampValue == null)	// Now
		{
			Date date = new Date();
			Time time = new Time(date.getTime());
			return String.valueOf(time);
		}
		return String.valueOf(timestampValue);		
	}
	
	String getDumpValueAsString()
	{
		if(timestampValue == null)	// Now
		{
			Date date = new Date();
			Time time = new Time(date.getTime());
			return "(Timestamp now):'"+String.valueOf(time)+"'";
		}
		return "(Timestamp):'"+String.valueOf(timestampValue)+"'";		
	}
	
	public int getValueAsInt()
	{
		return 0;
	}
	
	double getValueAsDouble()
	{
		return 0.0;
	}
	
	String getType()
	{
		return "Timestamp";
	}
	
	int getSQLType()
	{
		return Types.TIMESTAMP;
	}
	
	Object getValue()
	{
		if(timestampValue == null)	// Now
		{
			Date date = new Date();
			Timestamp ts = new Timestamp(date.getTime()); 
			return ts;
		}
		return timestampValue;
	}

	
	Timestamp timestampValue = null;
}

