/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package jlib.polling;

import java.util.ArrayList;

import jlib.threads.Timer;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: PollingTimer.java,v 1.1 2008/06/19 14:18:32 u930di Exp $
 */
public class PollingTimer extends Timer
{
	private ArrayList<BaseDirectoryPoller> dirsPollers = null;
	
	synchronized public void addDirectoryPoller(BaseDirectoryPoller dirPoller)
	{
		if(dirsPollers == null)
			dirsPollers = new ArrayList<BaseDirectoryPoller>();
		dirsPollers.add(dirPoller);
	}
	
	public boolean PollAtLoadTime()
	{
		return doPulse();
	}
	
	protected boolean pulse()
	{
		return doPulse();
	}
	
	synchronized private boolean doPulse()
	{
		if(dirsPollers != null)
		{
			for(int n = 0; n< dirsPollers.size(); n++)
			{
				BaseDirectoryPoller dirPoller = dirsPollers.get(n);
				dirPoller.poll();
			}
		}
		return true;
	}
}
