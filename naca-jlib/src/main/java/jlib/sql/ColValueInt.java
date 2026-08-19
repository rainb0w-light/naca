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


/** Provides col value int behavior. */
public class ColValueInt extends ColValue
{
    /** Creates a new col value int instance. */
    public ColValueInt(String csName, int nValue)
    {
        super(csName);
        this.nValue = nValue;
    }

    /** Executes the duplicate operation. */
    public ColValue duplicate()
    {
        return new ColValueInt(csName, nValue);
    }

    /** Sets the param sqlclause. */
    public void setParamSQLClause(SQLClause clause)
    {
        clause.param(nValue);
    }

    /** Executes the do fill with resurlt set col operation. */
    public void doFillWithResurltSetCol(ResultSet resultSet, int nCol)
        throws SQLException
    {
        nValue = resultSet.getInt(nCol);
    }

    public String getValueAsString()
    {
        return String.valueOf(nValue);
    }

    String getDumpValueAsString()
    {
        return "(Int):'"+String.valueOf(nValue)+"'";
    }

    public int getValueAsInt()
    {
        return nValue;
    }

    double getValueAsDouble()
    {
        return (double)nValue;
    }

    String getType()
    {
        return "Int";
    }

    int getSQLType()
    {
        return Types.INTEGER;
    }

    Object getValue()
    {
        return String.valueOf(Integer.valueOf(nValue));
    }


    int nValue = 0;
}
