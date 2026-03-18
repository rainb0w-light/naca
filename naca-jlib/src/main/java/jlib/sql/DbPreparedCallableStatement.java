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

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: DbPreparedCallableStatement.java,v 1.5 2007/10/17 05:04:27 u930di Exp $
 */
public class DbPreparedCallableStatement
{
	protected CallableStatement callableStatement = null;
	
	public DbPreparedCallableStatement(CallableStatement callableStatement)
	{
		init(callableStatement);
	}	
	
	public void init(CallableStatement callableStatement)
	{
		callableStatement = callableStatement;
	}
	
	public boolean setInValue(int nParamId, double d)
	{
		try
		{
			callableStatement.setDouble(nParamId, d);
			return true;
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean setInValueWithException(int nParamId, double d)
		throws SQLException
	{
		callableStatement.setDouble(nParamId, d);
		return true;
	}
	
	public boolean setInValue(int nParamId, int n)
	{
		try
		{
			callableStatement.setInt(nParamId, n);
			return true;
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean setInValueWithException(int nParamId, short s)
		throws SQLException
	{
		callableStatement.setShort(nParamId, s);
		return true;
	}
	
	public boolean setInValue(int nParamId, short s)
	{
		try
		{
			callableStatement.setShort(nParamId, s);
			return true;
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public boolean setInValueWithException(int n, String cs)
		throws SQLException
	{
		callableStatement.setString(n, cs);
		return true;
	}

	public boolean setInValue(int n, String cs)
	{
		try
		{
			callableStatement.setString(n, cs);
			return true;
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
	public String getOutValueStringWithException(int nParamId)
		throws SQLException
	{
		return callableStatement.getString(nParamId);
	}
	
	public String getOutValueString(int nParamId) throws SQLException
	{
		return callableStatement.getString(nParamId);
	}
//	
//	public String getOutValueString(int nParamId)
//	{
//		try
//		{
//			return callableStatement.getString(nParamId);
//		}
//		catch (SQLException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return "";
//	}
	
	public double getOutValueDoubleWithException(int nParamId)
		throws SQLException
	{
		return callableStatement.getDouble(nParamId);
	}
	
	public double getOutValueDouble(int nParamId)
	{
		try
		{
			return callableStatement.getDouble(nParamId);
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0.0;
	}
	
	public int getOutValueIntWithException(int nParamId)
		throws SQLException
	{
		return callableStatement.getInt(nParamId);
	}

	public int getOutValueInt(int nParamId)
	{
		try
		{
			return callableStatement.getInt(nParamId);
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	
	public short getOutValueShortWithException(int nParamId)
		throws SQLException
	{
		return callableStatement.getShort(nParamId);
	}
	
	public short getOutValueShort(int nParamId)
	{
		try
		{
			return callableStatement.getShort(nParamId);
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public boolean registerOutParameterWithException(int nParamId, int nTypeId)
		throws SQLException
	{
		callableStatement.registerOutParameter(nParamId, nTypeId);
		return true;
	}
	
	public boolean registerOutParameter(int nParamId, int nTypeId)
	{
		try
		{
			callableStatement.registerOutParameter(nParamId, nTypeId);
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	
	public boolean registerOutParameter(String csName, ColDescriptionInfo colDescriptionInfo)
	{
		try
		{
			callableStatement.registerOutParameter(csName, colDescriptionInfo.nTypeId);
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean registerOutParameter(int nParamId, ColDescriptionInfo colDescriptionInfo)
	{
		try
		{
			callableStatement.registerOutParameter(nParamId, colDescriptionInfo.nTypeId);
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean executeWithException() 
		throws SQLException
	{
		boolean b = callableStatement.execute();
		return b;
	}

	public boolean execute() 
		throws SQLException
	{
		boolean b = callableStatement.execute();
		return b;
	}
	
	public boolean closeWithException()
		throws SQLException
	{		
		callableStatement.close();
		return true;
	}

	public boolean close()
	{
		try
		{
			callableStatement.close();
			return true;
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
