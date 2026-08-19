/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.log;

import jlib.misc.StringUtil;

/**
 * @author PJD
 *
 */
public class PatternLayoutConsole extends LogPatternLayout
{
    /** Creates a new pattern layout console instance. */
    public PatternLayoutConsole(String csFormat)
    {
        super();
        this.csFormat = csFormat;
    }

    String getMessage(LogParams logParams)
    {
        return logParams.getMessage();
    }

    String format(LogParams logParams, int n)
    {
        String cs = csFormat;
        cs = StringUtil.replace(cs, "%Message", logParams.toString(), true);
        cs = StringUtil.replace(cs, "%ThreadName", logParams.getThreadName(), true);
        cs = StringUtil.replace(cs, "%ThreadId", logParams.getThreadId(), true);
        cs = StringUtil.replace(cs, "%StartTime", logParams.getStartTime(), true);
        cs = StringUtil.replace(cs, "%Timestamp", logParams.getDisplayTimestamp(), true);
        cs = StringUtil.replace(cs, "%CR", "\n", true);
        cs = StringUtil.replace(cs, "%LF", "\r", true);
        return cs;
    }

    int getNbLoop(LogParams logParams)
    {
        return 1;
    }

    private String csFormat = null;
}
