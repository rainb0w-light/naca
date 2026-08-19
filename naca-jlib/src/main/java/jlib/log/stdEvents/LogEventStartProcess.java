/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.log.stdEvents;

import jlib.log.Log;
import jlib.log.LogEvent;
import jlib.log.LogEventType;
import jlib.log.LogFlowStd;
import jlib.log.LogLevel;


/**
 * @deprecated Use {@link EventStart} instead.
 * @author u930di
 */
public class LogEventStartProcess extends LogEvent
{
    /** Creates a new log event start process instance. */
    public LogEventStartProcess(String csProduct)
    {
        super(LogEventType.Start, LogFlowStd.Any, LogLevel.Critical, csProduct);
    }

    /** Executes the log operation. */
    public static LogEvent log(String csChannel)
    {
        return LogEventStartProcess.log(csChannel, null);
    }

    /** Executes the log operation. */
    public static LogEvent log(String csChannel, String csProduct)
    {
        LogEventStartProcess event = new LogEventStartProcess(csProduct);
        Log.log(csChannel, event, "");
        return event;
    }

    /** Executes the log operation. */
    public static LogEvent log(String csChannel, String csRunId, String csRuntimeId)
    {
        LogEventStartProcess event = new LogEventStartProcess(null);
        Log.log(csChannel, event, "", csRunId, csRuntimeId);
        return event;
    }

    /** Executes the log operation. */
    public static LogEvent log(String csChannel, String csProduct, String csRunId, String csRuntimeId)
    {
        LogEventStartProcess event = new LogEventStartProcess(csProduct);
        Log.log(csChannel, event, "", csRunId, csRuntimeId);
        return event;
    }
}
