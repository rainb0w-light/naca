/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
// D:\dev\AdSpiderDriving\Logging\StdInfo.java
// STModuleGen generated JLib log class

package jlib.log.stdEvents;

import jlib.log.Log;
import jlib.log.LogEvent;
import jlib.log.LogEventType;
import jlib.log.LogFlowStd;
import jlib.log.LogLevel;

/**
 * @-deprecated Use {@link EventRemark} instead.
 * @author PJD
 */
public class StdInfo extends LogEvent
{
    /** Creates a new std info instance. */
    public StdInfo(String csProduct)
    {
        super(LogEventType.Remark, LogFlowStd.Trace, LogLevel.Verbose, csProduct);
    }

    /** Executes the log operation. */
    public static LogEvent log(String csChannel, String csContext, String csMessage)
    {
        return StdInfo.log(csChannel, null, csContext, csMessage);
    }

    /** Executes the log operation. */
    public static LogEvent log(String csChannel, String csProduct, String csContext, String csMessage)
    {
        StdInfo event = new StdInfo(csProduct);
        event.fillMember("Context", csContext);
        Log.log(csChannel, event, csMessage);
        return event;
    }
}
