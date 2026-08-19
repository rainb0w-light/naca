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
public class PatternLayoutRawLine extends LogPatternLayout
{
    /** Creates a new pattern layout raw line instance. */
    public PatternLayoutRawLine(String csFormat)
    {
        super();
        this.csFormat = csFormat;
    }

    String getMessage(LogParams logParams)
    {
        String csMessage = logParams.getMessage();
        return csMessage+"\r\n";
    }


    /** Executes the format operation. */
    public String format(LogParams logParams, int n)
    {
        if(n == 0)
        {
            if(StringUtil.isEmpty(csFormat))
            {
                return logParams.toString() + "\r\n";
            }
            else
            {
                String cs = csFormat;
                cs = StringUtil.replace(cs, "%FullText", logParams.toString(), true);
                cs = StringUtil.replace(cs, "%Message", logParams.getMessage(), true);
                cs = StringUtil.replace(cs, "%ThreadName", logParams.getThreadName(), true);
                cs = StringUtil.replace(cs, "%ThreadId", logParams.getThreadId(), true);
                cs = StringUtil.replace(cs, "%StartTime", logParams.getStartTime(), true);
                cs = StringUtil.replace(cs, "%Timestamp", logParams.getDisplayTimestamp(), true);
                cs = StringUtil.replace(cs, "%CR", "\r", true);
                cs = StringUtil.replace(cs, "%LF", "\n", true);
                return cs;
            }
        }

        return null;
    }

    int getNbLoop(LogParams logParams)
    {
        return 1;
    }

    private String csFormat = null;
}
