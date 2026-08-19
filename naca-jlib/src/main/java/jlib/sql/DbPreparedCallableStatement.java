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
/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: DbPreparedCallableStatement.java,v 1.5 2007/10/17 05:04:27 u930di Exp $
 */
public class DbPreparedCallableStatement
{
    protected CallableStatement callableStatement = null;

    /** Creates a new db prepared callable statement instance. */
    public DbPreparedCallableStatement(CallableStatement callableStatement)
    {
        init(callableStatement);
    }

    /** Executes the init operation. */
    public void init(CallableStatement callableStatement)
    {
        this.callableStatement = callableStatement;
    }

    /** Sets the in value. */
    public boolean setInValue(int nParamId, double d)
    {
        try
        {
            callableStatement.setDouble(nParamId, d);
            return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /** Sets the in value with exception. */
    public boolean setInValueWithException(int nParamId, double d)
        throws SQLException
    {
        callableStatement.setDouble(nParamId, d);
        return true;
    }

    /** Sets the in value. */
    public boolean setInValue(int nParamId, int n)
    {
        try
        {
            callableStatement.setInt(nParamId, n);
            return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /** Sets the in value with exception. */
    public boolean setInValueWithException(int nParamId, short s)
        throws SQLException
    {
        callableStatement.setShort(nParamId, s);
        return true;
    }

    /** Sets the in value. */
    public boolean setInValue(int nParamId, short s)
    {
        try
        {
            callableStatement.setShort(nParamId, s);
            return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /** Sets the in value with exception. */
    public boolean setInValueWithException(int n, String cs)
        throws SQLException
    {
        callableStatement.setString(n, cs);
        return true;
    }

    /** Sets the in value. */
    public boolean setInValue(int n, String cs)
    {
        try
        {
            callableStatement.setString(n, cs);
            return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }


    /** Returns the out value string with exception. */
    public String getOutValueStringWithException(int nParamId)
        throws SQLException
    {
        return callableStatement.getString(nParamId);
    }

    /** Returns the out value string. */
    public String getOutValueString(int nParamId) throws SQLException
    {
        return callableStatement.getString(nParamId);
    }
//
//  public String getOutValueString(int nParamId)
//  {
//      try
//      {
//          return callableStatement.getString(nParamId);
//      }
//      catch (SQLException e)
//      {
//          e.printStackTrace();
//      }
//      return "";
//  }

    /** Returns the out value double with exception. */
    public double getOutValueDoubleWithException(int nParamId)
        throws SQLException
    {
        return callableStatement.getDouble(nParamId);
    }

    /** Returns the out value double. */
    public double getOutValueDouble(int nParamId)
    {
        try
        {
            return callableStatement.getDouble(nParamId);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return 0.0;
    }

    /** Returns the out value int with exception. */
    public int getOutValueIntWithException(int nParamId)
        throws SQLException
    {
        return callableStatement.getInt(nParamId);
    }

    /** Returns the out value int. */
    public int getOutValueInt(int nParamId)
    {
        try
        {
            return callableStatement.getInt(nParamId);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return 0;
    }


    /** Returns the out value short with exception. */
    public short getOutValueShortWithException(int nParamId)
        throws SQLException
    {
        return callableStatement.getShort(nParamId);
    }

    /** Returns the out value short. */
    public short getOutValueShort(int nParamId)
    {
        try
        {
            return callableStatement.getShort(nParamId);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    /** Executes the register out parameter with exception operation. */
    public boolean registerOutParameterWithException(int nParamId, int nTypeId)
        throws SQLException
    {
        callableStatement.registerOutParameter(nParamId, nTypeId);
        return true;
    }

    /** Executes the register out parameter operation. */
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


    /** Executes the register out parameter operation. */
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

    /** Executes the register out parameter operation. */
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

    /** Executes the execute with exception operation. */
    public boolean executeWithException()
        throws SQLException
    {
        boolean b = callableStatement.execute();
        return b;
    }

    /** Executes the execute operation. */
    public boolean execute()
        throws SQLException
    {
        boolean b = callableStatement.execute();
        return b;
    }

    /** Closes the with exception. */
    public boolean closeWithException()
        throws SQLException
    {
        callableStatement.close();
        return true;
    }

    /** Executes the close operation. */
    public boolean close()
    {
        try
        {
            callableStatement.close();
            return true;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
