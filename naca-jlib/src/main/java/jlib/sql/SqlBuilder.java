/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.sql;

/** Provides sql builder behavior. */
public class SqlBuilder
{
    private StringBuilder sb;

    /** Creates a new sql builder instance. */
    public SqlBuilder()
    {
        sb = new StringBuilder();
    }

    /** Executes the append operation. */
    public void append(String sql)
    {
        sb.append(sql + "\r\n");
    }

    /** Returns a string representation of this value. */
    public String toString()
    {
        return sb.toString();
    }
}
