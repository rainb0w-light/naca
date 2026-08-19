/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Provides col value behavior. */
public abstract class ColValue
{
    /** Creates a new col value instance. */
    public ColValue(String csName, String csReplacement)
    {
        this.csName = csName;
        this.csReplacement = csReplacement;
    }

    /** Creates a new col value instance. */
    public ColValue(String csName)
    {
        this.csName = csName;
        csReplacement = "?";
    }

    abstract String getDumpValueAsString();
    /** Returns the value as string. */
    public abstract String getValueAsString();
    /** Returns the value as int. */
    public abstract int getValueAsInt();
    abstract double getValueAsDouble();
    abstract Object getValue();
    abstract String getType();
    abstract int getSQLType();
    /** Executes the duplicate operation. */
    public abstract ColValue duplicate();
    /** Sets the param sqlclause. */
    public abstract void setParamSQLClause(SQLClause clause);
    /** Executes the do fill with resurlt set col operation. */
    public abstract void doFillWithResurltSetCol(ResultSet resultSet, int nCol) throws SQLException;

//  void setIntoObject(Object oMember)
//  {
//      Class memberClass = oMember.getClass();
//      if(memberClass.isInstance(Integer.class))
//      {
//          int n = getValueAsInt();
//          oMember.
//          ((Integer)oMember).getInteger(nm, val) = n;
//          return true;
//      }
//  }

    /** Executes the fill with resurlt set col operation. */
    public void fillWithResurltSetCol(ResultSet resultSet, int nCol)
        throws SQLException
    {
        doFillWithResurltSetCol(resultSet, nCol);
    }

    boolean hasName(String csKey)
    {
        if (csName.equalsIgnoreCase(csKey)) {
            return true;
        }
        return false;
    }

    boolean isOrder(int nOrder)
    {
        if (nOrder == nOrder) {
            return true;
        }
        return false;
    }

    void setOrder(int n)
    {
        nOrder = n;
    }

    /** Returns a string representation of this value. */
    public String toString()
    {
        return "[" + getType() + "] " + csName + "='" + getValueAsString() + "'";
    }

    public String getName()
    {
        return csName;
    }

    public String getNameUppercase()
    {
        return csName.toUpperCase();
    }

    public String getReplacement()
    {
        return csReplacement;
    }

    /** Returns whether set col param. */
    public boolean canSetColParam()
    {
        return false;
    }

    /** Sets the param into stmt. */
    public boolean setParamIntoStmt(PreparedStatement stmt, int nCol)
    {
        return false;
    }

    String csName = null;
    String csReplacement=null;
    int nOrder = 0;
}
