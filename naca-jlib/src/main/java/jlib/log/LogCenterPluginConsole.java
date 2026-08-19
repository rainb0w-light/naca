/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.log;


import jlib.misc.StringUtil;
import jlib.xml.Tag;
import org.w3c.dom.Element;


/**
 * @author U930DI
 *
 */
public class LogCenterPluginConsole extends LogCenter
{
    private static int nLineId = 0;

    /** Resets the line coutner. */
    public static void resetLineCoutner()
    {
        nLineId = 0;
    }

    /** Creates a new log center plugin console instance. */
    public LogCenterPluginConsole(LogCenterLoader logCenterLoader)
    {
        super(logCenterLoader);
    }

    /** Loads the specifics entries. */
    public void loadSpecificsEntries(Element el)
    {
    }

    /** Returns the and inc line. */
    public static String getAndIncLine()
    {
        int n = nLineId;
        nLineId++;
        return StringUtil.FormatWithFill4LeftZero(n);
    }

    /** Loads the specifics entries. */
    public void loadSpecificsEntries(Tag tagLogCenter)
    {
        csFormat = tagLogCenter.getVal("Format");
    }

    boolean open()
    {
        return true;
    }

    boolean closeLogCenter()
    {
        return true;
    }

    void preSendOutput()
    {
    }

    void sendOutput(LogParams logParam)
    {
        String csOut = patternLayout.format(logParam, 0);
        if(pluginMarker != null)
        {
            LogEventType logEventType = logParam.getLogEventType();
            if (logEventType == LogEventType.Error) {
                pluginMarker.error("(0) [Error] " + getAndIncLine() + " " + csDecoratedFileNameSource + csOut);
            } else if (logEventType == LogEventType.Warning) {
                pluginMarker.warn("(0) [warning] " + getAndIncLine() + " " + csDecoratedFileNameSource + csOut);
            } else {
                pluginMarker.info("(0) [Info] " + getAndIncLine() + " " + csDecoratedFileNameSource + csOut);
            }
        }
    }

    void postSendOutput()
    {
    }


    String getFormat()
    {
        return csFormat;
    }

    private String csFormat = null;

    public String getType()
    {
        return "LogCenterPluginConsole";
    }

    /** Sets the plugin marker. */
    public void setPluginMarker(BasePluginMarker pluginMarker, String csFileNameSource, boolean bInfo, boolean bWarning, boolean bError)
    {
        this.pluginMarker = pluginMarker;
        this.isinfo = bInfo;
        this.iswarning = bWarning;
        this.iserror = bError;
        csDecoratedFileNameSource = "%" + csFileNameSource + "% ";
    }

    private BasePluginMarker pluginMarker = null;
    private String csDecoratedFileNameSource = null;
    boolean isinfo = false;
    boolean iswarning = false;
    boolean iserror = false;
}
