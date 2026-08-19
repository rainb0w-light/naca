/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.log;


import jlib.xml.Tag;
import org.w3c.dom.Element;


/**
 * @author U930DI
 *
 */
public class LogCenterConsole extends LogCenter
{
    /** Creates a new log center console instance. */
    public LogCenterConsole(LogCenterLoader logCenterLoader)
    {
        super(logCenterLoader);
    }

    /** Loads the specifics entries. */
    public void loadSpecificsEntries(Element el)
    {
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

    protected void sendOutput(LogParams logParam)
    {
        String csOut = patternLayout.format(logParam, 0);
        System.out.println(csOut);
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
        return "LogCenterConsole";
    }
}
