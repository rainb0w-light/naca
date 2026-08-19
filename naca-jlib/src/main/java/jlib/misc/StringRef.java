/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

/** Provides string ref behavior. */
public class StringRef
{
    /** Creates a new string ref instance. */
    public StringRef()
    {
    }

    /** Creates a new string ref instance. */
    public StringRef(String cs)
    {
        this.cs = cs;
    }


    /** Executes the get operation. */
    public String get()
    {
        return cs;
    }

    /** Executes the set operation. */
    public void set(String cs)
    {
        this.cs = cs;
    }

    /** Returns a string representation of this value. */
    public String toString()
    {
        if (cs != null) {
            return "StringRef: \"" + cs + "\"";
        }
        return "StringRef: \"<null>\"";
    }

    private String cs = null;
}
