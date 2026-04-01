/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package jlib.threads;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id$
 */
// Usage 

/*
public class MyTimer extends Timer
{
	private Caller c = null;
	MyTimer(Caller c)
	{
		this.c = c;
	}
	
	protected boolean pulse()
	{
		return c.onTimerPulse();
	}
}

// Caller code in class Caller 

class Caller
{
...
	void launchTimer()
	{
		...
		MyTimer timer = new MyTimer(this);
		timer.startTimer(60000);	// Pulse every minute
		...
	}
	
	public boolean onTimerPulse()	// Executed in the context of the timer thread
	{
		// Do actions ...
		return true; 
	}

*/

public abstract class Timer extends Thread
{
	private int nPeriodWait_ms = 1000;
	
	public Timer()
	{
	}
	
	public void startTimer(int nPeriodWait_ms)
	{
		nPeriodWait_ms = nPeriodWait_ms;
		start();
	}
	
	public void run()
	{
		boolean iscontinue = true;
		while(iscontinue)
		{
			try
			{
				Thread.sleep(nPeriodWait_ms);
				iscontinue = pulse();
			} 
			catch (InterruptedException e)
			{
			}
		}
	}
	
	protected abstract boolean pulse();
	
	public void requestStop()
	{
		interrupt();
	}
}
