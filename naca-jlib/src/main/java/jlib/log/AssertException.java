/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.log;

/** Signals a assert exception condition. */
public class AssertException extends Error
{
    private static final long serialVersionUID = 1L;

    /** Creates a new assert exception instance. */
    public AssertException()
    {
    }

    /** Creates a new assert exception instance. */
    public AssertException(String arg0)
    {
        super(arg0);
    }
}
