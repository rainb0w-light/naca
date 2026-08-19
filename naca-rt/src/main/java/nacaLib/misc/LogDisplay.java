/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.misc;

import jlib.log.Log;
import jlib.log.LogEvent;
import jlib.log.LogEventType;
import jlib.log.LogLevel;


/** Provides log display behavior. */
public class LogDisplay extends LogEvent
{
    /** Creates a new log display instance. */
    public LogDisplay()
    {
        super(LogEventType.Start, LogFlowCustomNacaRT.Display, LogLevel.Normal, null);
    }

    /** Executes the log operation. */
    public static LogEvent log(String csMessage)
    {
        LogDisplay event = new LogDisplay();
        Log.log("NacaRT", event, csMessage);
        return event;
    }
}
