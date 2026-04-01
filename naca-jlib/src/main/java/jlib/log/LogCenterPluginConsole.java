/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.log;


/*
 * Created on 3 mars 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import jlib.misc.StringUtil;
import jlib.xml.*;

import org.w3c.dom.Element;

/**
 * @author U930DI
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LogCenterPluginConsole extends LogCenter
{
	private static int nLineId = 0;
	
	public static void resetLineCoutner()
	{
		nLineId = 0;
	}

	public LogCenterPluginConsole(LogCenterLoader logCenterLoader)
	{
		super(logCenterLoader);
	}
	
	public void loadSpecificsEntries(Element el)
	{
	}
	
	public static String getAndIncLine()
	{
		int n = nLineId;
		nLineId++;
		return StringUtil.FormatWithFill4LeftZero(n);
	}
			
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
			if(logEventType == LogEventType.Error)
				pluginMarker.error("(0) [Error] " + getAndIncLine() + " " + csDecoratedFileNameSource + csOut);
			else if(logEventType == LogEventType.Warning)
				pluginMarker.warn("(0) [warning] " + getAndIncLine() + " " + csDecoratedFileNameSource + csOut);
			else
				pluginMarker.info("(0) [Info] " + getAndIncLine() + " " + csDecoratedFileNameSource + csOut);
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
	
	public void setPluginMarker(BasePluginMarker pluginMarker, String csFileNameSource, boolean bInfo, boolean bWarning, boolean bError)
	{
		pluginMarker = pluginMarker;
		bInfo = bInfo; 
		bWarning = bWarning;
		bError = bError;
		csDecoratedFileNameSource = "%" + csFileNameSource + "% ";
	}
	
	private BasePluginMarker pluginMarker = null;
	private String csDecoratedFileNameSource = null;
	boolean isinfo = false;
	boolean iswarning = false;
	boolean iserror = false;
}
