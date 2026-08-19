/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.threads;

/** Provides thread pool request terminaison behavior. */
public class ThreadPoolRequestTerminaison extends ThreadPoolRequest
{
    ThreadPoolRequestTerminaison()
    {
        super(true);
    }

    /** Executes the execute operation. */
    public void execute()
    {
    }
}
