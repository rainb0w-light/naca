/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.log;

import java.util.ArrayList;
import java.util.UUID;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.SimpleType;

import jlib.jmxMBean.CompositeDataDesc;
import jlib.jmxMBean.CompositeTypeDesc;

/**
 * Class receiving {@link LogEvent}s and storing them into a media.
 * The media depends on the actual implementation of the class. Known implementations
 * are:
 * <ul>
 * 	<li>{@link jlib.log.LogCenterConsole}, sends the events to the console.</li>
 * 	<li>{@link jlib.log.LogCenterDb}, sends the events to a database.</li>
 * 	<li>{@link jlib.log.LogCenterDbFlat}, sends the events to a COP/LOG compatible database.</li>
 * 	<li>{@link jlib.log.LogCenterFile}, sends the events to a file.</li>
 * </ul>
 * @author PJD
 */
public abstract class LogCenter extends LogCenterCloseMBeam // LogCenterOpenMBean   
{
/**
 * The <i>LogCenter</i> instance is initialized based on the information a 
 * <i>LogCenterLoader</i> has collected from a [LogCenter] section of the JLib.log
 * xml configuration file.
 * This configuration file is originally provided to the {@link Log#open} method,
 * which is the method that initializes the {@link Log} static instance containing
 * the collection of <i>LogCenter</i>s. 
 * @param logCenterLoader A xml reader which has loaded a [LogCenter] section
 * of the JLib.log xml configuration file.
 */
	public LogCenter(LogCenterLoader logCenterLoader)
	{
		super(logCenterLoader.csName + " (" + logCenterLoader.csMode + ")", "Log center Open MBean");
		
		bEnable = logCenterLoader.bEnable;
		csChannel = logCenterLoader.csChannel;
		logLevel = new LogLevel(logCenterLoader.logLevel);
		csMode = logCenterLoader.csMode;
		logFlow = logCenterLoader.logFlow;
		nNbRequestBufferSize = logCenterLoader.nNbRequestBufferSize;
		arrLogParamItem = new ArrayList<LogParams>();
	}
	
	public void setPatternLayout(LogPatternLayout patternLayout)
	{
		patternLayout = patternLayout;
	}	
	
	protected LogPatternLayout patternLayout = null;

	protected boolean bEnable = false;                    // If not enabled, the log center doesn't accept any event.
	protected String csChannel = null;
	protected LogFlow logFlow = null;
	protected LogLevel logLevel = new LogLevel(LogLevel.Normal);

	protected String csMode = "";
	protected String csProcess = null;
	protected String csProduct = null;
	protected String csRunId = null;
	protected String csRuntimeId = null;

	private ArrayList<LogParams> arrLogParamItem = null;

	protected int nNbRequestBufferSize = 0;
	protected boolean bAsync = false;
/**
 * Receives the description of a {@link LogEvent} (wrapped up in a
 * {@link LogParams} decorator}, checks if the event
 * meet the conditions to be accepted by the <i>LogCenter</i>, in which
 * case it stores it.
 * The three conditions are:
 * <ul>
 * 	<li>The event is sent on the channel the <i>LogCenter</i> is listening to.</li>
 * 	<li>The event has a {@link LogFlow} accepted by the <i>LogCenter</i> flow (see protected
 * 	property {@link logFlow}).</li>
 * 	<li>The event has a {@link LogLevel} equal or higher than the
 * 	minimal required by the <i>LogCenter</i> (see property {@link getLevel}.</li> 
 * </ul>
 * If the events buffer is enabled, the event description is added to it,
 * instead of immediately stored.
 * @param logParam The description of the event to be logged.
 */
	void output(LogParams logParam)
	{
		if(bOpen && bEnable)
		{
			boolean b = false;
			if(logParam.csChannel == null)
				b = true;
			else if(logParam.csChannel.equalsIgnoreCase(csChannel))
				b = true;
			if(b)
			{
				b = logParam.isAcceptable(logLevel, logFlow);
				if(b)
				{
					if(nNbRequestBufferSize == 0)	// No buffering
					{
						preSendOutput();
						sendOutput(logParam);
						postSendOutput();
					}
					else
						addLogParamItem(logParam);
				}
			}
		}
	}
/**
 * Add the description of one {@link LogEvent} to the events buffer.
 * If the events buffer size outpasses its limit, the buffer is flushed
 * via {@link outputAllLogParamsItems}, and all events are stored.
 * @param logParam The description of the event to be stored
 * in the events buffer.
 */
	synchronized void addLogParamItem(LogParams logParam)
	{
		arrLogParamItem.add(logParam);
		if(arrLogParamItem.size() >= nNbRequestBufferSize)
		{
			outputAllLogParamsItems();
		}
	}
/**
 * Empties the events buffer.
 * The emptying sequence is the following:
 * <ul>
 * 	<li></li>
 * 	<li></li>
 * 	<li></li>
 * 	<li></li>
 * </ul> 
 */	
	synchronized void flushCachedLogParamsItems()
	{
		if(arrLogParamItem != null)
			outputAllLogParamsItems();
	}
	
