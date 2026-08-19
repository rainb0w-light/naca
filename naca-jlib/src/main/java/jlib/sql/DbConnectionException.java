/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.sql;

/** Signals a db connection exception condition. */
public class DbConnectionException extends Exception
{
    private static final long serialVersionUID = "DbConnectionException".hashCode();

    private String message;

    /** Creates a new db connection exception instance. */
    public DbConnectionException(String csMessage)
    {
        this.message = csMessage;
    }

    /** Returns a string representation of this value. */
    public String toString()
    {
        return "DbConnectionException: "+message;
    }
}
