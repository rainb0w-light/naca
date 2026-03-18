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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: ColValueBigDecimal.java,v 1.7 2007/12/05 09:43:28 u930bm Exp $
 */
public class ColValueBigDecimal extends ColValue
{
	public ColValueBigDecimal(String csName, BigDecimal bdValue)
	{
		super(csName);
		bdValue = bdValue;
	}
	
	public ColValue duplicate()
	{
		return new ColValueBigDecimal(csName, bdValue);
	}
	
	public void setParamSQLClause(SQLClause clause)
	{
		clause.param(bdValue);
	}	
	
	public void doFillWithResurltSetCol(ResultSet resultSet, int nCol)
		throws SQLException
	{
		bdValue = resultSet.getBigDecimal(nCol);
	}

	public String getValueAsString()
	{
		return String.valueOf(bdValue);
	}
	
	public int getValueAsInt()
	{
		return bdValue.intValue();
	}
	
	double getValueAsDouble()
	{
		return bdValue.doubleValue();
	}

	String getDumpValueAsString()
	{
		return "(BigDecimal):'"+String.valueOf(bdValue)+"'";
	}
	
	String getType()
	{
		return "BigDecimal";
	}
	
	int getSQLType()
	{
		return Types.DECIMAL;
	}
	
	Object getValue()
	{
		return bdValue;
	}
	
	BigDecimal bdValue = null;
}
