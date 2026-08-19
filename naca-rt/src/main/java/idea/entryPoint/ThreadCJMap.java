/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package idea.entryPoint;

import jlib.misc.ThreadSafeCounter;

/** Provides thread cjmap behavior. */
public class ThreadCJMap extends Thread
{
    /** Creates a new thread cjmap instance. */
    public ThreadCJMap(ThreadSafeCounter counter, CJMapThreadedRun cjmapRun)
    {
        this.counter = counter;
        this.cjmapRun = cjmapRun;
    }

    /** Runs this operation. */
    public void run()
    {
        cjmapRun.run();
        counter.dec();
    }

    /** Executes the request stop operation. */
    public void requestStop()
    {
        interrupt();
    }

    private CJMapThreadedRun cjmapRun = null;
    private ThreadSafeCounter counter = null;
}
