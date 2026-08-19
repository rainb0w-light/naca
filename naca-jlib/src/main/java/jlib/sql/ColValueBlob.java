/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.sql;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

/** Provides col value blob behavior. */
public class ColValueBlob extends ColValue
{
    /** Creates a new col value blob instance. */
    public ColValueBlob(String csName, SerialBlob blob)
    {
        super(csName);
        blValue = blob;
    }

    /** Executes the duplicate operation. */
    public ColValue duplicate()
    {
        return new ColValueBlob(csName, blValue);
    }

    /** Sets the param sqlclause. */
    public void setParamSQLClause(SQLClause clause)
    {
        clause.param(blValue);
    }

    /** Executes the do fill with resurlt set col operation. */
    public void doFillWithResurltSetCol(ResultSet resultSet, int nCol)
        throws SQLException
    {
        Blob blob = resultSet.getBlob(nCol);
        blValue = new SerialBlob(blob);
    }

    public String getValueAsString()
    {
        return blValue.toString();
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
        return "(Blog):'"+blValue.toString();
    }

    String getType()
    {
        return "Blob";
    }

    int getSQLType()
    {
        return Types.BLOB;
    }

    Object getValue()
    {
        return blValue;
    }

    /** Returns whether set col param. */
    public boolean canSetColParam()
    {
        return true;
    }

    /** Sets the param into stmt. */
    public boolean setParamIntoStmt(PreparedStatement stmt, int nCol)
    {
        InputStream is;
        try
        {
            is = blValue.getBinaryStream();
            int nLength = is.available();
            stmt.setBinaryStream(nCol+1, is, nLength);
        }
        catch (SerialException e1)
        {
            e1.printStackTrace();
            return false;
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

    private SerialBlob blValue = null;
}
