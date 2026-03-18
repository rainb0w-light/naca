/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.sql;

import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


public class ColValueBinaryStream extends ColValue
{
	public ColValueBinaryStream(String csName, InputStream is)
	{
		super(csName);
		is = is;
	}
	
	public ColValue duplicate()
	{
		return new ColValueBinaryStream(csName, is);
	}
	
	public void setParamSQLClause(SQLClause clause)
	{
		clause.param(is);
	}
	
	public void doFillWithResurltSetCol(ResultSet resultSet, int nCol)
		throws SQLException
	{
		is = resultSet.getBinaryStream(nCol);
	}

	public String getValueAsString()
	{
		return "";
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
		return "(BinaryStream): not display";
	}
	
	String getType()
	{
		return "InputStream";
	}
	
	int getSQLType()
	{
		return Types.LONGVARBINARY;
	}
	
	Object getValue()
	{
		return is;
	}
	
	public boolean canSetColParam()
	{
		return true;
	}
	
	public boolean setParamIntoStmt(PreparedStatement stmt, int nCol)
	{
		try
		{
			int nLength = is.available();
			stmt.setBinaryStream(nCol+1, is, nLength);
		}
		catch (SQLException e)
		{
			LogSQLException.log(e);
			return false;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private InputStream is = null;
}
