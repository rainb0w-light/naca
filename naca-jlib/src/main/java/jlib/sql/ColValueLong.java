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


/** Provides col value long behavior. */
public class ColValueLong extends ColValue
{
    /** Creates a new col value long instance. */
    public ColValueLong(String csName, long lValue)
    {
        super(csName);
        this.lValue = lValue;
    }

    /** Executes the duplicate operation. */
    public ColValue duplicate()
    {
        return new ColValueLong(csName, lValue);
    }

    /** Sets the param sqlclause. */
    public void setParamSQLClause(SQLClause clause)
    {
        clause.param(lValue);
    }

    /** Executes the do fill with resurlt set col operation. */
    public void doFillWithResurltSetCol(ResultSet resultSet, int nCol)
        throws SQLException
    {
        lValue = resultSet.getLong(nCol);
    }

    public String getValueAsString()
    {
        return String.valueOf(lValue);
    }

    public int getValueAsInt()
    {
        return (int)lValue;
    }

    double getValueAsDouble()
    {
        return (double)lValue;
    }

    String getDumpValueAsString()
    {
        return "(long):'"+String.valueOf(lValue)+"'";
    }

    String getType()
    {
        return "long";
    }

    int getSQLType()
    {
        return Types.INTEGER;
    }

    Object getValue()
    {
        return String.valueOf(Long.valueOf(lValue));
    }

    long lValue = 0L;
}
