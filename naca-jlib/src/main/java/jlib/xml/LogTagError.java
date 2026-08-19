/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.xml;

import jlib.log.Log;
import jlib.log.LogEvent;
import jlib.log.LogEventType;
import jlib.log.LogExceptionEvent;
import jlib.log.LogFlowStd;
import jlib.log.LogLevel;

/** Provides log tag error behavior. */
public class LogTagError extends LogExceptionEvent
{
    /** Creates a new log tag error instance. */
    public LogTagError()
    {
        super(LogEventType.Error, LogFlowStd.Any, LogLevel.Debug);
    }

    /** Executes the log operation. */
    public static LogEvent log(Exception e)
    {
        LogTagError event = new LogTagError();
        event.fillExceptionMembers(e);
        Log.log(null, event, "Exception");
        return event;
    }
}
