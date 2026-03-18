/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.threads;

public abstract class ThreadPoolRequest
{
	public ThreadPoolRequest(boolean bTerminaison)
	{
		bTerminaisonRequest = bTerminaison;
	}

	public boolean getTerminaisonRequest()
	{
		return bTerminaisonRequest;
	};
	
	protected void setNotTerminaisonRequest()
	{
		bTerminaisonRequest = false;
	};
	
	/*!	
	Execute (virtual)
	\retval: ULONG: return code of the execution
	\note This function mus be override in derivated
	*/
	public abstract void execute();
	
	private	boolean bTerminaisonRequest;
}
