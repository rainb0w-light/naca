/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
// D:\dev\AdSpiderDriving\Logging\LoadConfigCompleted.java
// STModuleGen generated JLib log class

package jlib.log.stdEvents;

import jlib.log.Log;
import jlib.log.LogEvent;
import jlib.log.LogEventType;
import jlib.log.LogFlowStd;
import jlib.log.LogLevel;


/** Provides load config completed behavior. */
public class LoadConfigCompleted extends LogEvent
{
    /** Creates a new load config completed instance. */
    public LoadConfigCompleted(String csProduct)
    {
        super(LogEventType.Remark, LogFlowStd.Monitoring, LogLevel.Normal, csProduct);
    }

    /** Executes the log operation. */
    public static LogEvent log(String csChannel, String csProduct, String csMessage)
    {
        LoadConfigCompleted event = new LoadConfigCompleted(csProduct);
        Log.log(csChannel, event, csMessage);
        return event;
    }

    /** Executes the log operation. */
    public static LogEvent log(String csChannel, String csMessage)
    {
        return LoadConfigCompleted.log(csChannel, null, csMessage);
    }
}
