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
public class LogEventError extends LogEvent
{
	LogEventError()
	{
		super(LogEventType.Error, LogFlowStd.System, LogLevel.Critical);
	}

	public static LogEvent info(int n)
	{
		LogEventError event = new LogEventError();
		event.fillMember("nErrorId", n);
		return event;
	}
}
