/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.log;

/**
 * @author PJD
 *
 */
public class LogEventWarning extends LogEvent
{
    LogEventWarning()
    {
        super(LogEventType.Warning, LogFlowStd.System, LogLevel.Normal);
    }

    /** Executes the info operation. */
    public static LogEvent info()
    {
        LogEventWarning event = new LogEventWarning();
        return event;
    }
}