	synchronized private void outputAllLogParamsItems()
	{
		int nNbEntries = arrLogParamItem.size();
		preSendOutput();
		for(int n=0; n<nNbEntries; n++)
		{
			LogParams logParam = arrLogParamItem.get(n);
			sendOutput(logParam);
		}
		postSendOutput();
		arrLogParamItem.clear();
	}
	
	abstract boolean open();
/**
 * Method called before storing one or more events via {@link sendOutput}.
 * This method can be implemented at extending the class to perform any
 * customized action before storing the event(s). If no action is
 * required, use the following code to disable the method:
 * <pre>
 * 	void preSendOutput(){}
 * </pre>
 * When the events buffer is being flushed, this method is called only once,
 * before the first event is sent to be stored. 
 */
	abstract void preSendOutput();
/**
 * Method called by {@link output} for immediately store an event.
 * If the buffer is being flushed, this method is called once for each
 * event in the buffer.<p/>
 * This method has to be implemented by extending the <i>LogCenter</i> class.
 * @param logParam The parameters of the {@link LogEvent} to be processed.
 */
	abstract void sendOutput(LogParams logParam);

/**
 * Method called after storing one or more events via {@link sendOutput}.
 * This method can be implemented at extending the class to perform any
 * customized action after storing the event(s). If no action is
 * required, use the following code to disable the method:
 * <pre>
 * 	void postSendOutput(){}
 * </pre>
 * When the events buffer is being flushed, this method is called only once,
 * after the first event has beem sent to be stored. 
 */
	abstract void postSendOutput();
	
	abstract boolean closeLogCenter();
	
	boolean isOpen()
	{
		return bOpen;
	}
	
	boolean doOpen()
	{
		if(!bOpen)
			bOpen = open();
		return bOpen;
	}
	
	
	boolean close()
	{
		if(bOpen)
		{
			flushCachedLogParamsItems();
			bOpen = !closeLogCenter();
		}
		return !bOpen;
	}	
	
