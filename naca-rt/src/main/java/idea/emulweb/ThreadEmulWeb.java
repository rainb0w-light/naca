/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package idea.emulweb;

import jlib.misc.ThreadSafeCounter;

/** Provides thread emul web behavior. */
public class ThreadEmulWeb extends Thread
{
    /** Creates a new thread emul web instance. */
    public ThreadEmulWeb(ThreadSafeCounter counter, EmulWebThreadedRun emulWebRun)
    {
        this.counter = counter;
        this.emulWebRun = emulWebRun;
    }

    /** Runs this operation. */
    public void run()
    {
        emulWebRun.run();
        counter.dec();
    }

    /** Executes the request stop operation. */
    public void requestStop()
    {
        interrupt();
    }

    private EmulWebThreadedRun emulWebRun = null;
    private ThreadSafeCounter counter = null;
}
