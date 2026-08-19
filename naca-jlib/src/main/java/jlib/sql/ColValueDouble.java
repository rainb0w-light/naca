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

/** Provides col value double behavior. */
public class ColValueDouble extends ColValue
{
    /** Creates a new col value double instance. */
    public ColValueDouble(String csName, double dValue)
    {
        super(csName);
        this.value = dValue;
    }

    /** Executes the duplicate operation. */
    public ColValue duplicate()
    {
        return new ColValueDouble(csName, value);
    }

    /** Sets the param sqlclause. */
    public void setParamSQLClause(SQLClause clause)
    {
        clause.param(value);
    }

    /** Executes the do fill with resurlt set col operation. */
    public void doFillWithResurltSetCol(ResultSet resultSet, int nCol)
        throws SQLException
    {
        value = resultSet.getDouble(nCol);
    }

    public String getValueAsString()
    {
        return String.valueOf(value);
    }

    String getDumpValueAsString()
    {
        return "(Double):'"+String.valueOf(value)+"'";
    }

    public int getValueAsInt()
    {
        return (int) value;
    }

    double getValueAsDouble()
    {
        return value;
    }

    String getType()
    {
        return "Double";
    }

    int getSQLType()
    {
        return Types.DOUBLE;
    }

    Object getValue()
    {
        return String.valueOf(Double.valueOf(value));
    }


    double value = 0.0;
}
