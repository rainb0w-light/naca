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
	public BaseThread()
	{
	}

	public abstract void run();

	public void requestStop()
	{
		interrupt();
	}
}
