/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
// D:\dev\AdSpiderDriving\Logging\LoadConfigStart.java
// STModuleGen generated JLib log class

package jlib.log.stdEvents;

import jlib.log.Log;
import jlib.log.LogEvent;
import jlib.log.LogEventType;
import jlib.log.LogFlowStd;
import jlib.log.LogLevel;

/** Provides load config start behavior. */
public class LoadConfigStart extends LogEvent
{
    /** Creates a new load config start instance. */
    public LoadConfigStart(String csProduct)
    {
        super(LogEventType.Remark, LogFlowStd.Monitoring, LogLevel.Important, csProduct);
    }

    /** Executes the log operation. */
    public static LogEvent log(String csChannel, String csName, String csMessage)
    {
        return LoadConfigStart.log(csChannel, null, csName, csMessage);
    }

    /** Executes the log operation. */
    public static LogEvent log(String csChannel, String csProduct, String csName, String csMessage)
    {
        LoadConfigStart event = new LoadConfigStart(csProduct);
        event.fillMember("Name", csName);
        Log.log(csChannel, event, csMessage);
        return event;
    }
}
