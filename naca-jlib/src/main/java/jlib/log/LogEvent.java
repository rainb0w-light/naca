/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.log;

import java.util.ArrayList;

/**
 * Events are raised by applications to signal any particular circumstance,
 * and are sent to the {@link Log} listener, which will broadcast the
 * event to all active {@link LogCenter} instances.
 * Events are caracterized by:
 * <ul>
 * 	<li>Its name (see {@link #getName} and {@link #getShortName}). If not specified,
 * 	the default value is the event class (either <i>LogEvent</i>, either the
 * 	name of the extending class.</li>
 * 	<li>Its type (see {@link #getLogEventType}). This characteristic is mandatory.</li>
 *  <li>Its level (see {@link #getLogLevel}). This characteristic is mandatory. </li>
 * 	<li>Its flow (see {@link #getLogFlow}). This characteristic is mandatory.
 * 	<li>The product it refers to (see {@link #getProduct}). If not specified, the
 * 	LogCenter will use its default product instead.</li>
 * 	<li>The process it refers to (see {@link #getProcess}). If not specified, the
 * 	LogCenter will use its default Process instead.</li>
 * 	<li>A variable list of parameters (see {@link #fillMember}). It can be
 * 	empty if there are no parameters associated with the event.</li>
 * </ul>
 * Different constructors allow to specify the characteristics of the event. Once
 * created, the event cannot be modified.<p/>
 * Events are accepted by the log centers under two conditions:
 * <ol>
 * 	<li>The event <i>level</i> must be high enough.</li>
 * 	<li>The log center must be interested by the event <i>flow</i>.</li>
 * <ol>
 * Additionally, the log center must be listening to the channel specified
 * by calling the {@link Log#log} method.
 * @author u930di
 */
public class LogEvent
{
	public LogEvent(LogEventType logEventType, LogFlow logFlow, LogLevel logLevel, String csProduct, String csProcess, String csName) {
		logEventType = logEventType;
		logLevel = logLevel;
		logFlow = logFlow;
		csProduct = csProduct;
		csProcess = csProcess;
		csName = csName;
	}

	public LogEvent(LogEventType logEventType, LogFlow logFlow, LogLevel logLevel, String csProduct, String csProcess) {
		logEventType = logEventType;
		logLevel = logLevel;
		logFlow = logFlow;
		csProduct = csProduct;
		csProcess = csProcess;
	}

	public LogEvent(LogEventType logEventType, LogFlow logFlow, LogLevel logLevel, String csProduct)
	{
		logEventType = logEventType;
		logFlow = logFlow;
		logLevel = logLevel;
		csProduct = csProduct;
	}
	
	public LogEvent(LogEventType logEventType, LogFlow logFlow, LogLevel logLevel)
	{
		logEventType = logEventType;
		logFlow = logFlow;
		logLevel = logLevel;
	}
	
	public LogEvent(LogFlow logFlow, LogLevel logLevel)
	{
		logEventType = LogEventType.Remark; 
		logFlow = logFlow;
		logLevel = logLevel;
	}

	public void setLogLevel(LogLevel logLevel)
	{
		logLevel = logLevel;
	}

	public LogLevel getLogLevel()
	{
		return logLevel;
	}
	
	public LogEventType getLogEventType()
	{
		return logEventType;
	}
	
	public void setLogFlow(LogFlow logFlow)
	{
		logFlow = logFlow;
	}

	public LogFlow getLogFlow()
	{
		return logFlow;
	}

	public void setName(String csName)
	{
		csName=csName;
	}

	public String getName()
	{
		if (csName==null) 
			csName = getClass().getName();
		return csName;
	}
	
	String getShortName()
	{
		if (csName==null) 
			csName = getClass().getName();
		int n = csName.lastIndexOf(".");
		if (n<0)
			return csName;
		else
			return csName.substring(n+1);
	}

	public void fillMember(String csName, String csValue)
	{
		if(arrLogInfoMembers == null)
			arrLogInfoMembers = new ArrayList<LogInfoMember>();
		LogInfoMember logInfomember = new LogInfoMember(csName, csValue);
		arrLogInfoMembers.add(logInfomember);
	}
	
	public void fillMember(String csName, int nValue)
	{
		if(arrLogInfoMembers == null)
			arrLogInfoMembers = new ArrayList<LogInfoMember>();
		LogInfoMember logInfomember = new LogInfoMember(csName, nValue);
		arrLogInfoMembers.add(logInfomember);
	}

	public String getAsString()
	{
		String cs = "" ;
		if (csProduct != null && !csProduct.equals(""))
		{
			cs = "Product="+csProduct+" ; " ;
		}
		if(arrLogInfoMembers != null)
		{
			int nNbMembers = arrLogInfoMembers.size();
			for(int n=0; n<nNbMembers; n++)
			{				
				LogInfoMember logInfoMember = arrLogInfoMembers.get(n);
				cs += "; "+ logInfoMember.getAsString();
			}
			return cs;
		}
		return cs ;
	}
	
	String getTextAsString(int n)
	{
		if(arrLogInfoMembers != null)
		{
			int nNbMembers = arrLogInfoMembers.size();
			if(n < nNbMembers)
			{
				LogInfoMember logInfoMember = arrLogInfoMembers.get(n);
				String cs = logInfoMember.getAsString();
				return cs;
			}
		}
		return null;
	}
	
	String getItemValue(int n)
	{
		if(arrLogInfoMembers != null)
		{
			int nNbMembers = arrLogInfoMembers.size();
			if(n < nNbMembers)
			{
				LogInfoMember logInfoMember = arrLogInfoMembers.get(n);
				String cs = logInfoMember.getValue();
				return cs;
			}
		}
		return null;
	}
	
	LogInfoMember getParamInfoMember(int n)
	{
		if(arrLogInfoMembers != null)
		{
			int nNbMembers = arrLogInfoMembers.size();
			if(n < nNbMembers)
			{
				LogInfoMember logInfoMember = arrLogInfoMembers.get(n);
				return logInfoMember;
			}
		}
		return null;
	}
	
	int getNbParamInfoMember()
	{
		if(arrLogInfoMembers != null)
		{
			int nNbMembers = arrLogInfoMembers.size();
			return nNbMembers;
		}
		return 0;
	}
	
	String getProduct()
	{
		return csProduct;
	}

	String getProcess()
	{
		return csProcess;
	}
	
	private String csName = null;
	private LogEventType logEventType = null;
	private LogLevel logLevel = null;
	private LogFlow logFlow = null;
	private String csProduct = null;
	private String csProcess = null;
	private ArrayList<LogInfoMember> arrLogInfoMembers = null;
}
