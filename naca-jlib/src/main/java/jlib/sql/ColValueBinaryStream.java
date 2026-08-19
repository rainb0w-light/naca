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


/** Provides col value binary stream behavior. */
public class ColValueBinaryStream extends ColValue
{
    /** Creates a new col value binary stream instance. */
    public ColValueBinaryStream(String csName, InputStream is)
    {
        super(csName);
        this.is = is;
    }

    /** Executes the duplicate operation. */
    public ColValue duplicate()
    {
        return new ColValueBinaryStream(csName, is);
    }

    /** Sets the param sqlclause. */
    public void setParamSQLClause(SQLClause clause)
    {
        clause.param(is);
    }

    /** Executes the do fill with resurlt set col operation. */
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

    /** Returns whether set col param. */
    public boolean canSetColParam()
    {
        return true;
    }

    /** Sets the param into stmt. */
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
