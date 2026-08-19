/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.threads;

/** Provides thread pool request behavior. */
public abstract class ThreadPoolRequest
{
    /** Creates a new thread pool request instance. */
    public ThreadPoolRequest(boolean bTerminaison)
    {
        isterminaisonRequest = bTerminaison;
    }

    public boolean getTerminaisonRequest()
    {
        return isterminaisonRequest;
    };

    protected void setNotTerminaisonRequest()
    {
        isterminaisonRequest = false;
    };

    /*!
    Execute (virtual)
    \retval: ULONG: return code of the execution
    \note This function mus be override in derivated
    */
    /** Executes the execute operation. */
    public abstract void execute();

    private boolean isterminaisonRequest;
}
