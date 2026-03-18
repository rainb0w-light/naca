/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.log;


import jlib.misc.DateUtil;
import jlib.misc.StringUtil;

/*
 * Created on 23 juin 2005
 */

/**
 * Class to decorate a {@link LogEvent} instance with some additional properties.
 * Additional properties are:
 * <ul>
 * 	<li>A text message.</li>
 * 	<li>The channer where the message is sent to.</li>
 * 	<li>Information about the file, the class, the method and the thread
 * 	where the event has been created.</li> 
 * </ul>
 * In some special cases it is useful to specify:
 * <ul>
 * 	<li>The <i>RunId</i> identifier.</li>
 * 	<li>The <i>RuntimeId</i> identifier.</li>
 * </ul>
 * See the {@link Log} class overview for more details about those identifiers.
 */
public class LogParams
{
/**
 * Class constructor.
 * @param csChannel The channel where the event is to be sent.
 * @param logEvent The event itself.
 * @param csMessage A text message with additional description about the event.
 */
	LogParams(String csChannel, LogEvent logEvent, String csMessage)
	{
		csChannel = csChannel;
		logEvent = logEvent;
		csMessage = csMessage;
		lStartTime = Log.getRunningTime_ms();
		Thread thread = Thread.currentThread();
		lThreadId = thread.getId();
		csThreadName = thread.getName();
		csTimestamp = DateUtil.getDisplayTimeStamp();
		csRunId = null;
		csRuntimeId = null;
	}
/**
 * Class constructor specifying the <i>RunId</i> and <i>RuntimeId</i> identifiers.
 * See the {@link Log} class overview for more details about these identifiers.
 * @param csChannel The channel where the event is to be sent.
 * @param logEvent The event itself.
 * @param csMessage A text message with additional description about the event.
 * @param csRunId 
 * @param csRuntimeId
 */
	LogParams(String csChannel, LogEvent logEvent, String csMessage, String csRunId, String csRuntimeId)
	{
		csChannel = csChannel;
		logEvent = logEvent;
		csMessage = csMessage;
		lStartTime = Log.getRunningTime_ms();
		Thread thread = Thread.currentThread();
		lThreadId = thread.getId();
		csThreadName = thread.getName();
		csTimestamp = DateUtil.getDisplayTimeStamp();
		csRunId = csRunId;
		csRuntimeId = csRuntimeId;
	}

	LogEventType getLogEventType()
	{
		return logEvent.getLogEventType();
	}
	
	String getDisplayTimestamp()
	{
		return csTimestamp;	
	}
	
	boolean isAcceptable(LogLevel minLogLevel, LogFlow logFlow)
	{
		if(logFlow.isAcceptable(logEvent.getLogFlow()))
		{
			if(logEvent.getLogLevel().isGreaterOrEqual(minLogLevel))
				return true;
		}
		return false;
	}
	
	public String toString()
	{
		String cs = "";
		if (!StringUtil.isEmpty(getMessage()))
		{
			cs += getMessage();
		} 
		cs += logEvent.getAsString();
		return cs;
	}
	
	public String getTextItem(int n)
	{
		return logEvent.getTextAsString(n);
	}
	
	public String getItemValue(int n)
	{
		return logEvent.getItemValue(n);
	}
	
	void fillAppCallerLocation(CallStackExclusion callStackExclusion)
	{
		Throwable th = new Throwable();
		StackTraceElement tStack[]  = th.getStackTrace(); 
		int nNbEntries = tStack.length;
		for(int n=0; n<nNbEntries; n++)
		{
			String csClassName = tStack[n].getClassName();
			if(callStackExclusion.doNotContains(csClassName))
			{
				caller = tStack[n];
				return;
			}
		}
		caller = null;
	}
	
	long getThreadId()
	{
		return lThreadId;
	}
	
	String getThreadName()
	{
		return csThreadName;
	}
	
	long getStartTime()
	{
		return lStartTime;
	}
	
	String getType()
	{
		return logEvent.getLogEventType().getType();
	}
	
	String getFile()
	{
		String fileName=null;
		if(caller != null)
			fileName = caller.getFileName();

// If application is compiled without debug information, the file name
// is not available:
		if (fileName==null)
			fileName="N/A";

		return fileName;
	}
	
	String getMethod()
	{
		String method = null;
		if(caller != null)
			method = caller.getMethodName();
// If application is compiled without debug information, the method name
// is not available:
		if (method==null)
			method="N/A";
		return method;
	}

	int getLine()
	{
		if(caller != null)
			return caller.getLineNumber();
		return 0;
	}
	
	String getEventName()
	{
		return logEvent.getName();		
	}
	
	String getShortEventName()
	{
		String cs = logEvent.getName();
		int n = cs.lastIndexOf('.');
		if(n > 0)
		{
			cs = cs.substring(n+1);
		}
		return cs;
	}
	
	int getEventId()
	{
		String cs = logEvent.getName();
		int nNbParam = getNbParamInfoMember();
		cs += nNbParam;
		for(int n=0; n<nNbParam; n++)
		{
			LogInfoMember info = getParamInfoMember(n);
			String csParamId = info.getName();
			cs += csParamId;			
		}
		return cs.hashCode();
	}
	
	String getMessage()
	{
		if(csMessage != null)
			return csMessage;
		return "";
	}
	
	LogInfoMember getParamInfoMember(int n)
	{
		return logEvent.getParamInfoMember(n);
	}
	
	int getNbParamInfoMember()
	{
		int n = logEvent.getNbParamInfoMember();
		if(n > 10)
			n = 10;
		return n;
	}
	
	String getProduct()
	{
		String csProduct = logEvent.getProduct();
		return csProduct;
	}

	String csChannel = null;
	LogEvent logEvent = null;
	String csMessage = null;
	StackTraceElement caller = null;	
	String csThreadName;
	long lThreadId = 0;
	long lStartTime = 0;
	String csTimestamp = null;
	String csRunId;
	String csRuntimeId;
}
 