/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.threads;

/**
 * @author u930di
 *
 */
public abstract class BaseThread extends Thread
{
    /** Creates a new base thread instance. */
    public BaseThread()
    {
    }

    /** Runs this operation. */
    public abstract void run();

    /** Executes the request stop operation. */
    public void requestStop()
    {
        interrupt();
    }
}