	private boolean bOpen = false;
/**
 * Returns <i>true</i> if the <i>LogCenter</i> is enabled.
 * A disabled <i>LogCenter</i> doesn't accept any {@link LogEvent}. An
 * enabled <i>LogCenter</i> accepts events provided three conditions are
 * met:
 * <ul>
 * 	<li>The event is sent on the channel the <i>LogCenter</i> is listening to.</li>
 * 	<li>The event has a {@link LogFlow} accepted by the <i>LogCenter</i> flow (see protected
 * 	property {@link #logFlow}).</li>
 * 	<li>The event has a {@link LogLevel} equal or higher than the
 * 	minimal required by the <i>LogCenter</i> (see property {@link #getLevel}.</li> 
 * </ul>
 * @return <i>true</i> if the <i>LogCenter</i> is enabled.
 */	
	public Boolean getEnable()
	{
		return bEnable;
	}
/**
 * Enables the <i>LogCenter</i>.
 * A disabled <i>LogCenter</i> doesn't accept any {@link LogEvent}. An
 * enabled <i>LogCenter</i> accepts events provided three conditions are
 * met:
 * <ul>
 * 	<li>The event is sent on the channel the <i>LogCenter</i> is listening to.</li>
 * 	<li>The event has a {@link LogFlow} accepted by the <i>LogCenter</i> flow (see protected
 * 	property {@link #logFlow}).</li>
 * 	<li>The event has a {@link LogLevel} equal or higher than the
 * 	minimal required by the <i>LogCenter</i> (see property {@link #getLevel}.</li> 
 * </ul>
 * @param b If <i>true</i>, it enables the <i>LogCenter</i>. Otherwise
 * it disables it.
 */	
	public void setEnable(Boolean b)
	{
		bEnable = b;
	}
/**
 * Returns the current {@link LogLevel} of the <i>LogCenter</i> as a string.
 * @return The current {@link LogLevel} of the <i>LogCenter</i> as a string.
 */	
	public String getLevel()
	{
		return logLevel.getAsString();
	}
/**
 * Sets the minimal level required for the {@link LogEvent}s to be
 * accepted by the <i>LogCenter</i>.
 * @param csLevel The minimal required level. Provided level should be
 * one of the accepted by {@link LogLevel}.
 */	
	public void setLevel(String csLevel)
	{
		logLevel.set(csLevel);
	}
	
/**
 * Sets the minimal level required for the {@link LogEvent}s to be
 * accepted by the <i>LogCenter</i> to {@link LogLevel#Critical}. 
 */	
	public void setCritical()
	{
		setLevel("Critical");
	}
	
/**
 * Sets the minimal level required for the {@link LogEvent}s to be
 * accepted by the <i>LogCenter</i> to {@link LogLevel#Important}. 
 */	
	public void setImportant()
	{
		setLevel("Important");
	}	
	
/**
 * Sets the minimal level required for the {@link LogEvent}s to be
 * accepted by the <i>LogCenter</i> to {@link LogLevel#Normal}. 
 */	
	public void setNormal()
	{
		setLevel("Normal");
	}
	
/**
 * Sets the minimal level required for the {@link LogEvent}s to be
 * accepted by the <i>LogCenter</i> to {@link LogLevel#Verbose}. 
 */	
	public void setVerbose()
	{
		setLevel("Verbose");
	}
	
/**
 * Sets the minimal level required for the {@link LogEvent}s to be
 * accepted by the <i>LogCenter</i> to {@link LogLevel#Debug}. 
 */	
	public void setDebug()
	{
		setLevel("Debug");
	}
	
/**
 * Sets the minimal level required for the {@link LogEvent}s to be
 * accepted by the <i>LogCenter</i> to {@link LogLevel#FineDebug}. 
 */	
	public void setFineDebug()
	{
		setLevel("FineDebug");
	}
	
	public CompositeType getStateType()
	{
		CompositeTypeDesc compositeTypeDesc = new CompositeTypeDesc("LogCenterCompositeType", "LogCenterCompositeType Desc");
		compositeTypeDesc.addItem("Enable", "EnableDesc", SimpleType.BOOLEAN);
		compositeTypeDesc.addItem("Level", "LevelDesc", SimpleType.STRING);
		return compositeTypeDesc.generateCompositeType();
	}
			
	public CompositeData getState()
	{
		CompositeType compositeType = getStateType();
		CompositeDataDesc compositeDataDesc = new CompositeDataDesc(compositeType);
		compositeDataDesc.setItemValue("Enable", getEnable());
		compositeDataDesc.setItemValue("Level", getLevel());
		return compositeDataDesc.generateCompositeData();
	}
	
	public void setState(CompositeData data)
	{
		int n = 0;
	}

	public String getChannel() 
	{
		return csChannel;
	}

	public void setRunId(String csRunId) 
	{
		csRunId=csRunId;
	}

	public String getRunId() 
	{
		if (csRunId==null) 
		{
			csRunId=UUID.randomUUID().toString();
		}
		return csRunId;
	}

	public void setRuntimeId(String csRuntimeId)
	{
		csRuntimeId=csRuntimeId;		
	}

	public String getRuntimeId() 
	{
		if (csRuntimeId==null) 
		{
			csRuntimeId=UUID.randomUUID().toString();
		}
		return csRuntimeId;
	}
/**
 * Sets the default product for the <i>LogCenter</i>
 * @param csProduct
 */
	public void setProduct(String csProduct) 
	{
		csProduct=csProduct;
	}

	public String getProduct()
	{
		return csProduct;
	}

	public void setProcess(String csProcess)
	{
		csProcess=csProcess;
	}

	public String getProcess()
	{
		return csProcess;
	}
	
	public abstract String getType();
}