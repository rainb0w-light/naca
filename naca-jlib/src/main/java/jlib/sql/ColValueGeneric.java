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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import jlib.misc.NumberParser;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: ColValueGeneric.java,v 1.7 2007/12/05 09:43:28 u930bm Exp $
 */
public class ColValueGeneric extends ColValue
{
    public ColValueGeneric(String csName)
    {
        super(csName);
    }

    public ColValue duplicate()
    {
        return new ColValueGeneric(csName);
    }

    public void setParamSQLClause(SQLClause clause)
    {
        clause.param(value);
    }

    public void doFillWithResurltSetCol(ResultSet resultSet, int nCol)
        throws SQLException
    {
        value = resultSet.getString(nCol);
    }

    public void setValue(String csValue)
    {
        this.value = csValue;
    }

    public void setValue(int n)
    {
        value = String.valueOf(n);
    }

    public void setValue(long l)
    {
        value = String.valueOf(l);
    }

    public String getValueAsString()
    {
        return value;
    }

    public int getValueAsInt()
    {
        return NumberParser.getAsInt(value);
    }

    double getValueAsDouble()
    {
        return NumberParser.getAsDouble(value);
    }

    String getDumpValueAsString()
    {
        return "(Generic):'"+value+"'";
    }

    String getType()
    {
        return "Generic";
    }

    int getSQLType()
    {
        return Types.CHAR;
    }

    Object getValue()
    {
        return value;
    }

    private String value = null;
}
