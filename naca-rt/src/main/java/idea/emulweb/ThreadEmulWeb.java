/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package idea.emulweb;

import jlib.misc.ThreadSafeCounter;

public class ThreadEmulWeb extends Thread
{
	public ThreadEmulWeb(ThreadSafeCounter counter, EmulWebThreadedRun emulWebRun)
	{
		this.counter = counter;
		this.emulWebRun = emulWebRun;
	}
	
	public void run()
	{
		emulWebRun.run();
		counter.dec();
	}
	
	public void requestStop()
	{
		interrupt();
	}
	
	private EmulWebThreadedRun emulWebRun = null;
	private ThreadSafeCounter counter = null;
}